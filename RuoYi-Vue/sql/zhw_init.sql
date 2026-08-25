-- 真会写 (zhw) 业务表初始化 SQL
-- 建议 MySQL 8.0+（递归CTE、窗口函数支持）

-- ------------------------------------
-- 1. 故事主表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_story` (
  `story_id`       bigint       NOT NULL AUTO_INCREMENT COMMENT '故事ID',
  `title`          varchar(100) NOT NULL                COMMENT '故事标题',
  `cover`          varchar(500) DEFAULT ''              COMMENT '封面图片URL',
  `setting_content` varchar(300) NOT NULL               COMMENT '故事设定内容（15~300字）',
  `user_id`        bigint       NOT NULL                COMMENT '创建人用户ID',
  `view_count`     int          DEFAULT 0               COMMENT '浏览量',
  `like_count`     int          DEFAULT 0               COMMENT '点赞数',
  `comment_count`  int          DEFAULT 0               COMMENT '评论数',
  `branch_count`   int          DEFAULT 0               COMMENT '分支数量',
  `participant_count` int        DEFAULT 0              COMMENT '独立参与人数',
  `heat_score`     bigint       DEFAULT 0               COMMENT '热度分值=独立参与人数x30+分支数x30+评论数x3+点赞数x1',
  `status`         char(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `del_flag`       char(1)      DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
  `create_by`      varchar(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`    datetime                             COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`    datetime                             COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (`story_id`),
  KEY `idx_heat_score` (`heat_score` DESC),
  KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='故事主表';

-- ------------------------------------
-- 2. 故事分支表（树状结构）
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_branch` (
  `branch_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '分支ID',
  `story_id`       bigint       NOT NULL                COMMENT '所属故事ID',
  `parent_id`      bigint       DEFAULT 0               COMMENT '父节点ID（0=直接续写故事设定）',
  `parent_type`    tinyint      DEFAULT 0               COMMENT '父节点类型（0=故事设定 1=分支）',
  `chapter_num`    int          NOT NULL DEFAULT 1      COMMENT '章节序号（系统自动计算=父深度+1）',
  `chapter_title`  varchar(100) DEFAULT ''              COMMENT '用户自定义章节标题',
  `content`        varchar(300) NOT NULL                COMMENT '分支正文内容（15~300字）',
  `user_id`        bigint       NOT NULL                COMMENT '作者用户ID',
  `like_count`     int          DEFAULT 0               COMMENT '点赞数',
  `view_count`     int          DEFAULT 0               COMMENT '浏览量',
  `comment_count`  int          DEFAULT 0               COMMENT '评论数',
  `child_count`    int          DEFAULT 0               COMMENT '直接子分支数量',
  `del_flag`       char(1)      DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
  `create_by`      varchar(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`    datetime                             COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`    datetime                             COMMENT '更新时间',
  PRIMARY KEY (`branch_id`),
  KEY `idx_story_id` (`story_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='故事分支表';

-- ------------------------------------
-- 3. 评论表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_comment` (
  `comment_id`        bigint       NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `story_id`          bigint       NOT NULL                COMMENT '所属故事ID',
  `branch_id`         bigint       DEFAULT 0               COMMENT '所属分支ID（0=故事评论）',
  `parent_id`         bigint       DEFAULT 0               COMMENT '父评论ID（0=根评论）',
  `reply_to_user_id`  bigint       DEFAULT 0               COMMENT '回复的目标用户ID',
  `content`           varchar(500) NOT NULL                COMMENT '评论内容',
  `user_id`           bigint       NOT NULL                COMMENT '评论者用户ID',
  `like_count`        int          DEFAULT 0               COMMENT '点赞数',
  `del_flag`          char(1)      DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
  `create_by`         varchar(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`       datetime                             COMMENT '创建时间',
  `update_by`         varchar(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`       datetime                             COMMENT '更新时间',
  PRIMARY KEY (`comment_id`),
  KEY `idx_story_id`  (`story_id`),
  KEY `idx_branch_id` (`branch_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ------------------------------------
-- 4. 点赞表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_like` (
  `like_id`      bigint  NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `target_id`    bigint  NOT NULL                COMMENT '目标ID',
  `target_type`  tinyint NOT NULL                COMMENT '目标类型（0=故事 1=分支 2=评论）',
  `user_id`      bigint  NOT NULL                COMMENT '点赞用户ID',
  `create_time`  datetime                        COMMENT '创建时间',
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ------------------------------------
-- 5. 标记/订阅表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_mark` (
  `mark_id`        bigint  NOT NULL AUTO_INCREMENT COMMENT '标记ID',
  `story_id`       bigint  NOT NULL                COMMENT '所属故事ID',
  `branch_id`      bigint  DEFAULT 0               COMMENT '标记的分支ID（0=标记故事设定本身）',
  `user_id`        bigint  NOT NULL                COMMENT '用户ID',
  `last_read_time` datetime                        COMMENT '最后阅读时间（用于判断有无新内容）',
  `create_time`    datetime                        COMMENT '创建时间',
  PRIMARY KEY (`mark_id`),
  UNIQUE KEY `uk_user_story_branch` (`user_id`, `story_id`, `branch_id`),
  KEY `idx_story_id`  (`story_id`),
  KEY `idx_branch_id` (`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='标记订阅表';

-- ------------------------------------
-- 6. 消息通知表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_notification` (
  `notif_id`    bigint  NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id`     bigint  NOT NULL                COMMENT '接收通知的用户ID',
  `sender_id`   bigint  NOT NULL                COMMENT '触发通知的用户ID',
  `notif_type`  tinyint NOT NULL                COMMENT '通知类型（0=被赞 1=被评论 2=被回复 3=被续写）',
  `target_id`   bigint  NOT NULL                COMMENT '目标ID（故事/分支/评论ID）',
  `target_type` tinyint NOT NULL                COMMENT '目标类型（0=故事 1=分支 2=评论）',
  `story_id`    bigint  DEFAULT 0               COMMENT '关联故事ID（便于前端跳转）',
  `is_read`     tinyint DEFAULT 0               COMMENT '是否已读（0=未读 1=已读）',
  `create_time` datetime                        COMMENT '创建时间',
  PRIMARY KEY (`notif_id`),
  KEY `idx_user_id`    (`user_id`),
  KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- ------------------------------------
-- 7. 话题挑战表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_topic` (
  `topic_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '话题ID',
  `title`        varchar(100) NOT NULL                COMMENT '话题标题',
  `banner`       varchar(500) DEFAULT ''              COMMENT '话题封面图片',
  `announcement` text                                COMMENT '活动公告（富文本/纯文本）',
  `reward_text`  text                                COMMENT '活动奖励，每行一条，格式：标签|数值',
  `join_steps`   text                                COMMENT '参与方式，每行一条步骤',
  `requirements` text                                COMMENT '作品要求，每行一条',
  `judge_rule`   text                                COMMENT '评选说明',
  `start_time`   datetime                            COMMENT '活动开始时间',
  `end_time`     datetime                            COMMENT '活动结束时间',
  `status`       char(1)      DEFAULT '0'             COMMENT '状态（0进行中 1已结束）',
  `del_flag`     char(1)      DEFAULT '0'             COMMENT '删除标志（0存在 2删除）',
  `create_by`    varchar(64)  DEFAULT ''              COMMENT '创建者',
  `create_time`  datetime                            COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''              COMMENT '更新者',
  `update_time`  datetime                            COMMENT '更新时间',
  PRIMARY KEY (`topic_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='话题挑战表';

-- ------------------------------------
-- 8. 故事-话题关联表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_story_topic` (
  `id`          bigint  NOT NULL AUTO_INCREMENT,
  `story_id`    bigint  NOT NULL COMMENT '故事ID',
  `topic_id`    bigint  NOT NULL COMMENT '话题ID',
  `create_time` datetime        COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_story_topic` (`story_id`, `topic_id`),
  KEY `idx_topic_id` (`topic_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='故事话题关联表';

-- ------------------------------------
-- 9. 用户故事积分表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_user_score` (
  `score_id`    bigint NOT NULL AUTO_INCREMENT COMMENT '积分记录ID',
  `user_id`     bigint NOT NULL                COMMENT '用户ID',
  `story_id`    bigint NOT NULL                COMMENT '所属故事ID',
  `score`       int    DEFAULT 0               COMMENT '积分（被赞x1 被评论x3 被标记x20 被续写x30）',
  `create_time` datetime                       COMMENT '创建时间',
  `update_time` datetime                       COMMENT '更新时间',
  PRIMARY KEY (`score_id`),
  UNIQUE KEY `uk_user_story` (`user_id`, `story_id`),
  KEY `idx_story_id` (`story_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='用户故事积分表';

-- ------------------------------------
-- 10. 首页活动横幅表
-- ------------------------------------
CREATE TABLE IF NOT EXISTS `zhw_home_banner` (
  `banner_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '横幅ID',
  `image`       varchar(255) NOT NULL                COMMENT '图片URL',
  `link_url`    varchar(255) DEFAULT ''              COMMENT '点击跳转的小程序内部路径，可为空',
  `sort_order`  int          DEFAULT 0               COMMENT '排序，越小越靠前',
  `status`      char(1)      DEFAULT '0'             COMMENT '状态（0启用 1禁用）',
  `create_by`   varchar(64)  DEFAULT ''              COMMENT '创建者',
  `create_time` datetime                             COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT ''              COMMENT '更新者',
  `update_time` datetime                             COMMENT '更新时间',
  PRIMARY KEY (`banner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='首页活动横幅表';
