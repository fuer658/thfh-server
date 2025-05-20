package com.thfh.repository;

import com.thfh.model.UserFile;
import com.thfh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户上传文件数据访问接口
 */
@Repository
public interface UserFileRepository extends JpaRepository<UserFile, Long> {

    /**
     * 根据用户查找所有上传的文件
     * @param user 用户实体
     * @return 用户上传文件列表
     */
    List<UserFile> findByUser(User user);

    /**
     * 根据用户和文件路径查找文件
     * @param user 用户实体
     * @param filePath 文件路径
     * @return 匹配的用户文件Optional对象
     */
    Optional<UserFile> findByUserAndFilePath(User user, String filePath);

    /**
     * 根据用户和文件路径删除文件
     * @param user 用户实体
     * @param filePath 文件路径
     */
    void deleteByUserAndFilePath(User user, String filePath);

    /**
     * 检查指定用户和文件路径的文件是否存在
     * @param user 用户实体
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean existsByUserAndFilePath(User user, String filePath);
}