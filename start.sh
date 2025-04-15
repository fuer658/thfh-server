#!/bin/bash

# 脚本名称: start.sh
# 描述: 用于在Ubuntu上启动Spring Boot后端服务
# 版本: 1.0.0
# 日期: $(date +%Y-%m-%d)

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # 无颜色

# 配置项 - 根据实际情况修改
APP_NAME="thfh-server"
APP_VERSION="1.0.0"
# 应用目录，默认为当前目录
APP_DIR="$PWD"
# Java相关配置
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError"
# Spring配置
SPRING_PROFILES="--spring.profiles.active=database"
# 日志和PID文件
LOG_DIR="${APP_DIR}/logs"
PID_FILE="${APP_DIR}/app.pid"
JAR_FILE="${APP_DIR}/target/${APP_NAME}-${APP_VERSION}.jar"

# 创建日志目录
mkdir -p ${LOG_DIR}

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

# 检查Java是否安装
check_java() {
    # 直接检查java命令是否可用
    if which java >/dev/null 2>&1; then
        log "找到java命令"
        JAVA="java"
        JAVA_VERSION=$(java -version 2>&1 | grep -i version | head -n 1 | awk -F '"' '{print $2}')
        log "Java版本: $JAVA_VERSION"
        return 0
    fi

    # 检查常见的Java安装路径
    for java_path in "/usr/bin/java" "/usr/local/bin/java" "/opt/jdk/bin/java" "/usr/lib/jvm/java-8-openjdk-amd64/bin/java"; do
        if [ -x "$java_path" ]; then
            log "找到Java: $java_path"
            JAVA="$java_path"
            JAVA_VERSION=$($JAVA -version 2>&1 | grep -i version | head -n 1 | awk -F '"' '{print $2}')
            log "Java版本: $JAVA_VERSION"
            return 0
        fi
    done

    # 检查JAVA_HOME环境变量
    if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
        log "找到JAVA_HOME: $JAVA_HOME"
        JAVA="$JAVA_HOME/bin/java"
        JAVA_VERSION=$($JAVA -version 2>&1 | grep -i version | head -n 1 | awk -F '"' '{print $2}')
        log "Java版本: $JAVA_VERSION"
        return 0
    fi

    # 如果以上都失败，尝试设置JAVA_HOME
    if [ -d "/usr/lib/jvm/java-8-openjdk-amd64" ]; then
        export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"
        log "设置JAVA_HOME: $JAVA_HOME"
        JAVA="$JAVA_HOME/bin/java"
        JAVA_VERSION=$($JAVA -version 2>&1 | grep -i version | head -n 1 | awk -F '"' '{print $2}')
        log "Java版本: $JAVA_VERSION"
        return 0
    fi

    # 如果所有尝试都失败
    error "未找到Java。请安装Java 8或更高版本，或设置JAVA_HOME环境变量。"
    exit 1
}

