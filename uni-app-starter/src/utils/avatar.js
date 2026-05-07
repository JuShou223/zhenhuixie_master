/*
 * @Date: 2026-04-16 15:19:57
 * @LastEditors: 徐一鸣
 * @LastEditTime: 2026-04-16 15:28:58
 * @Description:
 */
import projectConfig from "@/env.config.js";

/**
 * 解析头像 URL
 * - 相对路径（/img/avatar/...）→ 拼接 baseUrl，换服务器只改 env.config.js
 * - 完整 URL（http/https）→ 直接使用
 * - 空值 → 本地占位图
 */
export function resolveAvatar(avatar) {
  if (!avatar) return "/static/avatar-default.png";
  if (avatar.startsWith("http://") || avatar.startsWith("https://"))
    return avatar;
  console.log(projectConfig.baseUrl + avatar);
  return projectConfig.baseUrl + avatar;
}
