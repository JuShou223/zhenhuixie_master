-- 社区用户表（独立于系统 sys_user）
CREATE TABLE IF NOT EXISTS `zhw_user` (
  `user_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name`   varchar(30)  NOT NULL                COMMENT '用户名',
  `nick_name`   varchar(30)  DEFAULT ''              COMMENT '昵称',
  `avatar`      varchar(500) DEFAULT ''              COMMENT '头像URL',
  `remark`      varchar(200) DEFAULT ''              COMMENT '个人签名',
  `phone`       varchar(20)  DEFAULT ''              COMMENT '手机号',
  `gender`      char(1)      DEFAULT '0'             COMMENT '0未知 1男 2女',
  `birthday`    date         DEFAULT NULL            COMMENT '生日',
  `login_time`  datetime     DEFAULT NULL            COMMENT '最后登录时间',
  `login_ip`    varchar(50)  DEFAULT ''              COMMENT '最后登录IP',
  `password`    varchar(100) DEFAULT ''              COMMENT 'BCrypt密码',
  `status`      char(1)      DEFAULT '0'             COMMENT '0正常 1停用',
  `create_time` datetime                             COMMENT '创建时间',
  `update_time` datetime                             COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='社区用户表';

-- 迁移现有社区用户数据
INSERT INTO zhw_user (user_id, user_name, nick_name, avatar, remark, password, status, create_time)
SELECT user_id, user_name, nick_name, avatar, remark, password, status, create_time
FROM sys_user
WHERE user_id >= 100  -- admin(1) 和 ry(2) 是系统用户，不迁移
  AND NOT EXISTS (SELECT 1 FROM zhw_user WHERE zhw_user.user_id = sys_user.user_id);
