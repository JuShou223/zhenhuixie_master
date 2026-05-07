#!/bin/bash
# 真会写 · 部署脚本 — 上传到服务器 /var/data/deploy.sh
# 用法（在服务器上执行）：
#   ./deploy.sh jar    备份旧 JAR → 替换 → 重启 Java 容器
#   ./deploy.sh h5     备份旧 H5 → 替换 → 热重载 Nginx
#   ./deploy.sh admin  备份旧后台 → 替换 → 热重载 Nginx
#   ./deploy.sh all    同时部署以上三项
#
# 备份规则：每种最多保留 3 份，超出自动删除最旧的
#   JAR 备份目录：    /var/data/java/backups/
#   H5 备份目录：      /var/data/nginx/h5_backups/
#   后台备份目录：     /var/data/nginx/admin_backups/
#
# 部署前需先把文件上传到 /tmp：
#   scp ruoyi-admin.jar                 root@SERVER:/tmp/ruoyi-admin.jar
#   scp -r dist/build/h5/*              root@SERVER:/tmp/zhw_h5/
#   scp -r dist/*                       root@SERVER:/tmp/zhw_admin/
#
# 本地构建：
#   cd RuoYi-Vue && mvn clean package -Dmaven.test.skip=true
#   cd uni-app-starter && npm run build:h5
#   cd RuoYi-Vue/ruoyi-ui && VUE_APP_PUBLIC_PATH=/admin/ npm run build:prod

set -e

JAR_PATH=/var/data/java/ruoyi-admin.jar
JAR_BACKUP_DIR=/var/data/java/backups
H5_DIR=/var/data/nginx/h5
H5_BACKUP_DIR=/var/data/nginx/h5_backups
ADMIN_DIR=/var/data/nginx/admin
ADMIN_BACKUP_DIR=/var/data/nginx/admin_backups

MODE=$1

backup_jar() {
    mkdir -p "$JAR_BACKUP_DIR"
    if [ -f "$JAR_PATH" ]; then
        TS=$(date +%Y%m%d_%H%M%S)
        cp "$JAR_PATH" "$JAR_BACKUP_DIR/ruoyi-admin.jar.$TS"
        echo "[backup] JAR -> ruoyi-admin.jar.$TS"
        ls -t "$JAR_BACKUP_DIR"/ruoyi-admin.jar.* 2>/dev/null | tail -n +4 | while read f; do
            rm -f "$f" && echo "[backup] 删除旧备份: $(basename $f)"
        done
    fi
}

backup_h5() {
    mkdir -p "$H5_BACKUP_DIR"
    if [ -d "$H5_DIR" ] && [ "$(ls -A $H5_DIR 2>/dev/null)" ]; then
        TS=$(date +%Y%m%d_%H%M%S)
        cp -r "$H5_DIR" "$H5_BACKUP_DIR/h5_$TS"
        echo "[backup] H5 -> h5_$TS"
        ls -td "$H5_BACKUP_DIR"/h5_* 2>/dev/null | tail -n +4 | while read d; do
            rm -rf "$d" && echo "[backup] 删除旧备份: $(basename $d)"
        done
    fi
}

backup_admin() {
    mkdir -p "$ADMIN_BACKUP_DIR"
    if [ -d "$ADMIN_DIR" ] && [ "$(ls -A $ADMIN_DIR 2>/dev/null)" ]; then
        TS=$(date +%Y%m%d_%H%M%S)
        cp -r "$ADMIN_DIR" "$ADMIN_BACKUP_DIR/admin_$TS"
        echo "[backup] 后台 -> admin_$TS"
        ls -td "$ADMIN_BACKUP_DIR"/admin_* 2>/dev/null | tail -n +4 | while read d; do
            rm -rf "$d" && echo "[backup] 删除旧备份: $(basename $d)"
        done
    fi
}

deploy_jar() {
    backup_jar
    cp /tmp/ruoyi-admin.jar "$JAR_PATH"
    echo "[deploy] JAR 已替换"
    docker restart java
    echo "[deploy] Java 容器已重启"
}

deploy_h5() {
    backup_h5
    rm -rf "$H5_DIR"/*
    cp -r /tmp/zhw_h5/. "$H5_DIR"/
    echo "[deploy] H5 已替换"
    docker exec nginx nginx -s reload
    echo "[deploy] Nginx 已热重载"
}

deploy_admin() {
    backup_admin
    rm -rf "$ADMIN_DIR"/*
    cp -r /tmp/zhw_admin/. "$ADMIN_DIR"/
    echo "[deploy] 后台已替换"
    docker exec nginx nginx -s reload
    echo "[deploy] Nginx 已热重载"
}

case "$MODE" in
    jar)    deploy_jar ;;
    h5)     deploy_h5 ;;
    admin)  deploy_admin ;;
    all)    deploy_jar; deploy_h5; deploy_admin ;;
    *)      echo "用法: deploy.sh jar|h5|admin|all"; exit 1 ;;
esac

echo "[done] 部署完成"