# 检查Maven是否安装
check_maven() {
    # 直接尝试运行mvn命令
    if command -v mvn >/dev/null 2>&1 || which mvn >/dev/null 2>&1 || mvn -version >/dev/null 2>&1; then
        log "找到Maven命令"
        MVN="mvn"
        MVN_VERSION=$(mvn -version 2>&1 | head -n 1)
        log "Maven版本: $MVN_VERSION"
        return 0
    fi

    # 检查M2_HOME环境变量
    if [ -n "$M2_HOME" ]; then
        if [ -x "$M2_HOME/bin/mvn" ]; then
            log "找到M2_HOME: $M2_HOME"
            MVN="$M2_HOME/bin/mvn"
            MVN_VERSION=$($MVN -version 2>&1 | head -n 1)
            log "Maven版本: $MVN_VERSION"
            return 0
        fi
    fi

    # 检查MAVEN_HOME环境变量
    if [ -n "$MAVEN_HOME" ]; then
        if [ -x "$MAVEN_HOME/bin/mvn" ]; then
            log "找到MAVEN_HOME: $MAVEN_HOME"
            MVN="$MAVEN_HOME/bin/mvn"
            MVN_VERSION=$($MVN -version 2>&1 | head -n 1)
            log "Maven版本: $MVN_VERSION"
            return 0
        fi
    fi

    # 如果在环境变量中找不到，尝试常见的安装路径
    for mvn_path in "/usr/bin/mvn" "/usr/local/bin/mvn" "/opt/maven/bin/mvn" "/usr/share/maven/bin/mvn"; do
        if [ -x "$mvn_path" ]; then
            log "找到Maven: $mvn_path"
            MVN="$mvn_path"
            MVN_VERSION=$($MVN -version 2>&1 | head -n 1)
            log "Maven版本: $MVN_VERSION"
            return 0
        fi
    done

    # 如果所有尝试都失败
    error "未找到Maven。请确保Maven已安装并添加到PATH环境变量中，或设置M2_HOME/MAVEN_HOME环境变量。"
    exit 1
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

    # 检查是否存在Maven配置文件
    if [ -f "$APP_DIR/settings.xml" ]; then
        log "使用自定义Maven配置文件: $APP_DIR/settings.xml"
        $MVN -s "$APP_DIR/settings.xml" clean package -DskipTests
    else
        log "使用默认Maven配置"
        $MVN clean package -DskipTests
    fi

    if [ $? -ne 0 ]; then
        error "构建失败，请检查Maven日志"
        exit 1
    fi

    log "构建成功"
}

# 检查MySQL配置
check_mysql() {
    log "检查MySQL配置..."

    # 如果存在setup-mysql.sh脚本，则执行它
    if [ -f "$APP_DIR/setup-mysql.sh" ]; then
        log "执行setup-mysql.sh脚本"
        bash "$APP_DIR/setup-mysql.sh"
        if [ $? -ne 0 ]; then
            warn "MySQL配置检查失败，应用可能无法正常连接数据库"
            log "请手动检查MySQL配置和数据库连接信息"
        fi
    else
        warn "setup-mysql.sh脚本不存在，跳过MySQL配置检查"
        log "如果应用无法连接数据库，请手动检查MySQL配置"
    fi
}

