package com.thfh.service;

import com.thfh.dto.UserInterestDTO;
import com.thfh.model.InterestType;
import com.thfh.model.User;
import com.thfh.model.UserInterest;
import com.thfh.repository.UserInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户兴趣服务类
 * 提供用户兴趣相关的业务逻辑处理
 */
@Service
public class UserInterestService {
    @Autowired
    private UserInterestRepository userInterestRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户的所有兴趣
     * @param userId 用户ID
     * @return 用户兴趣DTO
     */
    public UserInterestDTO getUserInterests(Long userId) {
        User user = userService.getUserById(userId);
        List<UserInterest> userInterests = userInterestRepository.findByUser(user);
        
        UserInterestDTO dto = new UserInterestDTO();
        dto.setUserId(userId);
        dto.setInterests(userInterests.stream()
                .map(UserInterest::getInterestType)
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    /**
     * 获取当前登录用户的兴趣
     * @return 用户兴趣DTO
     */
    public UserInterestDTO getCurrentUserInterests() {
        User currentUser = userService.getCurrentUser();
        return getUserInterests(currentUser.getId());
    }
    
    /**
     * 更新用户兴趣
     * @param dto 用户兴趣DTO
     * @return 更新后的用户兴趣DTO
     */
    @Transactional
    public UserInterestDTO updateUserInterests(UserInterestDTO dto) {
        User user = userService.getUserById(dto.getUserId());
        
        // 检查权限
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getId().equals(user.getId())) {
            throw new RuntimeException("没有权限修改其他用户的兴趣");
        }
        
        // 删除现有兴趣
        userInterestRepository.deleteByUser(user);
        
        // 添加新兴趣
        List<UserInterest> newInterests = new ArrayList<>();
        if (dto.getInterests() != null) {
            for (InterestType interestType : dto.getInterests()) {
                UserInterest interest = new UserInterest();
                interest.setUser(user);
                interest.setInterestType(interestType);
                newInterests.add(interest);
            }
            userInterestRepository.saveAll(newInterests);
        }
        
        return getUserInterests(user.getId());
    }
    
    /**
     * 获取所有可用的兴趣类型
     * @return 兴趣类型列表
     */
    public List<InterestType> getAllInterestTypes() {
        return Arrays.asList(InterestType.values());
    }
} 