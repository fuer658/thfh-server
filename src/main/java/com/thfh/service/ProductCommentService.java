package com.thfh.service;

import com.thfh.dto.ProductCommentDTO;
import com.thfh.model.ProductComment;
import com.thfh.model.ProductCommentLike;
import com.thfh.model.User;
import com.thfh.repository.ProductCommentLikeRepository;
import com.thfh.repository.ProductCommentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;

/**
 * 商品评论服务
 */
@Service
public class ProductCommentService {
    @Autowired
    private ProductCommentRepository productCommentRepository;
    @Autowired
    private ProductCommentLikeRepository productCommentLikeRepository;
    @Autowired
    private UserService userService;

    /**
     * 用户评论商品
     */
    public ProductCommentDTO commentProductDTO(Long productId, String content, Long parentId, List<String> images) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未授权，请先登录");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "评论内容不能为空");
        }
        if (parentId != null) {
            ProductComment parent = productCommentRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "父评论不存在"));
            if (parent.getParentId() != null) {
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "只允许二级评论，不能回复二级评论");
            }
        }
        ProductComment comment = new ProductComment();
        comment.setProductId(productId);
        comment.setUserId(currentUser.getId());
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setCreateTime(java.time.LocalDateTime.now());
        comment.setLikeCount(0);
        comment.setImages(images);
        comment = productCommentRepository.save(comment);
        return toDTO(comment);
    }

    /**
     * 获取商品评论树（只返回两级结构）
     */
    public Page<ProductCommentDTO> getProductCommentTree(Long productId, PageRequest pageRequest) {
        // 查询一级评论
        Page<ProductComment> rootPage = productCommentRepository.findByProductIdAndParentIdIsNull(productId, pageRequest);
        List<ProductComment> rootComments = rootPage.getContent();
        List<Long> rootIds = rootComments.stream().map(ProductComment::getId).collect(Collectors.toList());
        // 查询所有二级评论
        List<ProductComment> secondLevel = rootIds.isEmpty() ? Collections.emptyList() : productCommentRepository.findByParentIdIn(rootIds);
        // 按parentId分组
        Map<Long, List<ProductCommentDTO>> secondMap = secondLevel.stream()
                .map(this::toDTO)
                .collect(Collectors.groupingBy(ProductCommentDTO::getParentId));
        // 组装树结构
        List<ProductCommentDTO> tree = rootComments.stream().map(root -> {
            ProductCommentDTO dto = toDTO(root);
            dto.setReplies(secondMap.getOrDefault(dto.getId(), Collections.emptyList()));
            return dto;
        }).collect(Collectors.toList());
        return new PageImpl<>(tree, pageRequest, rootPage.getTotalElements());
    }

    /**
     * 获取商品评论扁平列表
     */
    public Page<ProductCommentDTO> getProductComments(Long productId, PageRequest pageRequest) {
        Page<ProductComment> page = productCommentRepository.findByProductIdAndParentIdIsNull(productId, pageRequest);
        List<ProductCommentDTO> dtos = page.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageRequest, page.getTotalElements());
    }

    /**
     * 点赞商品评论
     */
    public Integer likeProductComment(Long commentId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未授权，请先登录");
        }
        ProductComment comment = productCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        if (productCommentLikeRepository.existsByCommentIdAndUserId(commentId, currentUser.getId())) {
            throw new BusinessException(ErrorCode.CONFLICT, "已经点赞过该评论");
        }
        ProductCommentLike like = new ProductCommentLike();
        like.setCommentId(commentId);
        like.setUserId(currentUser.getId());
        like.setCreatedAt(java.time.LocalDateTime.now());
        productCommentLikeRepository.save(like);
        comment.setLikeCount(comment.getLikeCount() + 1);
        productCommentRepository.save(comment);
        return comment.getLikeCount();
    }

    /**
     * 取消点赞商品评论
     */
    public Integer unlikeProductComment(Long commentId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未授权，请先登录");
        }
        ProductComment comment = productCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        if (!productCommentLikeRepository.existsByCommentIdAndUserId(commentId, currentUser.getId())) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "未点赞该评论");
        }
        productCommentLikeRepository.deleteByCommentIdAndUserId(commentId, currentUser.getId());
        comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        productCommentRepository.save(comment);
        return comment.getLikeCount();
    }

    /**
     * 检查用户是否已点赞商品评论
     */
    public Boolean isProductCommentLiked(Long commentId, Long userId) {
        return productCommentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    /**
     * 用户删除自己评论
     */
    public void deleteProductComment(Long commentId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未授权，请先登录");
        }
        ProductComment comment = productCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有权限删除该评论");
        }
        List<ProductComment> children = productCommentRepository.findByParentIdIn(Collections.singletonList(commentId));
        productCommentRepository.deleteAll(children);
        productCommentRepository.delete(comment);
    }

    /**
     * 管理员删除评论
     */
    public void deleteProductCommentByAdmin(Long commentId) {
        ProductComment comment = productCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        List<ProductComment> children = productCommentRepository.findByParentIdIn(Collections.singletonList(commentId));
        productCommentRepository.deleteAll(children);
        productCommentRepository.delete(comment);
    }

    /**
     * 根据评论ID查询评论DTO
     */
    public ProductCommentDTO findById(Long commentId) {
        Optional<ProductComment> commentOpt = productCommentRepository.findById(commentId);
        return commentOpt.map(this::toDTO).orElse(null);
    }

    /**
     * 分页查询用户的所有评论
     */
    public Page<ProductCommentDTO> getCommentsByUser(Long userId, PageRequest pageRequest) {
        Page<ProductComment> page = productCommentRepository.findByUserId(userId, pageRequest);
        List<ProductCommentDTO> dtos = page.getContent().stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(dtos, pageRequest, page.getTotalElements());
    }

    /**
     * 用户对自己评论追加追评
     */
    public ProductCommentDTO appendProductComment(Long commentId, String content, List<String> images) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未授权，请先登录");
        }
        ProductComment mainComment = productCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        if (!mainComment.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有权限追加评论");
        }
        if (mainComment.getAppendCommentId() != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "该评论已追评");
        }
        if (mainComment.getParentId() != null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "只允许对一级评论追评");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "追评内容不能为空");
        }
        // 创建追评评论
        ProductComment append = new ProductComment();
        append.setProductId(mainComment.getProductId());
        append.setUserId(currentUser.getId());
        append.setContent(content);
        append.setParentId(mainComment.getId()); // 追评的parentId指向主评论
        append.setCreateTime(java.time.LocalDateTime.now());
        append.setLikeCount(0);
        append.setImages(images);
        append = productCommentRepository.save(append);
        // 更新主评论的appendCommentId
        mainComment.setAppendCommentId(append.getId());
        productCommentRepository.save(mainComment);
        return toDTO(append);
    }

    /**
     * 实体转DTO
     */
    private ProductCommentDTO toDTO(ProductComment comment) {
        ProductCommentDTO dto = new ProductCommentDTO();
        BeanUtils.copyProperties(comment, dto);
        // 补充用户信息
        User user = userService.getUserById(comment.getUserId());
        if (user != null) {
            dto.setUserName(user.getUsername());
            dto.setUserNickname(user.getRealName());
            dto.setUserAvatar(user.getAvatar());
        }
        // 仅一级评论返回追评内容
        if (comment.getParentId() == null && comment.getAppendCommentId() != null) {
            ProductComment append = productCommentRepository.findById(comment.getAppendCommentId()).orElse(null);
            if (append != null) {
                dto.setAppendComment(toDTO(append));
            }
        }
        return dto;
    }
} 