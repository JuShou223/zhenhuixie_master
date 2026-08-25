-- 迁移：话题挑战表新增结构化活动信息字段（活动奖励/参与方式/作品要求/评选说明）
-- 用于话题详情页展示结构化的活动规则内容，替代/补充原有的 announcement 自由文本字段
-- 已存在的话题这些字段默认为空，前端会自动回退到只展示 announcement

ALTER TABLE `zhw_topic`
  ADD COLUMN `reward_text`  text COMMENT '活动奖励，每行一条，格式：标签|数值' AFTER `announcement`,
  ADD COLUMN `join_steps`   text COMMENT '参与方式，每行一条步骤' AFTER `reward_text`,
  ADD COLUMN `requirements` text COMMENT '作品要求，每行一条' AFTER `join_steps`,
  ADD COLUMN `judge_rule`   text COMMENT '评选说明' AFTER `requirements`;
