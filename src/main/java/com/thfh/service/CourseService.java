package com.thfh.service;

import com.thfh.dto.*;
import com.thfh.model.*;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.UserCourseInteractionRepository;
import com.thfh.repository.UserCourseRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 课程服务接口
 */
public interface CourseService {
    
    /**
     * 保存课程信息
     * @param course 课程对象
     * @return 保存后的课程对象
     */
    Course save(Course course);
    
    /**
     * 删除课程
     * @param id 课程ID
     */
    void delete(Long id);
}
