#!/bin/bash

# 脚本名称: start-backend.sh
# 描述: 用于在Ubuntu上启动Spring Boot后端服务
# 作者: Augment Agent
# 日期: $(date +%Y-%m-%d)

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # 无颜色

# 配置项 - 根据实际情况修改
APP_NAME="thfh-server"
APP_VERSION="1.0.0"
APP_DIR="/opt/thfh/backend"
JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64" # 根据实际Java安装路径修改
LOG_DIR="${APP_DIR}/logs"
PID_FILE="${APP_DIR}/app.pid"
JAR_FILE="${APP_DIR}/target/${APP_NAME}-${APP_VERSION}.jar"
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_DIR}"
SPRING_PROFILES="--spring.profiles.active=database"

# 创建必要的目录
mkdir -p ${APP_DIR}
mkdir -p ${LOG_DIR}

# 日志函数
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}" >&2
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

# 检查Java是否安装
check_java() {
    if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
        log "找到JAVA_HOME: $JAVA_HOME"
        JAVA="$JAVA_HOME/bin/java"
    elif type -p java > /dev/null 2>&1; then
        log "找到java命令"
        JAVA="java"
    else
        error "未找到Java。请安装Java 8或更高版本，或设置JAVA_HOME环境变量。"
        exit 1
    fi

    JAVA_VERSION=$($JAVA -version 2>&1 | awk -F '"' '/version/ {print $2}')
    log "Java版本: $JAVA_VERSION"
}

# 检查Maven是否安装
check_maven() {
    if type -p mvn > /dev/null 2>&1; then
        log "找到Maven命令"
        MVN="mvn"
    else
        error "未找到Maven。请安装Maven 3或更高版本。"
        exit 1
    fi

    MVN_VERSION=$($MVN -version | head -n 1)
    log "Maven版本: $MVN_VERSION"
}

# 检查应用是否正在运行
is_running() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null; then
            return 0
        fi
    fi
    return 1
}

# 构建应用
build_app() {
    log "开始构建应用..."
    cd "$APP_DIR" || { error "无法进入应用目录: $APP_DIR"; exit 1; }
    
    $MVN clean package -DskipTests
    
    if [ $? -ne 0 ]; then
        error "构建失败"
        exit 1
    fi
    
    log "构建成功"
}

# 启动应用
start_app() {
    log "正在启动应用..."
    
    if is_running; then
        warn "应用已经在运行中，PID: $(cat $PID_FILE)"
        return 0
    fi
    
    cd "$APP_DIR" || { error "无法进入应用目录: $APP_DIR"; exit 1; }
    
    nohup $JAVA $JAVA_OPTS -jar "$JAR_FILE" $SPRING_PROFILES > "$LOG_DIR/app.log" 2>&1 &
    
    PID=$!
    echo $PID > "$PID_FILE"
    
    sleep 5
    
    if is_running; then
        log "应用已成功启动，PID: $PID"
        log "日志文件位置: $LOG_DIR/app.log"
    else
        error "应用启动失败，请检查日志: $LOG_DIR/app.log"
        exit 1
    fi
}

# 停止应用
stop_app() {
    log "正在停止应用..."
    
    if is_running; then
        PID=$(cat "$PID_FILE")
        kill "$PID"
        
        # 等待应用停止
        TIMEOUT=30
        while is_running && [ $TIMEOUT -gt 0 ]; do
            sleep 1
            TIMEOUT=$((TIMEOUT - 1))
        done
        
        if is_running; then
            warn "应用未能正常停止，尝试强制终止..."
            kill -9 "$PID"
            sleep 2
        fi
        
        if ! is_running; then
            log "应用已停止"
            rm -f "$PID_FILE"
        else
            error "无法停止应用，请手动检查进程: $PID"
            exit 1
        fi
    else
        warn "应用未运行"
    fi
}

# 显示应用状态
status_app() {
    if is_running; then
        PID=$(cat "$PID_FILE")
        log "应用正在运行，PID: $PID"
        
        # 显示运行时间
        if type -p ps > /dev/null 2>&1; then
            STARTED=$(ps -o lstart= -p "$PID")
            log "启动时间: $STARTED"
        fi
        
        # 显示内存使用情况
        if type -p ps > /dev/null 2>&1; then
            MEM=$(ps -o rss= -p "$PID" | awk '{print $1/1024 " MB"}')
            log "内存使用: $MEM"
        fi
    else
        warn "应用未运行"
    fi
}

# 显示最近的日志
show_logs() {
    LINES=${1:-100}
    if [ -f "$LOG_DIR/app.log" ]; then
        tail -n "$LINES" "$LOG_DIR/app.log"
    else
        warn "日志文件不存在: $LOG_DIR/app.log"
    fi
}

# 主函数
main() {
    # 检查环境
    check_java
    check_maven
    
    # 处理命令行参数
    case "$1" in
        start)
            build_app
            start_app
            ;;
        stop)
            stop_app
            ;;
        restart)
            stop_app
            build_app
            start_app
            ;;
        status)
            status_app
            ;;
        build)
            build_app
            ;;
        logs)
            LINES=${2:-100}
            show_logs "$LINES"
            ;;
        *)
            echo "用法: $0 {start|stop|restart|status|build|logs [行数]}"
            exit 1
            ;;
    esac
    
    exit 0
}

# 执行主函数
main "$@"
