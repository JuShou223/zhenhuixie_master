/**
 * 对象转 URL query string
 * 例：{ a: 1, b: 2 } => "a=1&b=2&"
 */
export function tansParams(params) {
  let result = "";
  for (const key of Object.keys(params)) {
    const value = params[key];
    if (value === null || value === "" || typeof value === "undefined") continue;
    if (typeof value === "object") {
      for (const subKey of Object.keys(value)) {
        const subVal = value[subKey];
        if (subVal === null || subVal === "" || typeof subVal === "undefined") continue;
        result += encodeURIComponent(`${key}[${subKey}]`) + "=" + encodeURIComponent(subVal) + "&";
      }
    } else {
      result += encodeURIComponent(key) + "=" + encodeURIComponent(value) + "&";
    }
  }
  return result;
}

/**
 * 安全地返回页面（避免超过小程序 10 层页面栈限制）
 */
export function navigateBackTo(url) {
  const backUrl = url.startsWith("/") ? url.slice(1) : url;
  const pages = getCurrentPages();
  const index = pages.findIndex((p) => p.route === backUrl);
  if (index === -1) {
    uni.reLaunch({ url: "/" + backUrl });
    return;
  }
  uni.navigateBack({ delta: pages.length - index - 1 });
}
