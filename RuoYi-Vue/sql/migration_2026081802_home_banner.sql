-- 迁移：新增首页活动横幅表
-- 用于首页顶部活动展示区，后台可配置多张横幅图片，前端未配置时该区域自动隐藏

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
