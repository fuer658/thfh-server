# THFH 后台管理系统 API 文档

## 简介

这是THFH后台管理系统的API文档，使用Knife4j构建。

## 接口说明

本文档提供了系统所有接口的详细说明，包括：

- 接口地址
- 请求方式
- 请求参数
- 响应结果

## 认证说明

除了登录接口外，所有接口都需要在请求头中携带`Authorization`令牌进行认证。

格式：`Authorization: Bearer {token}`

## 返回格式

所有接口返回统一的JSON格式：

```json
{
  "result": "SUCCESS/ERROR",
  "message": "成功或错误消息",
  "data": "返回的数据对象"
}
```

## 接口文档使用说明

1. 左侧菜单列出了所有可用的API分类
2. 点击具体API可以查看详细信息和进行测试
3. 认证接口调用成功后，可以复制返回的token到Authorize中使用 