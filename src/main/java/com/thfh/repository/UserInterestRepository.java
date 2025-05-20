package com.thfh.repository;

import com.thfh.model.User;
import com.thfh.model.UserInterest;
import com.thfh.model.InterestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户兴趣关联数据访问层
 */
@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    /**
     * 根据用户查询所有兴趣
     * @param user 用户实体
     * @return 用户兴趣关联列表
     */
    List<UserInterest> findByUser(User user);
    
    /**
     * 根据用户ID查询所有兴趣
     * @param userId 用户ID
     * @return 用户兴趣关联列表
     */
     List<UserInterest> findByUserId(Long userId);

     /**
      * 根据用户ID列表查询所有兴趣
      * @param userIds 用户ID列表
      * @return 用户兴趣关联列表
      */
     List<UserInterest> findByUserIdIn(List<Long> userIds);

     /**
     * 根据用户和兴趣类型查询是否存在
     * @param user 用户实体
     * @param interestType 兴趣类型
     * @return 是否存在该关联
     */
    boolean existsByUserAndInterestType(User user, InterestType interestType);
    
    /**
     * 根据用户和兴趣类型删除关联
     * @param user 用户实体
     * @param interestType 兴趣类型
     */
    void deleteByUserAndInterestType(User user, InterestType interestType);
    
    /**
     * 删除用户的所有兴趣关联
     * @param user 用户实体
     */
    void deleteByUser(User user);
} 