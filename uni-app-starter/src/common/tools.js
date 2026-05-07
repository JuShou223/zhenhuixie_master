// 全局工具方法，挂载到 this.$t
// 页面中使用：this.$t.parseTime(date)  或  {{ $t.parseTime(item.createTime) }}

const install = (app) => {
  app.config.globalProperties.$t = {
    /**
     * 日期格式化
     * parseTime(new Date())          => "2026-04-13 16:30:00"
     * parseTime(date, "{y}/{m}/{d}") => "2026/04/13"
     */
    parseTime(time, pattern = "{y}-{m}-{d} {h}:{i}:{s}") {
      if (!time) return "";
      let date;
      if (typeof time === "object") {
        date = time;
      } else {
        if (typeof time === "string" && /^[0-9]+$/.test(time)) time = parseInt(time);
        if (typeof time === "string") {
          time = time.replace(/-/g, "/").replace("T", " ").replace(/\.\d{3}/g, "");
        }
        if (typeof time === "number" && time.toString().length === 10) time *= 1000;
        date = new Date(time);
      }
      const map = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay(),
      };
      return pattern.replace(/{(y|m|d|h|i|s|a)}/g, (_, key) => {
        let val = map[key];
        if (key === "a") return ["日", "一", "二", "三", "四", "五", "六"][val];
        return val < 10 ? "0" + val : val;
      });
    },

    // 空值转空字符串
    praseStrEmpty(str) {
      if (!str || str === "undefined" || str === "null") return "";
      return str;
    },

    // sleep
    wait(ms = 500) {
      return new Promise((resolve) => setTimeout(resolve, ms));
    },
  };
};

export default { install };
