package com.thfh.service;

import com.thfh.dto.CourseTagDTO;
import com.thfh.model.CourseTag;
import com.thfh.repository.CourseTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseTagService {
    
    @Autowired
    private CourseTagRepository courseTagRepository;
    
    /**
     * 创建新标签
     *
     * @param name 标签名称
     * @param description 标签描述
     * @return 创建的标签对象
     */
    @Transactional
    public CourseTag createTag(String name, String description) {
        // 检查标签名是否已存在
        if (courseTagRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("标签名已存在");
        }
        
        CourseTag tag = new CourseTag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setEnabled(true);
        return courseTagRepository.save(tag);
    }
    
    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    public List<CourseTag> getAllTags() {
        return courseTagRepository.findAll();
    }
    
    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签对象
     */
    public Optional<CourseTag> getTagById(Long id) {
        return courseTagRepository.findById(id);
    }
    
    /**
     * 根据名称获取标签
     *
     * @param name 标签名称
     * @return 标签对象
     */
    public CourseTag getTagByName(String name) {
        return courseTagRepository.findByName(name)
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
    public Optional<CourseTag> updateTag(Long id, String name, String description) {
        return courseTagRepository.findById(id).map(tag -> {
            tag.setName(name);
            tag.setDescription(description);
            return courseTagRepository.save(tag);
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
    public Optional<CourseTag> setTagEnabled(Long id, boolean enabled) {
        return courseTagRepository.findById(id).map(tag -> {
            tag.setEnabled(enabled);
            return courseTagRepository.save(tag);
        });
    }
    
    /**
     * 删除标签
     */
    @Transactional
    public void deleteTag(Long id) {
        courseTagRepository.deleteById(id);
    }
    
    /**
     * 更新标签
     */
    @Transactional
    public CourseTag updateTag(Long id, CourseTag updatedTag) {
        CourseTag tag = courseTagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));
        
        // 如果要更新的名称已存在于其他标签，则抛出异常
        courseTagRepository.findByName(updatedTag.getName())
                .ifPresent(existingTag -> {
                    if (!existingTag.getId().equals(id)) {
                        throw new IllegalArgumentException("标签名已存在");
                    }
                });
        
        tag.setName(updatedTag.getName());
        tag.setDescription(updatedTag.getDescription());
        return courseTagRepository.save(tag);
    }
    
    /**
     * 处理课程标签
     * 根据传入的标签DTO集合，查找或创建对应的标签实体
     *
     * @param tagDTOs 标签DTO集合
     * @return 标签实体集合
     */
    @Transactional
    public Set<CourseTag> processTags(Set<CourseTagDTO> tagDTOs) {
        Set<CourseTag> tags = new HashSet<>();
        
        for (CourseTagDTO tagDTO : tagDTOs) {
            CourseTag tag;
            
            if (tagDTO.getId() != null) {
                // 如果有ID，尝试查找现有标签
                tag = courseTagRepository.findById(tagDTO.getId())
                        .orElseGet(() -> createNewTag(tagDTO));
            } else if (tagDTO.getName() != null && !tagDTO.getName().trim().isEmpty()) {
                // 如果没有ID但有名称，尝试按名称查找或创建
                Optional<CourseTag> existingTag = courseTagRepository.findByName(tagDTO.getName());
                tag = existingTag.orElseGet(() -> createNewTag(tagDTO));
            } else {
                // 如果既没有ID也没有名称，跳过
                continue;
            }
            
            tags.add(tag);
        }
        
        return tags;
    }
    
    /**
     * 根据DTO创建新标签
     */
    private CourseTag createNewTag(CourseTagDTO tagDTO) {
        CourseTag tag = new CourseTag();
        tag.setName(tagDTO.getName());
        tag.setDescription(tagDTO.getDescription());
        tag.setEnabled(true);
        return courseTagRepository.save(tag);
    }
}
