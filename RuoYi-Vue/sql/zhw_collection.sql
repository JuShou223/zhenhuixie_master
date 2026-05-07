-- 故事精选收集系统迁移脚本
-- 执行前确认 zhw_story 表存在

-- 1. 故事表添加精选标记字段
ALTER TABLE zhw_story
    ADD COLUMN is_collected TINYINT(1) DEFAULT 0 COMMENT '是否精选（0否 1是）' AFTER status;

-- 2. 管理后台菜单：精选库（归属 menu_id=2000 故事社区）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2005, '精选库', 2000, 5, 'collection', 'zhw/collection/index', 1, 0, 'C', '0', '0', 'zhw:collection:list', 'star', 'admin', NOW());

-- 按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2040, '精选标记', 2005, 1, '', '', 1, 0, 'F', '0', '0', 'zhw:collection:collect', '#', 'admin', NOW());
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2041, '导出精选', 2005, 2, '', '', 1, 0, 'F', '0', '0', 'zhw:collection:export', '#', 'admin', NOW());
