package com.thfh.service;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkScore;
import com.thfh.model.User;
import com.thfh.repository.ArtworkRepository;
import com.thfh.repository.ArtworkScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.librec.conf.Configuration;
import net.librec.data.DataModel;
import net.librec.data.model.TextDataModel;
import net.librec.recommender.Recommender;
import net.librec.recommender.RecommenderContext;
import net.librec.recommender.cf.UserKNNRecommender;
import net.librec.recommender.item.RecommendedItem;
import net.librec.similarity.PCCSimilarity;
import net.librec.similarity.RecommenderSimilarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.HashSet;

/**
 * 推荐系统服务
 * 使用LibRec库实现作品推荐功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final ArtworkScoreRepository artworkScoreRepository;
    private final ArtworkRepository artworkRepository;
    private final UserService userService;

    @Value("${recommendation.data.path:./recommendation-data}")
    private String dataPath;

    @Value("${recommendation.rebuild.interval:48}") // 默认48小时重建一次推荐模型
    private int rebuildInterval;

    private Recommender recommender;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * 初始化推荐系统，并设置定时任务定期重建推荐模型
     */
    @PostConstruct
    public void init() {
        try {
            // 创建数据目录
            File dataDir = new File(dataPath);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // 初始启动时构建模型
            rebuildRecommendationModel();

            // 设置定时任务，定期重建推荐模型
            scheduler.scheduleAtFixedRate(
                this::rebuildRecommendationModel,
                rebuildInterval,
                rebuildInterval,
                TimeUnit.HOURS
            );
        } catch (Exception e) {
            log.error("初始化推荐系统失败", e);
        }
    }

    /**
     * 重建推荐模型
     */
    public synchronized void rebuildRecommendationModel() {
        try {
            log.info("开始重建推荐模型...");
            
            // 生成评分数据文件
            File ratingFile = new File(dataPath + "/ratings.txt");
            generateRatingDataFile(ratingFile);
            
            // 检查文件是否为空
            if (ratingFile.length() == 0 || !hasEnoughRatings()) {
                log.warn("评分数据不足或文件为空，无法构建推荐模型");
                return;
            }
            
            // 配置LibRec
            Configuration conf = new Configuration();
            conf.set("dfs.data.dir", dataPath);
            conf.set("data.input.path", "ratings.txt");
            conf.set("rec.recommender.similarity.key", "user");
            conf.set("rec.neighbors.knn.number", "50");
            conf.set("rec.recommender.isranking", "true");
            conf.set("rec.recommender.ranking.topn", "100");
            
            // 创建数据模型
            DataModel dataModel = new TextDataModel(conf);
            dataModel.buildDataModel();
            
            // 设置相似度计算方法
            RecommenderSimilarity similarity = new PCCSimilarity();
            similarity.buildSimilarityMatrix(dataModel);
            
            // 创建推荐上下文
            RecommenderContext context = new RecommenderContext(conf, dataModel, similarity);
            
            // 创建推荐器
            recommender = new UserKNNRecommender();
            recommender.setContext(context);
            
            // 构建推荐模型
            recommender.recommend(context);
            
            log.info("推荐模型重建完成");
        } catch (Exception e) {
            log.error("重建推荐模型失败", e);
        }
    }

    /**
     * 检查是否有足够的评分数据
     * @return 是否有足够的评分数据
     */
    private boolean hasEnoughRatings() {
        long ratingCount = artworkScoreRepository.count();
        // 推荐系统至少需要一定数量的评分数据才能有效工作
        // 用户数量和作品数量都应该至少有2个以上
        long userCount = artworkScoreRepository.countDistinctUser();
        long artworkCount = artworkScoreRepository.countDistinctArtwork();
        
        log.info("当前评分数据: 总评分数={}, 用户数={}, 作品数={}", ratingCount, userCount, artworkCount);
        
        return ratingCount >= 5 && userCount >= 2 && artworkCount >= 2;
    }

    /**
     * 生成评分数据文件
     * @param file 输出文件
     * @throws IOException IO异常
     */
    private void generateRatingDataFile(File file) throws IOException {
        List<ArtworkScore> scores = artworkScoreRepository.findAll();
        
        try (FileWriter writer = new FileWriter(file)) {
            for (ArtworkScore score : scores) {
                writer.write(score.getUser().getId() + " " + score.getArtwork().getId() + " " + score.getScore() + "\n");
            }
        }
    }

    /**
     * 为当前用户推荐作品
     * @param pageable 分页参数
     * @return 推荐作品列表
     */
    public Page<Artwork> getRecommendedArtworks(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        List<Artwork> finalRecommendedArtworks = new ArrayList<>();
        Set<Long> addedArtworkIds = new HashSet<>(); // 用于去重

        // 1. 尝试个性化推荐 (LibRec)
        if (currentUser != null && recommender != null && hasEnoughRatings()) {
            try {
                List<RecommendedItem> allRecommendedItems = recommender.getRecommendedList();

                if (allRecommendedItems != null && !allRecommendedItems.isEmpty()) {
                    String currentUserIdStr = currentUser.getId().toString();
                    
                    List<RecommendedItem> userSpecificItems = allRecommendedItems.stream()
                            .filter(item -> currentUserIdStr.equals(item.getUserId()))
                            .limit(pageable.getPageSize() * 2) 
                            .collect(Collectors.toList());

                    List<Long> artworkIds = userSpecificItems.stream()
                            .map(item -> Long.parseLong(item.getItemId()))
                            .collect(Collectors.toList());

                    if (!artworkIds.isEmpty()) {
                        List<Artwork> personalizedArtworks = artworkRepository.findByIdInAndEnabledTrue(artworkIds);
                        
                        Map<Long, Artwork> artworkMap = personalizedArtworks.stream()
                                .collect(Collectors.toMap(Artwork::getId, artwork -> artwork));
                        
                        for (RecommendedItem item : userSpecificItems) {
                            Artwork artwork = artworkMap.get(Long.parseLong(item.getItemId()));
                            if (artwork != null && addedArtworkIds.add(artwork.getId())) {
                                finalRecommendedArtworks.add(artwork);
                                if (finalRecommendedArtworks.size() >= pageable.getPageSize() * 1.5) break; 
                            }
                        }
                        log.debug("为用户 {} 生成个性化推荐 {} 条", currentUserIdStr, finalRecommendedArtworks.size());
                    }
                }
            } catch (Exception e) {
                log.error("获取个性化推荐列表失败 for user " + (currentUser != null ? currentUser.getId() : "null") + ". Error: " + e.getMessage(), e);
            }
        }

        // 2. 如果个性化推荐不足或未登录用户，则混合其他推荐策略
        // 2.1 编辑精选 (Curated Artworks)
        if (finalRecommendedArtworks.size() < pageable.getPageSize()) {
            int curatedNeeded = 0;
            if (finalRecommendedArtworks.isEmpty()) { 
                curatedNeeded = (int)(pageable.getPageSize() * 0.5);
            } else { 
                curatedNeeded = Math.max(0, (int)(pageable.getPageSize() * 0.3) - finalRecommendedArtworks.size());
            }
            if (finalRecommendedArtworks.size() < pageable.getPageSize() / 2 && curatedNeeded < pageable.getPageSize() / 4 && pageable.getPageSize() >= 4) {
                curatedNeeded = Math.max(1, pageable.getPageSize() / 4); // Ensure at least 1 if page size is small, or 1/4th
            }
            
            if (curatedNeeded > 0) {
                 Page<Artwork> curatedPage = getCuratedArtworks(PageRequest.of(0, curatedNeeded + 3)); 
                 for (Artwork artwork : curatedPage.getContent()) {
                    if (addedArtworkIds.add(artwork.getId()) && finalRecommendedArtworks.size() < pageable.getPageSize()) {
                        finalRecommendedArtworks.add(artwork);
                    }
                }
                log.debug("已添加编辑精选作品，当前总数: {}", finalRecommendedArtworks.size());
            }
        }

        // 2.2 热门作品 (Popular Artworks)
        if (finalRecommendedArtworks.size() < pageable.getPageSize()) {
            int popularNeeded = pageable.getPageSize() - finalRecommendedArtworks.size();
            if (popularNeeded > 0) {
                Page<Artwork> popularPage = getPopularArtworks(PageRequest.of(0, popularNeeded + 3)); 
                for (Artwork artwork : popularPage.getContent()) {
                    if (addedArtworkIds.add(artwork.getId()) && finalRecommendedArtworks.size() < pageable.getPageSize()) {
                        finalRecommendedArtworks.add(artwork);
                    }
                }
                log.debug("已添加热门作品，当前总数: {}", finalRecommendedArtworks.size());
            }
        }

        // 2.3 最新作品 (Latest Artworks)
        if (finalRecommendedArtworks.size() < pageable.getPageSize()) {
            int latestNeeded = pageable.getPageSize() - finalRecommendedArtworks.size();
            if (latestNeeded > 0) {
                Page<Artwork> latestPage = getLatestArtworks(PageRequest.of(0, latestNeeded + 3));
                for (Artwork artwork : latestPage.getContent()) {
                    if (addedArtworkIds.add(artwork.getId()) && finalRecommendedArtworks.size() < pageable.getPageSize()) {
                        finalRecommendedArtworks.add(artwork);
                    }
                }
                log.debug("已添加最新作品，当前总数: {}", finalRecommendedArtworks.size());
            }
        }
        
        if (finalRecommendedArtworks.isEmpty()) {
            log.warn("所有推荐策略均未获取到作品，将返回空列表或尝试一个基础查询");
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), finalRecommendedArtworks.size());
        start = Math.min(start, end); 

        List<Artwork> pageContent;
        if (start >= finalRecommendedArtworks.size() || start > end ) { 
             pageContent = new ArrayList<>();
        } else {
            pageContent = finalRecommendedArtworks.subList(start, end);
        }
        
        return new PageImpl<>(pageContent, pageable, finalRecommendedArtworks.size());
    }

    /**
     * 获取热门作品（评分高且浏览量大的作品）
     * @param pageable 分页参数
     * @return 热门作品列表
     */
    public Page<Artwork> getPopularArtworks(Pageable pageable) {
        return artworkRepository.findByEnabledTrueOrderByAverageScoreDescViewCountDesc(pageable);
    }

    /**
     * 获取最新作品
     * @param pageable 分页参数
     * @return 最新作品列表
     */
    public Page<Artwork> getLatestArtworks(Pageable pageable) {
        return artworkRepository.findByEnabledTrueOrderByCreateTimeDesc(pageable);
    }

    /**
     * 获取编辑精选作品
     * @param pageable 分页参数
     * @return 编辑精选作品列表
     */
    public Page<Artwork> getCuratedArtworks(Pageable pageable) {
        return artworkRepository.findByEnabledTrueAndRecommendedTrueOrderByUpdateTimeDesc(pageable);
    }
} 