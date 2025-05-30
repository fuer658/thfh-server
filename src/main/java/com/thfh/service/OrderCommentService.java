package com.thfh.service;

import com.thfh.dto.OrderCommentDTO;
import com.thfh.model.CommentLike;
import com.thfh.model.Order;
import com.thfh.model.OrderComment;
import com.thfh.model.User;
import com.thfh.repository.CommentLikeRepository;
import com.thfh.repository.OrderCommentRepository;
import com.thfh.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单评价服务
 */
@Service
public class OrderCommentService {

    @Autowired
    private OrderCommentRepository orderCommentRepository;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单评价
     * @param orderId 订单ID
     * @param content 评价内容
     * @param images 评价图片URL列表
     * @param video 评价视频URL
     * @param score 评分
     * @return 创建的评价
     */
    @Transactional
    public OrderComment createComment(Long orderId, String content, List<String> images, String video, Integer score) {
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        // 获取订单信息
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 检查订单是否属于当前用户
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("无权评价该订单");
        }

        // 检查订单状态是否为已完成
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("订单未完成，无法评价");
        }

        // 检查是否已评价
        if (orderCommentRepository.existsByOrderIdAndUserId(orderId, currentUser.getId())) {
            throw new RuntimeException("该订单已评价");
        }

        // 检查评分范围
        if (score < 1 || score > 10) {
            throw new RuntimeException("评分必须在1-10分之间");
        }

        // 创建评价
        OrderComment comment = new OrderComment();
        comment.setOrder(order);
        comment.setUser(currentUser);
        comment.setContent(content);
        comment.setImages(images != null ? String.join(",", images) : null);
        comment.setVideo(video);
        comment.setScore(score);
        comment.setLikeCount(0);

        return orderCommentRepository.save(comment);
    }

    /**
     * 获取订单的评价列表
     * @param orderId 订单ID
     * @return 评价列表
     */
    @Transactional(readOnly = true)
    public List<OrderCommentDTO> getCommentsByOrderId(Long orderId) {
        List<OrderComment> comments = orderCommentRepository.findByOrderId(orderId);
        return comments.stream()
                .map(this::toOrderCommentDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取艺术品的评价分页
     * @param artworkId 艺术品ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评价分页
     */
    @Transactional(readOnly = true)
    public Page<OrderCommentDTO> getCommentsByArtworkId(Long artworkId, int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<OrderComment> commentPage = orderCommentRepository.findByArtworkId(artworkId, pageRequest);
        return commentPage.map(this::toOrderCommentDTO);
    }

    /**
     * 获取用户的评价分页
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评价分页
     */
    @Transactional(readOnly = true)
    public Page<OrderCommentDTO> getCurrentUserComments(int pageNum, int pageSize) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<OrderComment> commentPage = orderCommentRepository.findByUserId(currentUser.getId(), pageRequest);
        return commentPage.map(this::toOrderCommentDTO);
    }
    
    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @Transactional
    public int likeComment(Long commentId) {
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 检查评论是否存在
        OrderComment comment = orderCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        // 检查是否已点赞
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, currentUser.getId())) {
            throw new RuntimeException("您已点赞过该评论");
        }
        
        // 创建点赞记录
        CommentLike like = new CommentLike();
        like.setComment(comment);
        like.setUser(currentUser);
        commentLikeRepository.save(like);
        
        // 更新评论点赞数
        comment.setLikeCount(comment.getLikeCount() + 1);
        orderCommentRepository.save(comment);
        
        return comment.getLikeCount();
    }
    
    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @Transactional
    public int unlikeComment(Long commentId) {
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 检查评论是否存在
        OrderComment comment = orderCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        // 查找点赞记录
        CommentLike like = commentLikeRepository.findByCommentIdAndUserId(commentId, currentUser.getId());
        if (like == null) {
            throw new RuntimeException("您未点赞过该评论");
        }
        
        // 删除点赞记录
        commentLikeRepository.delete(like);
        
        // 更新评论点赞数
        comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        orderCommentRepository.save(comment);
        
        return comment.getLikeCount();
    }
    
    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    @Transactional(readOnly = true)
    public boolean isCommentLiked(Long commentId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, currentUser.getId());
    }

    /**
     * 实体转DTO
     */
    public OrderCommentDTO toOrderCommentDTO(OrderComment comment) {
        if (comment == null) return null;
        
        OrderCommentDTO dto = new OrderCommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setImages(comment.getImages() != null ? 
                Arrays.asList(comment.getImages().split(",")) : null);
        dto.setVideo(comment.getVideo());
        dto.setScore(comment.getScore());
        dto.setCreateTime(comment.getCreateTime());
        dto.setUpdateTime(comment.getUpdateTime());
        dto.setLikeCount(comment.getLikeCount());
        
        // 检查当前用户是否已点赞
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            dto.setLiked(commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), currentUser.getId()));
        } else {
            dto.setLiked(false);
        }

        // 设置用户信息
        if (comment.getUser() != null) {
            dto.setUser(userService.convertToDTO(comment.getUser()));
        }

        // 设置订单信息
        if (comment.getOrder() != null) {
            dto.setOrder(orderService.toOrderDTO(comment.getOrder()));
        }

        return dto;
    }
} 