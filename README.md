# 唐海风华管理系统 - 后端服务 (THFH Admin System - Backend)

## 项目概述 (Project Overview)

这是唐海风华管理系统的后端服务，基于Spring Boot框架开发，提供RESTful API接口。

## 技术栈 (Technology Stack)

- **核心框架**: Spring Boot 2.7.0
- **ORM框架**: Spring Data JPA
- **安全框架**: Spring Security
- **数据库**: MySQL 8.0
- **API文档**: Knife4j (基于Swagger)
- **认证方式**: JWT (JSON Web Token)
- **构建工具**: Maven
- **其他工具库**:
  - Lombok: 简化Java代码
  - FastJSON: JSON处理
  - Commons-lang3: 通用工具类
  - Commons-io: IO操作工具类

## 项目结构 (Project Structure)

```text
thfh-server-new/
├── src/main/java/com/thfh/
│   ├── config/          # 配置类
│   │   ├── JwtConfig.java           # JWT配置
│   │   ├── SecurityConfig.java      # 安全配置
│   │   ├── SwaggerConfig.java       # Swagger配置
│   │   └── WebConfig.java           # Web配置
│   ├── controller/      # 控制器层
│   │   ├── AdminController.java     # 管理员相关接口
│   │   ├── AuthController.java      # 认证相关接口
│   │   ├── UserController.java      # 用户相关接口
│   │   └── ...
│   ├── model/           # 实体类
│   │   ├── Admin.java              # 管理员实体
│   │   ├── User.java               # 用户实体
│   │   ├── enums/                  # 枚举类
│   │   │   └── UserType.java       # 用户类型枚举
│   │   └── ...
│   ├── repository/      # 数据访问层
│   │   ├── AdminRepository.java    # 管理员数据访问
│   │   ├── UserRepository.java     # 用户数据访问
│   │   └── ...
│   ├── service/         # 服务层
│   │   ├── AdminService.java       # 管理员服务
│   │   ├── AuthService.java        # 认证服务
│   │   ├── UserService.java        # 用户服务
│   │   └── ...
│   ├── util/            # 工具类
│   │   ├── JwtUtil.java            # JWT工具类
│   │   ├── FileUtil.java           # 文件处理工具类
│   │   └── ...
│   ├── common/          # 通用类
│   │   ├── Result.java             # 统一响应格式
│   │   ├── exception/              # 异常处理
│   │   └── ...
│   └── ThfhAdminApplication.java   # 应用入口类
│
├── src/main/resources/
│   ├── application.yml             # 主配置文件
│   ├── application-database.yml    # 数据库配置
│   └── knife4j/                    # API文档配置
│       └── home.md                 # API文档首页
│
└── pom.xml                         # Maven配置文件
```

## 核心组件说明 (Core Components)

### 1. 实体类 (Entities)

系统中有两种主要的用户类型：

- **Admin**: 管理员实体类 (`src/main/java/com/thfh/model/Admin.java`)
- **User**: 用户实体类 (`src/main/java/com/thfh/model/User.java`)
  - 用户分为两种类型：`UserType.STUDENT`(学员) 和 `UserType.TEACHER`(教员)

### 2. 认证系统 (Authentication)

- 使用JWT进行认证
- 登录流程在`AuthService`中实现
- 登录时先尝试管理员表验证，失败后尝试用户表验证
- 认证成功后返回JWT令牌

### 3. 权限控制 (Authorization)

- 使用Spring Security的`@PreAuthorize`注解控制访问权限
- 管理员接口使用`@PreAuthorize("hasRole('ROLE_ADMIN')")`进行控制
- 用户接口根据用户类型进行权限控制

### 4. 统一响应格式 (Unified Response)

所有API响应都使用`com.thfh.common.Result`类进行封装，格式如下：

```json
{
  "result": "SUCCESS/ERROR",
  "message": "成功或错误消息",
  "data": "返回的数据对象"
}
```

## 开发指南 (Development Guide)

### 1. 添加新的API接口

1. 在`controller`包中创建或修改控制器类
2. 使用`@RestController`和`@RequestMapping`注解
3. 实现API方法，使用适当的HTTP方法注解(`@GetMapping`, `@PostMapping`等)
4. 返回`Result`类型的响应
5. 添加适当的权限控制注解

示例：

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result getAllUsers() {
        List<User> users = userService.findAll();
        return Result.success(users);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result createUser(@RequestBody @Valid UserDTO userDTO) {
        User user = userService.create(userDTO);
        return Result.success(user);
    }
}
```

### 2. 添加新的实体类

1. 在`model`包中创建实体类
2. 使用JPA注解(`@Entity`, `@Table`等)
3. 使用Lombok注解简化代码(`@Data`, `@Builder`等)
4. 定义适当的关系映射

示例：

```java
@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    // 其他字段和关系
}
```

### 3. 添加新的数据访问层

1. 在`repository`包中创建接口
2. 继承`JpaRepository`接口
3. 定义自定义查询方法

示例：

```java
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacherId(Long teacherId);

    Optional<Course> findByName(String name);

    @Query("SELECT c FROM Course c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Course> searchByKeyword(@Param("keyword") String keyword);
}
```

### 4. 添加新的服务层

1. 在`service`包中创建服务接口和实现类
2. 注入所需的依赖
3. 实现业务逻辑

示例：

```java
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public Course create(CourseDTO courseDTO) {
        // 实现创建逻辑
        Course course = new Course();
        // 设置属性
        return courseRepository.save(course);
    }

    // 其他方法
}
```

## 配置说明 (Configuration)

### 1. 数据库配置

数据库配置位于`application-database.yml`文件中：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/thfh_admin?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2b8&allowPublicKeyRetrieval=true
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 2. JWT配置

JWT配置位于`application.yml`文件中：

```yaml
jwt:
  secret: thfh-admin-secret-key
  expiration: 86400  # 24小时
```

### 3. 文件上传配置

文件上传配置位于`application.yml`文件中：

```yaml
file:
  upload-dir: ./uploads

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

## API文档 (API Documentation)

系统集成了Knife4j，提供了交互式API文档。启动后端服务后，访问以下地址查看API文档：

```text
http://localhost:8085/doc.html
```

## 常用命令 (Common Commands)

```bash
# 启动后端服务并显示日志
./start.sh start

# 启动后端服务但不显示日志
./start.sh start-nolog

# 停止后端服务
./start.sh stop

# 重启后端服务
./start.sh restart

# 查看应用状态
./start.sh status

# 仅构建应用
./start.sh build

# 查看日志
./start.sh logs [行数]

# 实时查看日志
./start.sh follow
```

## AI开发注意事项 (AI Development Notes)

1. **统一响应格式**: 所有API响应必须使用`com.thfh.common.Result`类进行封装
2. **不要创建impl文件**: 服务实现直接在服务类中完成，不要创建额外的impl文件
3. **权限控制**: 管理员接口使用`@PreAuthorize("hasRole('ROLE_ADMIN')")`进行控制
4. **用户类型**: 系统支持管理员和普通用户两种类型，普通用户又分为学员和教员
5. **数据库自动创建**: 系统使用JPA的`ddl-auto: update`配置，会自动创建和更新数据库表
6. **文件上传**: 系统支持文件上传功能，上传目录为`./uploads`
7. **启动服务**: 后端服务已在运行中，不需要重新启动
8. **代码风格**: 遵循Java标准命名规范，使用驼峰命名法
9. **异常处理**: 使用统一的异常处理机制，通过`@ControllerAdvice`和`@ExceptionHandler`处理异常
10. **日志记录**: 使用Lombok的`@Slf4j`注解和日志框架记录日志
