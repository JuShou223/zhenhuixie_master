-- 敏感词表
CREATE TABLE IF NOT EXISTS `zhw_sensitive_word` (
  `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '敏感词ID',
  `word`        varchar(200) NOT NULL                COMMENT '敏感词内容',
  `category`    varchar(50)  DEFAULT ''              COMMENT '分类（色情/暴恐/反动/民生/贪腐/其他）',
  `create_by`   varchar(64)  DEFAULT ''              COMMENT '创建者',
  `create_time` datetime                             COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT ''              COMMENT '更新者',
  `update_time` datetime                             COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word` (`word`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='敏感词表';
