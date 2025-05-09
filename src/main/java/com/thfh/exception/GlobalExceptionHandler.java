package com.thfh.exception;

import com.thfh.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各种异常，返回标准化的错误响应
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理资源不存在异常
     * @param e 资源不存在异常
     * @return 错误响应
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.info("资源不存在: {}", e.getMessage());
        return Result.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    /**
     * 处理请求处理器未找到异常
     * @param e 处理器未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.info("接口不存在: {}", e.getRequestURL());
        return Result.error(ErrorCode.NOT_FOUND.getCode(), "请求的接口不存在: " + e.getRequestURL());
    }

    /**
     * 处理请求参数绑定异常
     * @param e 参数绑定异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.debug("参数绑定异常: {}", message);
        return Result.error(ErrorCode.PARAMETER_ERROR.getCode(), "参数错误: " + message);
    }

    /**
     * 处理请求参数校验异常
     * @param e 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.debug("参数校验失败: {}", errors);
        
        Result<Map<String, String>> result = new Result<>();
        result.setCode(ErrorCode.PARAMETER_ERROR.getCode());
        result.setMessage("参数校验失败");
        result.setData(errors);
        return result;
    }

    /**
     * 处理约束违反异常
     * @param e 约束违反异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.debug("约束违反异常: {}", message);
        return Result.error(ErrorCode.PARAMETER_ERROR.getCode(), "参数错误: " + message);
    }

    /**
     * 处理非法参数异常
     * @param e 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.debug("非法参数: {}", e.getMessage());
        return Result.error(ErrorCode.PARAMETER_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理非法状态异常
     * @param e 非法状态异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleIllegalStateException(IllegalStateException e) {
        log.debug("非法状态: {}", e.getMessage());
        return Result.error(ErrorCode.FORBIDDEN.getCode(), e.getMessage());
    }

    /**
     * 处理参数类型不匹配异常
     * @param e 参数类型不匹配异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.debug("参数类型不匹配: {}", e.getMessage());
        return Result.error(ErrorCode.PARAMETER_ERROR.getCode(), "参数类型不匹配: " + e.getName() + "应为" + e.getRequiredType().getSimpleName() + "类型");
    }

    /**
     * 处理缺少请求参数异常
     * @param e 缺少请求参数异常
     * @return 错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.debug("缺少请求参数: {}", e.getMessage());
        return Result.error(ErrorCode.PARAMETER_ERROR.getCode(), "缺少请求参数: " + e.getParameterName());
    }

    /**
     * 处理上传文件大小超限异常
     * @param e 上传文件大小超限异常
     * @return 错误响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.debug("上传文件大小超过限制: {}", e.getMessage());
        return Result.error(ErrorCode.FILE_SIZE_LIMIT.getCode(), "上传文件大小超过限制，请压缩后再上传");
    }

    /**
     * 处理HTTP消息不可读异常
     * @param e HTTP消息不可读异常
     * @return 错误响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.debug("请求体格式错误: {}", e.getMessage());
        return Result.error(ErrorCode.PARAMETER_ERROR.getCode(), "请求体格式错误，请检查JSON格式");
    }

    /**
     * 处理身份验证异常
     * @param e 身份验证异常
     * @return 错误响应
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.debug("身份验证失败: {}", e.getMessage());
        return Result.error(ErrorCode.UNAUTHORIZED.getCode(), "身份验证失败: " + e.getMessage());
    }

    /**
     * 处理凭证错误异常
     * @param e 凭证错误异常
     * @return 错误响应
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.debug("凭证错误: {}", e.getMessage());
        return Result.error(ErrorCode.USERNAME_OR_PASSWORD_ERROR.getCode(), "用户名或密码错误");
    }

    /**
     * 处理权限拒绝异常
     * @param e 权限拒绝异常
     * @return 错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.debug("权限不足: {}", e.getMessage());
        return Result.error(ErrorCode.FORBIDDEN.getCode(), "权限不足，无法访问此资源");
    }

    /**
     * 处理业务异常
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.debug("业务异常: {}, 错误码: {}", e.getMessage(), errorCode.getCode());
        return Result.error(errorCode.getCode(), e.getMessage());
    }

    /**
     * 处理数据库访问异常
     * @param e 数据库访问异常
     * @return 错误响应
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常", e);
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), "数据库操作失败");
    }

    /**
     * 处理重复键异常
     * @param e 重复键异常
     * @return 错误响应
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        log.debug("数据重复: {}", e.getMessage());
        return Result.error(ErrorCode.DATA_ALREADY_EXIST.getCode(), "数据已存在，请勿重复添加");
    }

    /**
     * 处理数据完整性违反异常
     * @param e 数据完整性违反异常
     * @return 错误响应
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.debug("数据完整性违反: {}", e.getMessage());
        return Result.error(ErrorCode.PARAMETER_ERROR.getCode(), "数据不符合要求，请检查输入");
    }

    /**
     * 处理SQL异常
     * @param e SQL异常
     * @return 错误响应
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleSQLException(SQLException e) {
        log.error("SQL异常", e);
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), "数据库操作异常");
    }

    /**
     * 处理JWT过期异常
     * @param e JWT过期异常
     * @return 错误响应
     */
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleExpiredJwtException(ExpiredJwtException e) {
        log.debug("JWT令牌已过期: {}", e.getMessage());
        return Result.error(ErrorCode.UNAUTHORIZED.getCode(), "登录已过期，请重新登录");
    }

    /**
     * 处理JWT签名异常
     * @param e JWT签名异常
     * @return 错误响应
     */
    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleSignatureException(SignatureException e) {
        log.debug("无效的JWT签名: {}", e.getMessage());
        return Result.error(ErrorCode.UNAUTHORIZED.getCode(), "无效的身份凭证");
    }

    /**
     * 处理格式错误的JWT异常
     * @param e 格式错误的JWT异常
     * @return 错误响应
     */
    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleMalformedJwtException(MalformedJwtException e) {
        log.debug("JWT格式错误: {}", e.getMessage());
        return Result.error(ErrorCode.UNAUTHORIZED.getCode(), "无效的身份凭证格式");
    }

    /**
     * 处理JWT异常
     * @param e JWT异常
     * @return 错误响应
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleJwtException(JwtException e) {
        log.debug("JWT异常: {}", e.getMessage());
        return Result.error(ErrorCode.UNAUTHORIZED.getCode(), "身份验证失败，请重新登录");
    }

    /**
     * 处理认证不足异常
     * @param e 认证不足异常
     * @return 错误响应
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        log.debug("认证不足: {}", e.getMessage());
        return Result.error(ErrorCode.UNAUTHORIZED.getCode(), "请先登录");
    }

    /**
     * 处理所有其他异常
     * @param e 异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("服务器内部错误", e);
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), "服务器内部错误，请联系管理员");
    }
} 