package com.thfh.exception;

import com.thfh.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
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
        log.error("资源不存在: {}", e.getMessage());
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
        log.error("接口不存在: {}", e.getMessage());
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
        log.warn("参数绑定异常: {}", message);
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
        log.warn("参数校验失败: {}", errors);
        
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
        log.warn("约束违反异常: {}", message);
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
        log.error("非法参数: {}", e.getMessage());
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
        log.error("非法状态: {}", e.getMessage());
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
        log.warn("参数类型不匹配: {}", e.getMessage());
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
        log.warn("缺少请求参数: {}", e.getMessage());
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
        log.warn("上传文件大小超过限制: {}", e.getMessage());
        return Result.error(ErrorCode.FILE_SIZE_LIMIT.getCode(), "上传文件大小超过限制，请压缩后再上传");
    }

    /**
     * 处理身份验证异常
     * @param e 身份验证异常
     * @return 错误响应
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("身份验证失败: {}", e.getMessage());
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
        log.warn("凭证错误: {}", e.getMessage());
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
        log.warn("权限不足: {}", e.getMessage());
        return Result.error(ErrorCode.FORBIDDEN.getCode(), "权限不足，无法访问此资源");
    }

    /**
     * 处理业务异常
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        // 根据errorCode返回相应的HTTP状态码
        return Result.error(errorCode.getCode(), e.getMessage());
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