# 启动应用
start_app() {
    # 默认显示日志
    SHOW_LOG=true

    # 如果传入了参数并且是"nolog"，则不显示日志
    if [ "$1" = "nolog" ]; then
        SHOW_LOG=false
    fi

    log "正在启动应用..."

    if is_running; then
        warn "应用已经在运行中，PID: $(cat $PID_FILE)"
        return 0
    fi

    cd "$APP_DIR" || { error "无法进入应用目录: $APP_DIR"; exit 1; }

    # 检查MySQL配置
    check_mysql

    # 检查JAR文件是否存在
    if [ ! -f "$JAR_FILE" ]; then
        error "JAR文件不存在: $JAR_FILE"
        exit 1
    fi

    log "使用以下命令启动应用:"
    log "$JAVA $JAVA_OPTS -jar $JAR_FILE $SPRING_PROFILES"

    nohup $JAVA $JAVA_OPTS -jar "$JAR_FILE" $SPRING_PROFILES > "$LOG_DIR/app.log" 2>&1 &

    PID=$!
    echo $PID > "$PID_FILE"

    log "应用启动中，请稍候..."
    sleep 5

    if is_running; then
        log "应用已成功启动，PID: $PID"
        log "日志文件位置: $LOG_DIR/app.log"

        if [ "$SHOW_LOG" = true ]; then
            log "开始显示实时日志输出..."
            log "按 Ctrl+C 可以停止查看日志，应用会继续在后台运行"
            sleep 2
            tail -f "$LOG_DIR/app.log"
        else
            log "应用已在后台启动，可以通过 $0 logs 或 $0 follow 命令查看日志"
        fi
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
            echo -n "."
        done
        echo ""

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

        # 显示应用端口
        if type -p netstat > /dev/null 2>&1; then
            log "应用端口信息:"
            netstat -tulpn 2>/dev/null | grep "$PID" | awk '{print "  "$4" "$7}'
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

# 实时查看日志
follow_logs() {
    if [ -f "$LOG_DIR/app.log" ]; then
        tail -f "$LOG_DIR/app.log"
    else
        warn "日志文件不存在: $LOG_DIR/app.log"
    fi
}

# 显示帮助信息
show_help() {
    echo "用法: $0 {start|start-nolog|stop|restart|restart-nolog|status|build|logs [行数]|follow|check-mysql|help}"
    echo ""
    echo "命令:"
    echo "  1. start         启动应用并显示实时日志"
    echo "  2. start-nolog   启动应用但不显示日志"
    echo "  3. stop          停止应用"
    echo "  4. restart       重启应用并显示实时日志"
    echo "  5. restart-nolog 重启应用但不显示日志"
    echo "  6. status        显示应用状态"
    echo "  7. build         仅构建应用"
    echo "  8. logs          显示最近的日志 (默认100行)"
    echo "  9. follow        实时查看日志"
    echo " 10. check-mysql   检查MySQL配置"
    echo " 11. help          显示此帮助信息"

    if [ "$1" = "interactive" ]; then
        show_interactive_menu
    fi
}

# 交互式菜单
show_interactive_menu() {
    echo ""
    echo -n "请输入命令编号 (1-11): "
    read -r choice

    case "$choice" in
        1)
            main "start"
            ;;
        2)
            main "start-nolog"
            ;;
        3)
            main "stop"
            ;;
        4)
            main "restart"
            ;;
        5)
            main "restart-nolog"
            ;;
        6)
            main "status"
            ;;
        7)
            main "build"
            ;;
        8)
            echo -n "请输入要显示的日志行数 (默认100): "
            read -r log_lines
            if [ -z "$log_lines" ]; then
                log_lines=100
            fi
            main "logs" "$log_lines"
            ;;
        9)
            main "follow"
            ;;
        10)
            main "check-mysql"
            ;;
        11)
            main "help"
            ;;
        *)
            echo "无效的选择: $choice"
            exit 1
            ;;
    esac
}

# 主函数
main() {
    # 检查Java环境
    check_java

    # 处理命令行参数
    case "$1" in
        start)
            # 如果JAR文件已存在，直接启动应用
            if [ -f "$JAR_FILE" ]; then
                log "JAR文件已存在，直接启动应用"
                start_app
            else
                # 如果JAR文件不存在，需要检查Maven并构建
                check_maven
                build_app
                start_app
            fi
            ;;
        start-nolog)
            # 如果JAR文件已存在，直接启动应用
            if [ -f "$JAR_FILE" ]; then
                log "JAR文件已存在，直接启动应用"
                start_app "nolog"
            else
                # 如果JAR文件不存在，需要检查Maven并构建
                check_maven
                build_app
                start_app "nolog"
            fi
            ;;
        stop)
            stop_app
            ;;
        restart)
            stop_app
            # 如果JAR文件已存在，直接启动应用
            if [ -f "$JAR_FILE" ]; then
                log "JAR文件已存在，直接启动应用"
                start_app
            else
                # 如果JAR文件不存在，需要检查Maven并构建
                check_maven
                build_app
                start_app
            fi
            ;;
        restart-nolog)
            stop_app
            # 如果JAR文件已存在，直接启动应用
            if [ -f "$JAR_FILE" ]; then
                log "JAR文件已存在，直接启动应用"
                start_app "nolog"
            else
                # 如果JAR文件不存在，需要检查Maven并构建
                check_maven
                build_app
                start_app "nolog"
            fi
            ;;
        status)
            status_app
            ;;
        build)
            check_maven
            build_app
            ;;
        logs)
            LINES=${2:-100}
            show_logs "$LINES"
            ;;
        follow)
            follow_logs
            ;;
        check-mysql)
            check_mysql
            ;;
        help)
            show_help
            ;;
        *)
            if [ -z "$1" ]; then
                # 如果没有提供参数，显示帮助并进入交互式模式
                show_help "interactive"
            else
                show_help
                exit 1
            fi
            ;;
    esac

    exit 0
}

# 执行主函数
main "$@"
