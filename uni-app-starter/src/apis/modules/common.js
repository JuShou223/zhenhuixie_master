// 通用接口，按需修改或删除

// 获取验证码
export function captchaImage() {
  return uni.$u.http.get("/captchaImage");
}

// 账号密码登录（社区用户 → zhw_user）
export function login(params) {
  return uni.$u.http.post("/zhw/login", params);
}

// 用户注册（社区用户 → zhw_user）
export function register(params) {
  return uni.$u.http.post("/zhw/register", params);
}

// 退出登录
export function logout() {
  return uni.$u.http.post("/logout");
}

// 获取当前用户信息（从社区用户表）
export function getProfile() {
  return uni.$u.http.get("/zhw/user/profile");
}

// 文件上传（通用）
// 推荐直接用 utils/upload.js，这里只是示例
export function uploadFile(filePath) {
  return uni.$u.http.upload("/common/upload", {
    filePath,
    name: "file",
  });
}
