package com.thfh.service;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkScore;
import com.thfh.model.User;
import com.thfh.repository.ArtworkRepository;
import com.thfh.repository.ArtworkScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.data.DataModel;
import net.librec.data.model.TextDataModel;
import net.librec.filter.GenericRecommendedFilter;
import net.librec.recommender.Recommender;
import net.librec.recommender.RecommenderContext;
import net.librec.recommender.cf.UserKNNRecommender;
import net.librec.recommender.item.RecommendedItem;
import net.librec.similarity.PCCSimilarity;
import net.librec.similarity.RecommenderSimilarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
        
        if (currentUser == null) {
            // 未登录用户返回热门作品
            log.debug("未登录用户，返回热门作品");
            return getPopularArtworks(pageable);
        }
        
        try {
            if (recommender == null) {
                // 如果推荐器未初始化，返回热门作品
                log.debug("推荐器未初始化，返回热门作品");
                return getPopularArtworks(pageable);
            }
            
            // 获取推荐列表 - 使用实际可用的API
            List<RecommendedItem> recommendedItems = null;
            try {
                recommendedItems = recommender.getRecommendedList();
            } catch (Exception e) {
                log.error("获取推荐列表失败", e);
                return getPopularArtworks(pageable);
            }
            
            if (recommendedItems == null || recommendedItems.isEmpty()) {
                log.debug("推荐列表为空，返回热门作品");
                return getPopularArtworks(pageable);
            }
            
            // 过滤出当前用户的推荐项
            String userId = currentUser.getId().toString();
            recommendedItems = recommendedItems.stream()
                    .filter(item -> userId.equals(item.getUserId()))
                    .limit(pageable.getPageSize() * 2)
                    .collect(Collectors.toList());
                    
            if (recommendedItems.isEmpty()) {
                log.debug("当前用户没有个性化推荐项，返回热门作品");
                return getPopularArtworks(pageable);
            }
            
            // 提取作品ID列表
            List<Long> artworkIds = recommendedItems.stream()
                    .map(item -> Long.parseLong(item.getItemId()))
                    .collect(Collectors.toList());
            
            // 获取作品信息
            List<Artwork> artworks = artworkRepository.findByIdInAndEnabledTrue(artworkIds);
            
            if (artworks.isEmpty()) {
                log.debug("推荐作品列表为空，返回热门作品");
                return getPopularArtworks(pageable);
            }
            
            // 按推荐顺序排序
            List<Artwork> sortedArtworks = new ArrayList<>();
            Map<Long, Artwork> artworkMap = artworks.stream()
                    .collect(Collectors.toMap(Artwork::getId, artwork -> artwork, (a, b) -> a));
                    
            for (RecommendedItem item : recommendedItems) {
                Long artworkId = Long.parseLong(item.getItemId());
                if (artworkMap.containsKey(artworkId)) {
                    sortedArtworks.add(artworkMap.get(artworkId));
                }
            }
            
            // 处理分页
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), sortedArtworks.size());
            
            if (start >= sortedArtworks.size()) {
                return Page.empty(pageable);
            }
            
            List<Artwork> pageContent = sortedArtworks.subList(start, end);
            return new PageImpl<>(pageContent, pageable, sortedArtworks.size());
            
        } catch (Exception e) {
            log.error("获取推荐作品失败", e);
            return getPopularArtworks(pageable);
        }
    }

    /**
     * 获取热门作品（评分高且浏览量大的作品）
     * @param pageable 分页参数
     * @return 热门作品列表
     */
    public Page<Artwork> getPopularArtworks(Pageable pageable) {
        return artworkRepository.findByEnabledTrueOrderByAverageScoreDescViewCountDesc(pageable);
    }
} 