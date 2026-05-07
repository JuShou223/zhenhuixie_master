# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目简介

**真会写** — 一个故事创作社区平台。用户可以创建故事、续写分支（树状结构）、评论互动、参与话题挑战，通过点赞/评论/续写获得积分。协作文本创作 + 社区互动。

## 项目组成

| 子项目 | 路径 | 说明 |
|--------|------|------|
| `RuoYi-Vue` | `./RuoYi-Vue/` | Spring Boot 后端 + Vue2 管理后台 |
| `uni-app-starter` | `./uni-app-starter/` | UniApp 移动端（微信小程序/H5/App） |
| `docker` | `./docker/data/` | 生产环境 Docker Compose 配置 |

---

## 常用命令

### 后端（RuoYi-Vue Spring Boot）

```bash
# 打包 JAR（跳过测试）
cd RuoYi-Vue
mvn clean package -Dmaven.test.skip=true
# 产物：ruoyi-admin/target/ruoyi-admin.jar

# 本地启动（需先启动 MySQL + Redis）
cd RuoYi-Vue/ruoyi-admin
mvn spring-boot:run
```

### 管理后台前端（ruoyi-ui，Vue2 + Vue CLI）

```bash
cd RuoYi-Vue/ruoyi-ui
npm install
npm run dev          # 开发服务器
npm run build:prod   # 生产构建，产物在 dist/
```

### 移动端（uni-app-starter，UniApp 3 + Vue 3）

```bash
cd uni-app-starter
npm install
npm run dev:h5       # H5 开发
npm run dev:mp-weixin # 微信小程序开发
```

### 部署（服务器）

优先使用 `/deploy` 技能，它读取项目根目录的 `deploy.yaml` 自动完成构建→上传→部署：

| 目标 | 说明 | 上传方式 |
|------|------|---------|
| `jar` | Spring Boot 后端 | scp 单文件 |
| `admin` | Vue2 管理后台 | `pack: tar`（tar 打包上传，服务端解压） |
| `h5` | UniApp H5 移动端 | `pack: tar` |

```bash
# 手动部署时（SSH 到服务器）
cd /var/data
docker compose up -d                  # 重建变更的容器
docker exec nginx nginx -s reload     # 热重载 nginx
docker logs -f java --tail=80         # 查看后端日志
```

执行 SQL（`-h 127.0.0.1` 必须加，否则 socket 报错）：
```bash
docker exec -i mysql mysql -h 127.0.0.1 -uroot -p'ZhenHuiXie@Mysql2026' zhenhuixie < /tmp/xxx.sql
```

**部署路径（服务器）：**
- 后端 JAR：`/var/data/java/ruoyi-admin.jar`
- 管理后台前端：`/var/data/nginx/admin/`（nginx 映射 `/admin/` 路径）
- H5 前端：`/var/data/nginx/h5/`

---

## 架构说明

### 后端模块结构

```
RuoYi-Vue/
├── ruoyi-admin/        # 启动入口，Controller 层
│   └── src/main/java/com/ruoyi/web/controller/
│       ├── story/      # 故事社区接口（11 个 Controller）
│       │   ├── ZhwStoryController.java       # /zhw/story 故事 CRUD + 排行榜
│       │   ├── ZhwBranchController.java      # /zhw/branch 分支续写
│       │   ├── ZhwCommentController.java     # /zhw/comment 评论
│       │   ├── ZhwInteractionController.java # /zhw/like /zhw/mark 点赞+标记
│       │   ├── ZhwNotificationController.java# /zhw/notification 消息通知
│       │   ├── ZhwTopicController.java       # /zhw/topic 话题挑战
│       │   ├── ZhwUserController.java        # /zhw/user 用户主页+编辑资料
│       │   ├── AdminZhwTopicController.java  # /admin/zhw/topic 话题管理（管理端）
│       │   ├── AdminZhwStoryController.java  # /admin/zhw/story 故事管理（管理端）
│       │   ├── AdminZhwCommentController.java# /admin/zhw/comment 评论审核（管理端）
│       │   └── AdminZhwDashboardController.java # /admin/zhw/dashboard 数据概览（管理端）
│       ├── common/     # 验证码、通用上传
│       ├── system/     # RuoYi 系统管理（用户/角色/菜单等）
│       └── monitor/    # 系统监控
├── ruoyi-system/       # Domain / Mapper / Service 层
│   └── src/main/java/com/ruoyi/system/
│       ├── domain/     # ZhwStory, ZhwBranch, ZhwComment, ZhwLike,
│       │               # ZhwMark, ZhwNotification, ZhwTopic, ZhwUserScore
│       ├── mapper/     # 对应 MyBatis Mapper 接口（8 个）
│       └── service/    # IZhwStoryService 等 6 个 Service
├── ruoyi-common/       # 公共工具
├── ruoyi-framework/    # Spring Security 配置
│   └── config/SecurityConfig.java  # @Anonymous 注解白名单
└── sql/
    ├── ry_20260321.sql      # RuoYi 系统表
    ├── quartz.sql           # 定时任务表
    ├── zhw_init.sql         # 故事社区 9 张业务表
    └── zhw_admin_menu.sql   # 管理后台菜单（menu_id 2000-2032）
```

