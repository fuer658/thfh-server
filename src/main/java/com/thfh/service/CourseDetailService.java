package com.thfh.service;

import com.thfh.exception.ResourceNotFoundException;
import com.thfh.model.CourseDetail;
import com.thfh.model.CourseChapter;
import com.thfh.model.CourseSection;
import com.thfh.model.CourseSubSection;
import com.thfh.model.Course;
import com.thfh.repository.CourseDetailRepository;
import com.thfh.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 课程详情服务
 */
@Service
public class CourseDetailService {
    
    @Autowired
    private CourseDetailRepository courseDetailRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    /**
     * 根据课程ID获取课程详情
     * 如果课程详情不存在，则自动创建一个空的详情
     * 
     * @param courseId 课程ID
     * @return 课程详情对象
     */
    @Transactional
    public CourseDetail getCourseDetailByCourseId(Long courseId) {
        // 直接检查课程是否存在
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if(!courseOpt.isPresent()) {
            throw new ResourceNotFoundException("课程不存在");
        }
        
        Optional<CourseDetail> detailOpt = courseDetailRepository.findByCourseId(courseId);
        if (!detailOpt.isPresent()) {
            // 创建新的课程详情
            CourseDetail newDetail = new CourseDetail();
            newDetail.setCourseId(courseId);
            newDetail.setCreateTime(LocalDateTime.now());
            newDetail.setUpdateTime(LocalDateTime.now());
            return courseDetailRepository.save(newDetail);
        }
        
        return detailOpt.get();
    }
    
    /**
     * 创建或更新课程详情
     * 
     * @param courseDetail 课程详情对象
     * @return 保存后的课程详情对象
     */
    @Transactional
    public CourseDetail saveCourseDetail(CourseDetail courseDetail) {
        // 检查课程是否存在
        Long courseId = courseDetail.getCourseId();
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if(!courseOpt.isPresent()) {
            throw new ResourceNotFoundException("课程不存在");
        }
        
        if (courseDetail.getUpdateTime() == null) {
            courseDetail.setUpdateTime(LocalDateTime.now());
        }
        
        return courseDetailRepository.save(courseDetail);
    }
    
    /**
     * 添加章节到课程详情
     * 
     * @param courseId 课程ID
     * @param chapter 章节对象
     * @return 保存后的章节对象
     */
    @Transactional
    public CourseChapter addChapter(Long courseId, CourseChapter chapter) {
        // 获取课程详情
        CourseDetail courseDetail = getCourseDetailByCourseId(courseId);
        
        // 设置章节所属的课程详情
        chapter.setCourseDetail(courseDetail);
        
        // 设置章节顺序（放在最后）
        if (chapter.getOrderIndex() == null) {
            int lastIndex = courseDetail.getChapters().size();
            chapter.setOrderIndex(lastIndex);
        }
        
        // 添加章节到课程详情
        courseDetail.getChapters().add(chapter);
        
        // 保存课程详情
        courseDetailRepository.save(courseDetail);
        
        return chapter;
    }
    
    /**
     * 更新课程章节
     * 
     * @param chapterId 章节ID
     * @param chapter 更新的章节数据
     * @return 更新后的章节对象
     */
    @Transactional
    public CourseChapter updateChapter(Long chapterId, CourseChapter updatedChapter) {
        // 查找现有章节
        CourseChapter existingChapter = findChapterById(chapterId);
        
        // 更新章节属性
        existingChapter.setTitle(updatedChapter.getTitle());
        existingChapter.setDescription(updatedChapter.getDescription());
        if (updatedChapter.getOrderIndex() != null) {
            existingChapter.setOrderIndex(updatedChapter.getOrderIndex());
        }
        existingChapter.setUpdateTime(LocalDateTime.now());
        
        // 保存更改
        courseDetailRepository.save(existingChapter.getCourseDetail());
        
        return existingChapter;
    }
    
