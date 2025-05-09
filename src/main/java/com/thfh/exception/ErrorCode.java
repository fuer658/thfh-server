package com.thfh.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 错误码枚举
 * 定义系统中常见的错误类型和对应的错误码
 */
@Getter
public enum ErrorCode {

    // 通用错误码（1000-1999）
    SUCCESS(1000, "操作成功"),
    SYSTEM_ERROR(1001, "系统内部错误"),
    PARAMETER_ERROR(1002, "参数错误"),
    UNAUTHORIZED(1003, "未授权或登录已过期"),
    FORBIDDEN(1004, "权限不足"),
    NOT_FOUND(1005, "资源不存在"),
    METHOD_NOT_ALLOWED(1006, "请求方法不允许"),
    REQUEST_TIMEOUT(1007, "请求超时"),
    CONFLICT(1008, "数据冲突"),
    TOO_MANY_REQUESTS(1009, "请求频率过高"),
    
    // 用户相关错误码（2000-2999）
    USER_NOT_EXIST(2000, "用户不存在"),
    USER_ALREADY_EXIST(2001, "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR(2002, "用户名或密码错误"),
    ACCOUNT_DISABLED(2003, "账号已被禁用"),
    ACCOUNT_LOCKED(2004, "账号已被锁定"),
    USER_REGISTER_ERROR(2005, "用户注册失败"),
    USER_UPDATE_ERROR(2006, "用户信息更新失败"),
    
    // 文件操作相关错误码（3000-3999）
    FILE_UPLOAD_ERROR(3000, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(3001, "文件下载失败"),
    FILE_DELETE_ERROR(3002, "文件删除失败"),
    FILE_NOT_FOUND(3003, "文件不存在"),
    FILE_SIZE_LIMIT(3004, "文件大小超过限制"),
    FILE_TYPE_NOT_SUPPORT(3005, "不支持的文件类型"),
    
    // 业务相关错误码（4000-4999）
    DATA_NOT_EXIST(4000, "数据不存在"),
    DATA_ALREADY_EXIST(4001, "数据已存在"),
    DATA_ADD_ERROR(4002, "数据添加失败"),
    DATA_UPDATE_ERROR(4003, "数据更新失败"),
    DATA_DELETE_ERROR(4004, "数据删除失败"),
    OPERATION_FAILED(4005, "操作失败"),
    SERVICE_UNAVAILABLE(4006, "服务不可用"),
    BUSINESS_ERROR(4007, "业务处理异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取HTTP状态码
     * @return HTTP状态码
     */
    public HttpStatus getHttpStatus() {
        if (this.code >= 1000 && this.code < 2000) {
            // 通用错误
            switch (this) {
                case UNAUTHORIZED:
                    return HttpStatus.UNAUTHORIZED;
                case FORBIDDEN:
                    return HttpStatus.FORBIDDEN;
                case NOT_FOUND:
                    return HttpStatus.NOT_FOUND;
                case METHOD_NOT_ALLOWED:
                    return HttpStatus.METHOD_NOT_ALLOWED;
                case REQUEST_TIMEOUT:
                    return HttpStatus.REQUEST_TIMEOUT;
                case CONFLICT:
                    return HttpStatus.CONFLICT;
                case TOO_MANY_REQUESTS:
                    return HttpStatus.TOO_MANY_REQUESTS;
                case PARAMETER_ERROR:
                    return HttpStatus.BAD_REQUEST;
                default:
                    return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        } else if (this.code >= 2000 && this.code < 3000) {
            // 用户相关错误
            switch (this) {
                case USERNAME_OR_PASSWORD_ERROR:
                    return HttpStatus.UNAUTHORIZED;
                case USER_NOT_EXIST:
                    return HttpStatus.NOT_FOUND;
                case USER_ALREADY_EXIST:
                    return HttpStatus.CONFLICT;
                default:
                    return HttpStatus.BAD_REQUEST;
            }
        } else if (this.code >= 3000 && this.code < 4000) {
            // 文件操作相关错误
            if (this == FILE_NOT_FOUND) {
                return HttpStatus.NOT_FOUND;
            } else if (this == FILE_SIZE_LIMIT || this == FILE_TYPE_NOT_SUPPORT) {
                return HttpStatus.BAD_REQUEST;
            } else {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        } else {
            // 业务相关错误
            if (this == DATA_NOT_EXIST) {
                return HttpStatus.NOT_FOUND;
            } else if (this == DATA_ALREADY_EXIST) {
                return HttpStatus.CONFLICT;
            } else if (this == SERVICE_UNAVAILABLE) {
                return HttpStatus.SERVICE_UNAVAILABLE;
            } else {
                return HttpStatus.BAD_REQUEST;
            }
        }
    }
} 