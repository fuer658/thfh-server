#!/bin/bash

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # 无颜色

# 日志函数
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] 错误: $1${NC}" >&2
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] 警告: $1${NC}"
}

# 检查MySQL是否安装
check_mysql_installed() {
    if command -v mysql &> /dev/null; then
        log "MySQL已安装"
        return 0
    else
        error "MySQL未安装，请先安装MySQL"
        log "可以使用以下命令安装MySQL:"
        log "sudo apt update"
        log "sudo apt install mysql-server -y"
        return 1
    fi
}

# 检查MySQL服务是否运行
check_mysql_running() {
    if systemctl is-active --quiet mysql; then
        log "MySQL服务正在运行"
        return 0
    else
        error "MySQL服务未运行"
        log "可以使用以下命令启动MySQL服务:"
        log "sudo systemctl start mysql"
        return 1
    fi
}

# 检查数据库是否存在
check_database_exists() {
    DB_NAME="thfh_admin"
    DB_USER="root"
    DB_PASS="root"
    
    if mysql -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME" 2>/dev/null; then
        log "数据库 $DB_NAME 已存在"
        return 0
    else
        warn "数据库 $DB_NAME 不存在"
        return 1
    fi
}

# 创建数据库
create_database() {
    DB_NAME="thfh_admin"
    DB_USER="root"
    DB_PASS="root"
    
    log "正在创建数据库 $DB_NAME..."
    
    mysql -u"$DB_USER" -p"$DB_PASS" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        log "数据库 $DB_NAME 创建成功"
        return 0
    else
        error "数据库创建失败，可能是因为用户名或密码不正确"
        log "请检查application-database.yml中的数据库配置"
        return 1
    fi
}

# 主函数
main() {
    log "开始检查MySQL配置..."
    
    # 检查MySQL是否安装
    check_mysql_installed || return 1
    
    # 检查MySQL服务是否运行
    check_mysql_running || return 1
    
    # 检查数据库是否存在
    if ! check_database_exists; then
        # 创建数据库
        create_database || return 1
    fi
    
    log "MySQL配置检查完成，一切正常"
    return 0
}

# 执行主函数
main
