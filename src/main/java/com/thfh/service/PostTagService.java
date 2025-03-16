package com.thfh.service;

import com.thfh.model.PostTag;
import com.thfh.repository.PostTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostTagService {
    
    @Autowired
    private PostTagRepository postTagRepository;
    
    /**
     * 创建新标签
     *
     * @param name 标签名称
     * @param description 标签描述
     * @return 创建的标签对象
     */
    @Transactional
    public PostTag createTag(String name, String description) {
        // 检查标签名是否已存在
        if (postTagRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("标签名已存在");
        }
        
        PostTag tag = new PostTag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setEnabled(true);
        return postTagRepository.save(tag);
    }
    
    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    public List<PostTag> getAllTags() {
        return postTagRepository.findAll();
    }
    
    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签对象
     */
    public Optional<PostTag> getTagById(Long id) {
        return postTagRepository.findById(id);
    }
    
    /**
     * 根据名称获取标签
     *
     * @param name 标签名称
     * @return 标签对象
     */
    public PostTag getTagByName(String name) {
        return postTagRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));
    }
    
    /**
     * 更新标签
     *
     * @param id 标签ID
     * @param name 新的标签名称
     * @param description 新的标签描述
     * @return 更新后的标签对象
     */
    @Transactional
    public Optional<PostTag> updateTag(Long id, String name, String description) {
        return postTagRepository.findById(id).map(tag -> {
            tag.setName(name);
            tag.setDescription(description);
            return postTagRepository.save(tag);
        });
    }
    
    /**
     * 启用或禁用标签
     *
     * @param id 标签ID
     * @param enabled 是否启用
     * @return 更新后的标签对象
     */
    @Transactional
    public Optional<PostTag> setTagEnabled(Long id, boolean enabled) {
        return postTagRepository.findById(id).map(tag -> {
            tag.setEnabled(enabled);
            return postTagRepository.save(tag);
        });
    }
    
    /**
     * 删除标签
     */
    @Transactional
    public void deleteTag(Long id) {
        postTagRepository.deleteById(id);
    }
    
    /**
     * 更新标签
     */
    @Transactional
    public PostTag updateTag(Long id, PostTag updatedTag) {
        PostTag tag = postTagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));
        
        // 如果要更新的名称已存在于其他标签，则抛出异常
        postTagRepository.findByName(updatedTag.getName())
                .ifPresent(existingTag -> {
                    if (!existingTag.getId().equals(id)) {
                        throw new IllegalArgumentException("标签名已存在");
                    }
                });
        
        tag.setName(updatedTag.getName());
        tag.setDescription(updatedTag.getDescription());
        return postTagRepository.save(tag);
    }
} 