Mapper XML 在 `ruoyi-system/src/main/resources/mapper/system/Zhw*Mapper.xml`。

**管理端 API 规范：**
- 路由前缀 `/admin/zhw/`，与用户端 `/zhw/` 分离
- 权限注解 `@PreAuthorize("@ss.hasRole('admin')")`（不用 `@Anonymous`）
- 复用现有 Service/Mapper，在接口上扩展 `adminXxx` 方法

**ruoyi-ui 管理后台业务页面（zhw）：**

```
ruoyi-ui/src/
├── views/zhw/
│   ├── dashboard/index.vue  # 数据概览（平台统计卡片 + 趋势 + Top10）
│   ├── topic/index.vue      # 话题管理（CRUD + 状态切换）
│   ├── story/index.vue      # 故事管理（列表 + 禁用/恢复 + 删除）
│   └── comment/index.vue    # 评论审核（列表 + 强制删除）
└── api/zhw/
    ├── dashboard.js         # 统计 API
    ├── topic.js             # 话题 CRUD API
    ├── story.js             # 故事管理 API
    └── comment.js           # 评论审核 API
```

菜单通过 `zhw_admin_menu.sql` 注入 `sys_menu`（menu_id 段 2000-2032）。

### 数据库

- 数据库名：`zhenhuixie`，连接：`mysql:3306`（Docker 内网）
- 故事社区表（9 张）：`zhw_story`, `zhw_branch`, `zhw_comment`, `zhw_like`, `zhw_mark`, `zhw_notification`, `zhw_topic`, `zhw_user_score`
- 热度算法：`heat_score = participants*30 + branches*30 + comments*3 + likes*1`
- 分支表支持 CTE 树状查询（MySQL 8.0）

### uni-app-starter 移动端架构

```
uni-app-starter/
├── pages/
│   ├── index/          # 首页（推荐/热门，无限滚动）
│   ├── story/          # 故事相关（创建、详情、分支创作、分支详情、评论、排行）
│   ├── my/             # 个人中心（主页、编辑资料、消息、标记更新）
│   ├── topic/          # 话题（列表、公告、广场）
│   ├── login/          # 登录
│   └── register/       # 注册
├── apis/modules/       # API 模块（story, branch, comment, interaction, notification, topic, user）
├── utils/              # timeago.js 等工具
└── pages.json          # 路由配置
```

**技术栈：** UniApp 3 + Vue 3 + Pinia + Tailwind CSS + uview-plus + Vite 5

### 组件使用规范

**图标**：所有图标必须使用 `src/static/css/iconfont.css` 中已定义的 class，禁止直接写不存在的 icon 名。使用前先在 iconfont.css 中确认 class 存在。常用可用图标举例：`icon-lucide-pencil-line`、`icon-lucide-chevron-left/right/up`、`icon-lucide-zap`、`icon-lucide-circle-question-mark`、`icon-lucide-user`、`icon-lucide-lock`、`icon-lucide-bookmark-check`、`icon-lucide-trash-2`、`icon-lucide-x`、`icon-lucide-plus`、`icon-lucide-search`、`icon-lucide-bell`、`icon-lucide-heart`、`icon-lucide-star`。

**弹框 / 对话框**：移动端页面（`.vue` 文件）中需要弹框时，必须使用 `src/components/modal.vue`，禁止使用 `uni.showModal()`。

```js
// ✅ 正确
import Modal from '@/components/modal.vue'
const modalRef = ref(null)
// template: <modal ref="modalRef" />
modalRef.value.openModal({
  type: 'delete',       // normal | confirm | delete | warning | error | input | multi-select
  title: '确认删除',
  content: '删除后无法恢复，确定继续吗？',
  confirmText: '确认删除',
  onConfirm: () => { /* ... */ }
})

// ❌ 禁止
uni.showModal({ title: '...', content: '...', success: ({ confirm }) => {} })
```

例外：纯 JS 工具模块（非 Vue 组件，如 `utils/upload.js`）无法挂载组件，用 `uni.showToast` + 延迟跳转替代。

**登录引导弹框**：需要登录才能操作时，使用 `src/components/login-modal.vue`，调用 `loginModalRef.value.openModal({ title, description, onConfirm })`。

### Docker 服务

| 容器 | 镜像 | 内网 IP | 说明 |
|------|------|---------|------|
| mysql | mysql:8.0 | 177.7.0.11 | 不暴露公网端口 |
| redis | redis:7.0.0 | 177.7.0.10 | 不暴露公网端口 |
| java | eclipse-temurin:17-jre | 177.7.0.13 | 8080:8080 |
| nginx | nginx:stable | 177.7.0.14 | 80:80, 443:443 |

### Nginx 路由

- `xymaitech.top` → `/usr/share/nginx/html`（ruoyi-ui 管理后台）
- `xymaitech.top/prod-api/*` → `java:8080/`（后台接口）
- `xymaitech.top/profile/*` → 上传文件

---

## 项目文件清单

详见 `项目文件清单.md`（65 个文件：1 SQL + 37 后端 Java + 27 前端 Vue/JS）
