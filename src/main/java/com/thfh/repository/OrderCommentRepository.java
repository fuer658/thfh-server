package com.thfh.repository;

import com.thfh.model.OrderComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 订单评价数据访问接口
 */
public interface OrderCommentRepository extends JpaRepository<OrderComment, Long> {
    
    /**
     * 根据订单ID查询评价
     * @param orderId 订单ID
     * @return 评价列表
     */
    List<OrderComment> findByOrderId(Long orderId);

    /**
     * 根据艺术品ID分页查询评价
     * @param artworkId 艺术品ID
     * @param pageable 分页参数
     * @return 评价分页列表
     */
    @Query("SELECT c FROM OrderComment c WHERE c.order.artwork.id = :artworkId")
    Page<OrderComment> findByArtworkId(@Param("artworkId") Long artworkId, Pageable pageable);

    /**
     * 根据用户ID分页查询评价
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 评价分页列表
     */
    Page<OrderComment> findByUserId(Long userId, Pageable pageable);

    /**
     * 检查用户是否已对订单进行评价
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否存在评价
     */
    boolean existsByOrderIdAndUserId(Long orderId, Long userId);
} 