    /**
     * 删除课程章节
     * 
     * @param chapterId 章节ID
     */
    @Transactional
    public void deleteChapter(Long chapterId) {
        // 获取课程详情并删除章节
        CourseDetail courseDetail = courseDetailRepository.findById(
                courseDetailRepository.findAll().stream()
                .filter(cd -> cd.getChapters().stream().anyMatch(ch -> ch.getId().equals(chapterId)))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("找不到包含此章节的课程详情"))
                .getId()
        ).orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("课程详情不存在"));
        
        // 删除章节
        courseDetail.getChapters().removeIf(chapter -> chapter.getId().equals(chapterId));
        
        // 保存更改
        courseDetailRepository.save(courseDetail);
    }
    
    /**
     * 添加小节到章节
     * 
     * @param chapterId 章节ID
     * @param section 小节对象
     * @return 保存后的小节对象
     */
    @Transactional
    public CourseSection addSection(Long chapterId, CourseSection section) {
        // 查找章节
        CourseChapter chapter = findChapterById(chapterId);
        
        // 设置小节所属的章节
        section.setChapter(chapter);
        
        // 设置小节顺序（放在最后）
        if (section.getOrderIndex() == null) {
            int lastIndex = chapter.getSections().size();
            section.setOrderIndex(lastIndex);
        }
        
        // 添加小节到章节
        chapter.getSections().add(section);
        
        // 保存更改
        courseDetailRepository.save(chapter.getCourseDetail());
        
        return section;
    }
    
    /**
     * 更新课程小节
     * 
     * @param sectionId 小节ID
     * @param section 更新的小节数据
     * @return 更新后的小节对象
     */
    @Transactional
    public CourseSection updateSection(Long sectionId, CourseSection updatedSection) {
        // 查找所有课程详情
        List<CourseDetail> allDetails = courseDetailRepository.findAll();
        
        // 找到包含此小节的章节
        CourseChapter chapter = allDetails.stream()
                .flatMap(detail -> detail.getChapters().stream())
                .filter(ch -> ch.getSections().stream().anyMatch(sec -> sec.getId().equals(sectionId)))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("找不到包含此小节的章节"));
        
        // 找到要更新的小节
        CourseSection existingSection = chapter.getSections().stream()
                .filter(sec -> sec.getId().equals(sectionId))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("小节不存在，ID: " + sectionId));
        
        // 更新小节属性
        existingSection.setTitle(updatedSection.getTitle());
        existingSection.setDescription(updatedSection.getDescription());
        existingSection.setVideoUrl(updatedSection.getVideoUrl());
        existingSection.setDuration(updatedSection.getDuration());
        existingSection.setIsFree(updatedSection.getIsFree());
        if (updatedSection.getOrderIndex() != null) {
            existingSection.setOrderIndex(updatedSection.getOrderIndex());
        }
        existingSection.setUpdateTime(LocalDateTime.now());
        
        // 保存更改
        courseDetailRepository.save(chapter.getCourseDetail());
        
        return existingSection;
    }
    
    /**
     * 删除课程小节
     * 
     * @param sectionId 小节ID
     */
    @Transactional
    public void deleteSection(Long sectionId) {
        // 查找所有课程详情
        List<CourseDetail> allDetails = courseDetailRepository.findAll();
        
        // 找到包含此小节的章节
        CourseChapter chapter = allDetails.stream()
                .flatMap(detail -> detail.getChapters().stream())
                .filter(ch -> ch.getSections().stream().anyMatch(sec -> sec.getId().equals(sectionId)))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("找不到包含此小节的章节"));
        
        // 删除小节
        chapter.getSections().removeIf(section -> section.getId().equals(sectionId));
        
        // 保存更改
        courseDetailRepository.save(chapter.getCourseDetail());
    }
    
    /**
     * 添加子小节到小节
     * 
     * @param sectionId 小节ID
     * @param subSection 子小节对象
     * @return 保存后的子小节对象
     */
    @Transactional
    public CourseSubSection addSubSection(Long sectionId, CourseSubSection subSection) {
        // 查找小节
        CourseSection section = findSectionById(sectionId);
        
        // 设置子小节所属的小节
        subSection.setParentSection(section);
        
        // 设置子小节顺序（放在最后）
        if (subSection.getOrderIndex() == null) {
            int lastIndex = section.getSubSections().size();
            subSection.setOrderIndex(lastIndex);
        }
        
        // 添加子小节到小节
        section.getSubSections().add(subSection);
        
        // 保存更改
        courseDetailRepository.save(section.getChapter().getCourseDetail());
        
        return subSection;
    }
    
    /**
     * 更新子小节
     * 
     * @param subSectionId 子小节ID
     * @param subSection 更新的子小节数据
     * @return 更新后的子小节对象
     */
    @Transactional
    public CourseSubSection updateSubSection(Long subSectionId, CourseSubSection updatedSubSection) {
        // 查找所有课程详情
        List<CourseDetail> allDetails = courseDetailRepository.findAll();
        
        // 找到包含此子小节的小节
        CourseSection section = allDetails.stream()
                .flatMap(detail -> detail.getChapters().stream())
                .flatMap(ch -> ch.getSections().stream())
                .filter(sec -> sec.getSubSections().stream().anyMatch(sub -> sub.getId().equals(subSectionId)))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("找不到包含此子小节的小节"));
        
        // 找到要更新的子小节
        CourseSubSection existingSubSection = section.getSubSections().stream()
                .filter(sub -> sub.getId().equals(subSectionId))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("子小节不存在，ID: " + subSectionId));
        
        // 更新子小节属性
        existingSubSection.setTitle(updatedSubSection.getTitle());
        existingSubSection.setContent(updatedSubSection.getContent());
        existingSubSection.setType(updatedSubSection.getType());
        existingSubSection.setResourceUrl(updatedSubSection.getResourceUrl());
        if (updatedSubSection.getOrderIndex() != null) {
            existingSubSection.setOrderIndex(updatedSubSection.getOrderIndex());
        }
        existingSubSection.setUpdateTime(LocalDateTime.now());
        
        // 保存更改
        courseDetailRepository.save(section.getChapter().getCourseDetail());
        
        return existingSubSection;
    }
    
    /**
     * 删除子小节
     * 
     * @param subSectionId 子小节ID
     */
    @Transactional
    public void deleteSubSection(Long subSectionId) {
        // 查找所有课程详情
        List<CourseDetail> allDetails = courseDetailRepository.findAll();
        
        // 找到包含此子小节的小节
        CourseSection section = allDetails.stream()
                .flatMap(detail -> detail.getChapters().stream())
                .flatMap(ch -> ch.getSections().stream())
                .filter(sec -> sec.getSubSections().stream().anyMatch(sub -> sub.getId().equals(subSectionId)))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("找不到包含此子小节的小节"));
        
        // 删除子小节
        section.getSubSections().removeIf(sub -> sub.getId().equals(subSectionId));
        
        // 保存更改
        courseDetailRepository.save(section.getChapter().getCourseDetail());
    }
    
    /**
     * 获取课程的所有章节
     * 
     * @param courseId 课程ID
     * @return 章节列表
     */
    public List<CourseChapter> getChaptersByCourseId(Long courseId) {
        CourseDetail courseDetail = getCourseDetailByCourseId(courseId);
        return courseDetail.getChapters();
    }
    
    /**
     * 获取章节的所有小节
     * 
     * @param chapterId 章节ID
     * @return 小节列表
     */
    public List<CourseSection> getSectionsByChapterId(Long chapterId) {
        CourseChapter chapter = findChapterById(chapterId);
        return chapter.getSections();
    }
    
    /**
     * 获取小节的所有子小节
     * 
     * @param sectionId 小节ID
     * @return 子小节列表
     */
    public List<CourseSubSection> getSubSectionsBySectionId(Long sectionId) {
        CourseSection section = findSectionById(sectionId);
        return section.getSubSections();
    }
    
    // 辅助方法
    
    private CourseChapter findChapterById(Long chapterId) {
        // 查找所有课程详情
        List<CourseDetail> allDetails = courseDetailRepository.findAll();
        
        // 找到包含此章节的课程详情
        CourseDetail courseDetail = allDetails.stream()
                .filter(detail -> detail.getChapters().stream().anyMatch(ch -> ch.getId().equals(chapterId)))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("找不到包含此章节的课程详情"));
        
        // 找到章节
        return courseDetail.getChapters().stream()
                .filter(ch -> ch.getId().equals(chapterId))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("章节不存在，ID: " + chapterId));
    }
    
    private CourseSection findSectionById(Long sectionId) {
        // 查找所有课程详情
        List<CourseDetail> allDetails = courseDetailRepository.findAll();
        
        // 找到包含此小节的章节
        CourseChapter chapter = allDetails.stream()
                .flatMap(detail -> detail.getChapters().stream())
                .filter(ch -> ch.getSections().stream().anyMatch(sec -> sec.getId().equals(sectionId)))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("找不到包含此小节的章节"));
        
        // 找到小节
        return chapter.getSections().stream()
                .filter(sec -> sec.getId().equals(sectionId))
                .findFirst()
                .orElseThrow((Supplier<ResourceNotFoundException>) () -> new ResourceNotFoundException("小节不存在，ID: " + sectionId));
    }
} 