package com.thfh.service;

import com.thfh.dto.PostDTO;
import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;
import com.thfh.model.Post;
import com.thfh.model.PostTag;
import com.thfh.model.User;
import com.thfh.model.UserInterest;
import com.thfh.repository.PostBrowseHistoryRepository;
import com.thfh.repository.PostLikeRepository;
import com.thfh.repository.PostRepository;
import com.thfh.repository.UserInterestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态推荐服务
 * 基于用户兴趣、浏览历史和行为实现个性化推荐
 */
@Slf4j
@Service
public class PostRecommendationService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostBrowseHistoryRepository postBrowseHistoryRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInterestRepository userInterestRepository;

    @Autowired
    private PostService postService;
    
    /**
     * 获取推荐动态列表
     * 推荐策略:
     * 1. 基于用户兴趣标签
     * 2. 基于用户浏览历史
     * 3. 基于用户点赞行为
     * 4. 热门动态（高浏览量、点赞数、评论数）
     * 5. 近期新发布动态
     *
     * @param pageable 分页参数
     * @return 推荐动态DTO分页列表
     */
    public Page<PostDTO> getRecommendedPosts(Pageable pageable) {
        try {
            // 获取当前登录用户
            User currentUser = userService.getCurrentUser();
            Long userId = currentUser.getId();
            
            // 计算各类推荐动态并进行综合排序
            List<Post> recommendedPosts = getRecommendedPostsForUser(userId);
            
            // 处理分页
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), recommendedPosts.size());
            Page<Post> postPage;
            if (start <= end) {
                postPage = new PageImpl<>(recommendedPosts.subList(start, end), pageable, recommendedPosts.size());
            } else {
                postPage = new PageImpl<>(Collections.emptyList(), pageable, recommendedPosts.size());
            }
            
            // 转换为DTO
            return postPage.map(postService::toPostDTO);
        } catch (Exception e) {
            log.error("获取推荐动态发生错误", e);
            // 发生错误时返回最新动态作为兜底推荐
            return getFallbackRecommendations(pageable);
        }
    }
    
    /**
     * 获取指定用户的推荐动态列表
     *
     * @param userId 用户ID
     * @return 推荐的动态列表
     */
    private List<Post> getRecommendedPostsForUser(Long userId) {
        // 创建结果收集器，使用Map保证每个动态只出现一次
        Map<Long, RecommendedPost> recommendationMap = new HashMap<>();
        
        // 1. 基于用户兴趣标签查找动态
        List<Post> interestBasedPosts = getInterestBasedPosts(userId);
        for (Post post : interestBasedPosts) {
            RecommendedPost rp = recommendationMap.getOrDefault(post.getId(), new RecommendedPost(post));
            rp.setInterestScore(10.0); // 兴趣标签匹配的得分权重
            recommendationMap.put(post.getId(), rp);
        }
        
        // 2. 基于用户浏览历史推荐相似动态
        List<Post> historyBasedPosts = getHistoryBasedPosts(userId);
        for (Post post : historyBasedPosts) {
            RecommendedPost rp = recommendationMap.getOrDefault(post.getId(), new RecommendedPost(post));
            rp.setHistoryScore(8.0); // 浏览历史相关的得分权重
            recommendationMap.put(post.getId(), rp);
        }
        
        // 3. 基于用户点赞行为推荐相似动态
        List<Post> likeBasedPosts = getLikeBasedPosts(userId);
        for (Post post : likeBasedPosts) {
            RecommendedPost rp = recommendationMap.getOrDefault(post.getId(), new RecommendedPost(post));
            rp.setLikeScore(7.0); // 点赞行为相关的得分权重
            recommendationMap.put(post.getId(), rp);
        }
        
        // 4. 热门动态
        List<Post> hotPosts = getHotPosts();
        for (Post post : hotPosts) {
            RecommendedPost rp = recommendationMap.getOrDefault(post.getId(), new RecommendedPost(post));
            // 热门指数得分，根据浏览量、点赞数、评论数计算
            double hotScore = calculateHotScore(post);
            rp.setHotScore(hotScore);
            recommendationMap.put(post.getId(), rp);
        }
        
        // 5. 近期新发布动态
        List<Post> recentPosts = getRecentPosts();
        for (Post post : recentPosts) {
            RecommendedPost rp = recommendationMap.getOrDefault(post.getId(), new RecommendedPost(post));
            // 新鲜度得分，越新得分越高
            double freshnessScore = calculateFreshnessScore(post);
            rp.setFreshnessScore(freshnessScore);
            recommendationMap.put(post.getId(), rp);
        }
        
        // 过滤掉已经浏览过的动态
        filterViewedPosts(userId, recommendationMap);
        
        // 计算每个动态的最终得分并排序
        List<RecommendedPost> recommendedPosts = new ArrayList<>(recommendationMap.values());
        recommendedPosts.forEach(RecommendedPost::calculateTotalScore);
        recommendedPosts.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));
        
        // 转换为Post列表
        return recommendedPosts.stream()
                .map(RecommendedPost::getPost)
                .collect(Collectors.toList());
    }
    
    /**
     * 基于用户兴趣标签查找动态
     */
    private List<Post> getInterestBasedPosts(Long userId) {
        // 获取用户兴趣标签
        List<UserInterest> userInterests = userInterestRepository.findByUserId(userId);
        if (userInterests.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 从用户兴趣中获取兴趣名称
        List<String> interestTagNames = userInterests.stream()
                .map(interest -> interest.getInterestType().getDisplayName())
                .collect(Collectors.toList());
        
        // 根据兴趣标签查找动态
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            root.fetch("tags", JoinType.LEFT);
            query.distinct(true);
            
            Join<Post, PostTag> tagJoin = root.join("tags");
            return tagJoin.get("name").in(interestTagNames);
        };
        
        // 限制最多返回50条与兴趣相关的动态
        return postRepository.findAll(spec, PageRequest.of(0, 50)).getContent();
    }
    
    /**
     * 基于用户浏览历史推荐相似动态
     */
    private List<Post> getHistoryBasedPosts(Long userId) {
        // 获取用户最近浏览的动态ID
        List<Long> recentViewedPostIds = postBrowseHistoryRepository.findRecentBrowsedPostIdsByUserId(
                userId, PageRequest.of(0, 10));
        
        if (recentViewedPostIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取这些动态的标签
        Set<Long> tagIds = new HashSet<>();
        for (Long postId : recentViewedPostIds) {
            Post post = postRepository.findById(postId).orElse(null);
            if (post != null) {
                for (PostTag tag : post.getTags()) {
                    tagIds.add(tag.getId());
                }
            }
        }
        
        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 根据这些标签查找相似的动态，但排除已浏览过的动态
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            root.fetch("tags", JoinType.LEFT);
            query.distinct(true);
            
            Join<Post, PostTag> tagJoin = root.join("tags");
            Predicate tagPredicate = tagJoin.get("id").in(tagIds);
            Predicate notInViewedPredicate = cb.not(root.get("id").in(recentViewedPostIds));
            
            return cb.and(tagPredicate, notInViewedPredicate);
        };
        
        // 限制最多返回30条相似的动态
        return postRepository.findAll(spec, PageRequest.of(0, 30)).getContent();
    }
    
    /**
     * 基于用户点赞行为推荐相似动态
     */
    private List<Post> getLikeBasedPosts(Long userId) {
        // 获取用户点赞过的动态ID列表
        List<Long> likedPostIds = getUserLikedPostIds(userId);
        
        if (likedPostIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取这些动态的标签
        Set<Long> tagIds = new HashSet<>();
        for (Long postId : likedPostIds) {
            Post post = postRepository.findById(postId).orElse(null);
            if (post != null) {
                for (PostTag tag : post.getTags()) {
                    tagIds.add(tag.getId());
                }
            }
        }
        
        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 根据这些标签查找相似的动态，但排除已点赞过的动态
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            root.fetch("tags", JoinType.LEFT);
            query.distinct(true);
            
            Join<Post, PostTag> tagJoin = root.join("tags");
            Predicate tagPredicate = tagJoin.get("id").in(tagIds);
            Predicate notInLikedPredicate = cb.not(root.get("id").in(likedPostIds));
            
            return cb.and(tagPredicate, notInLikedPredicate);
        };
        
        // 限制最多返回30条相似的动态
        return postRepository.findAll(spec, PageRequest.of(0, 30)).getContent();
    }
    
    /**
     * 获取用户点赞过的动态ID列表
     * 由于PostLikeRepository没有直接提供获取点赞动态ID的方法，这里自行实现
     */
    private List<Long> getUserLikedPostIds(Long userId) {
        // 使用已有的findPostsByUserId方法获取点赞的动态，然后提取ID
        Page<Post> likedPosts = postLikeRepository.findPostsByUserId(userId, PageRequest.of(0, 50));
        return likedPosts.getContent().stream()
                .map(Post::getId)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取热门动态
     */
    private List<Post> getHotPosts() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        
        // 查询一周内的热门动态，根据综合热度（浏览量*0.4 + 点赞数*0.3 + 评论数*0.3）排序
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            root.fetch("tags", JoinType.LEFT);
            query.distinct(true);
            
            // 一周内的动态
            return cb.greaterThan(root.get("createTime"), oneWeekAgo);
        };
        
        // 按照创建时间降序获取最多50条动态
        List<Post> posts = postRepository.findAll(spec, PageRequest.of(0, 50, 
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createTime"))).getContent();
        
        // 按热度排序
        posts.sort((a, b) -> {
            double scoreA = calculateHotScore(a);
            double scoreB = calculateHotScore(b);
            return Double.compare(scoreB, scoreA);
        });
        
        return posts;
    }
    
    /**
     * 获取近期新发布动态
     */
    private List<Post> getRecentPosts() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        
        Specification<Post> spec = (root, query, cb) -> {
            // 添加root.fetch关联tags，确保标签被加载
            root.fetch("tags", JoinType.LEFT);
            query.distinct(true);
            
            // 三天内的动态
            return cb.greaterThan(root.get("createTime"), threeDaysAgo);
        };
        
        // 按照创建时间降序获取最多30条动态
        return postRepository.findAll(spec, PageRequest.of(0, 30, 
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createTime"))).getContent();
    }
    
    /**
     * 计算动态的热门指数
     */
    private double calculateHotScore(Post post) {
        // 热度计算公式：浏览量*0.4 + 点赞数*0.3 + 评论数*0.3
        Long viewCount = post.getViewCount() != null ? post.getViewCount() : 0L;
        Integer likeCount = post.getLikeCount() != null ? post.getLikeCount() : 0;
        Integer commentCount = post.getCommentCount() != null ? post.getCommentCount() : 0;
        
        return viewCount * 0.4 + likeCount * 0.3 + commentCount * 0.3;
    }
    
    /**
     * 计算动态的新鲜度得分
     */
    private double calculateFreshnessScore(Post post) {
        // 新鲜度计算：现在距离发布时间的小时数
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createTime = post.getCreateTime();
        long hoursDiff = java.time.Duration.between(createTime, now).toHours();
        
        // 发布时间越近，得分越高
        // 72小时内的动态，新鲜度从10分开始线性衰减
        if (hoursDiff <= 72) {
            return 10.0 * (1 - hoursDiff / 72.0);
        }
        return 0;
    }
    
    /**
     * 过滤用户已浏览过的动态（可选）
     */
    private void filterViewedPosts(Long userId, Map<Long, RecommendedPost> recommendationMap) {
        // 这里可以选择性地过滤或降低已浏览过的动态的权重
        // 本实现选择保留已浏览的动态，但降低其权重
        
        // 获取用户已浏览过的动态ID
        List<Long> viewedPostIds = postBrowseHistoryRepository.findRecentBrowsedPostIdsByUserId(
                userId, PageRequest.of(0, 100));
        
        // 对于已浏览过的动态，降低其总得分
        for (Long postId : viewedPostIds) {
            if (recommendationMap.containsKey(postId)) {
                RecommendedPost rp = recommendationMap.get(postId);
                // 设置一个较低的系数，比如已浏览过的动态权重降为60%
                rp.setViewedFactor(0.6);
            }
        }
    }
    
    /**
     * 兜底推荐：如果个性化推荐失败，返回最新动态
     */
    private Page<PostDTO> getFallbackRecommendations(Pageable pageable) {
        log.info("使用兜底推荐策略：最新动态");
        
        try {
            Specification<Post> spec = (root, query, cb) -> {
                // 添加root.fetch关联tags，确保标签被加载
                if (root.getModel().getPersistenceType() == javax.persistence.metamodel.Type.PersistenceType.ENTITY) {
                    root.fetch("tags", JoinType.LEFT);
                    query.distinct(true);
                }
                return null; // 不加任何条件，获取全部动态
            };
            
            // 按创建时间降序排序
            Page<Post> postPage = postRepository.findAll(spec, 
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createTime")));
            
            // 转换为DTO
            return postPage.map(postService::toPostDTO);
        } catch (Exception e) {
            log.error("获取兜底推荐也失败", e);
            return Page.empty(pageable);
        }
    }
    
    /**
     * 推荐动态封装类，用于计算推荐得分
     */
    private static class RecommendedPost {
        private Post post;
        private double interestScore = 0;
        private double historyScore = 0;
        private double likeScore = 0;
        private double hotScore = 0;
        private double freshnessScore = 0;
        private double totalScore = 0;
        private double viewedFactor = 1.0; // 如果已浏览过，此系数会降低
        
        public RecommendedPost(Post post) {
            this.post = post;
        }
        
        public Post getPost() {
            return post;
        }
        
        public void setInterestScore(double interestScore) {
            this.interestScore = interestScore;
        }
        
        public void setHistoryScore(double historyScore) {
            this.historyScore = historyScore;
        }
        
        public void setLikeScore(double likeScore) {
            this.likeScore = likeScore;
        }
        
        public void setHotScore(double hotScore) {
            this.hotScore = hotScore;
        }
        
        public void setFreshnessScore(double freshnessScore) {
            this.freshnessScore = freshnessScore;
        }
        
        public void setViewedFactor(double viewedFactor) {
            this.viewedFactor = viewedFactor;
        }
        
        public double getTotalScore() {
            return totalScore;
        }
        
        /**
         * 计算最终的推荐得分
         */
        public void calculateTotalScore() {
            // 各项得分权重
            final double INTEREST_WEIGHT = 0.35;   // 兴趣标签匹配的权重
            final double HISTORY_WEIGHT = 0.25;    // 浏览历史相关的权重
            final double LIKE_WEIGHT = 0.15;       // 点赞行为相关的权重
            final double HOT_WEIGHT = 0.15;        // 热门程度的权重
            final double FRESHNESS_WEIGHT = 0.10;  // 新鲜度的权重
            
            // 计算综合得分
            totalScore = (interestScore * INTEREST_WEIGHT
                    + historyScore * HISTORY_WEIGHT
                    + likeScore * LIKE_WEIGHT
                    + hotScore * HOT_WEIGHT
                    + freshnessScore * FRESHNESS_WEIGHT) * viewedFactor;
        }
    }
} 