-- 真会写后台管理菜单
-- 安全设计：每个 INSERT 都带 ON DUPLICATE KEY UPDATE（覆盖所有字段），
-- 即使重复执行也不会丢失数据，新部署和老环境升级都安全。
-- menu_id 段：2000-2099

-- 一级菜单：故事社区
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2000, '故事社区', 0, 10, 'zhw', NULL, 1, 0, 'M', '0', '0', '', 'edit', 'admin', NOW(), '', NULL, '真会写业务管理')
ON DUPLICATE KEY UPDATE
  menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component),
  perms=VALUES(perms), icon=VALUES(icon), remark=VALUES(remark);

-- 二级菜单：数据概览
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2001, '数据概览', 2000, 1, 'dashboard', 'zhw/dashboard/index', 1, 0, 'C', '0', '0', 'zhw:dashboard:view', 'dashboard', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
  menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component),
  perms=VALUES(perms), icon=VALUES(icon);

-- 二级菜单：话题管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2002, '话题管理', 2000, 2, 'topic', 'zhw/topic/index', 1, 0, 'C', '0', '0', 'zhw:topic:list', 'list', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
  menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component),
  perms=VALUES(perms), icon=VALUES(icon);

-- 二级菜单：故事管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2003, '故事管理', 2000, 3, 'story', 'zhw/story/index', 1, 0, 'C', '0', '0', 'zhw:story:list', 'documentation', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
  menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component),
  perms=VALUES(perms), icon=VALUES(icon);

-- 二级菜单：评论审核
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2004, '评论审核', 2000, 4, 'comment', 'zhw/comment/index', 1, 0, 'C', '0', '0', 'zhw:comment:list', 'message', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
  menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component),
  perms=VALUES(perms), icon=VALUES(icon);

-- 二级菜单：精选故事
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2005, '精选故事', 2000, 5, 'collection', 'zhw/collection/index', 1, 0, 'C', '0', '0', 'zhw:collection:list', 'star', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
  menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component),
  perms=VALUES(perms), icon=VALUES(icon);

-- 二级菜单：敏感词管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2006, '敏感词管理', 2000, 6, 'sensitive', 'zhw/sensitiveWord/index', 1, 0, 'C', '0', '0', 'zhw:sensitive:list', 'lock', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
  menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component),
  perms=VALUES(perms), icon=VALUES(icon);

-- 按钮权限（故事管理）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2010, '故事禁用', 2003, 1, '', '', 1, 0, 'F', '0', '0', 'zhw:story:disable', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2011, '故事删除', 2003, 2, '', '', 1, 0, 'F', '0', '0', 'zhw:story:delete', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);

-- 按钮权限（评论审核）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2020, '评论删除', 2004, 1, '', '', 1, 0, 'F', '0', '0', 'zhw:comment:delete', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);

-- 按钮权限（话题管理）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2030, '话题新增', 2002, 1, '', '', 1, 0, 'F', '0', '0', 'zhw:topic:add', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2031, '话题编辑', 2002, 2, '', '', 1, 0, 'F', '0', '0', 'zhw:topic:edit', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2032, '话题删除', 2002, 3, '', '', 1, 0, 'F', '0', '0', 'zhw:topic:delete', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);

-- 按钮权限（敏感词管理）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2040, '敏感词新增', 2006, 1, '', '', 1, 0, 'F', '0', '0', 'zhw:sensitive:add', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2041, '敏感词编辑', 2006, 2, '', '', 1, 0, 'F', '0', '0', 'zhw:sensitive:edit', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2042, '敏感词删除', 2006, 3, '', '', 1, 0, 'F', '0', '0', 'zhw:sensitive:delete', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2043, '敏感词刷新', 2006, 4, '', '', 1, 0, 'F', '0', '0', 'zhw:sensitive:reload', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);

-- 二级菜单：用户管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2007, '用户管理', 2000, 7, 'user', 'zhw/user/index', 1, 0, 'C', '0', '0', 'zhw:user:list', 'user', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), icon=VALUES(icon);

-- 按钮权限（用户管理）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2050, '用户详情', 2007, 1, '', '', 1, 0, 'F', '0', '0', 'zhw:user:detail', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2051, '用户停用', 2007, 2, '', '', 1, 0, 'F', '0', '0', 'zhw:user:disable', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2052, '密码重置', 2007, 3, '', '', 1, 0, 'F', '0', '0', 'zhw:user:resetPwd', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);

-- 二级菜单：首页横幅管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2008, '首页横幅管理', 2000, 8, 'homeBanner', 'zhw/homeBanner/index', 1, 0, 'C', '0', '0', 'zhw:homeBanner:list', 'picture', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), icon=VALUES(icon);

-- 按钮权限（首页横幅管理）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2053, '横幅新增', 2008, 1, '', '', 1, 0, 'F', '0', '0', 'zhw:homeBanner:add', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2054, '横幅编辑', 2008, 2, '', '', 1, 0, 'F', '0', '0', 'zhw:homeBanner:edit', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (2055, '横幅删除', 2008, 3, '', '', 1, 0, 'F', '0', '0', 'zhw:homeBanner:remove', '#', 'admin', NOW())
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), perms=VALUES(perms), parent_id=VALUES(parent_id);
