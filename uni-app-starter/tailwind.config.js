/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./public/index.html", "./src/**/*.{html,js,ts,jsx,tsx,vue}"],
  safelist: [
    { pattern: /guide-.*/ },  // 保护所有 guide- 前缀的 class 不被 Tailwind JIT 清除
  ],
  corePlugins: {
    preflight: false,
  },
  theme: {
    extend: {
      fontFamily: {
        heading: ['"Outfit"', '"Noto Serif SC"', 'Georgia', '"Songti SC"', 'STSong', 'serif'],
        body: ['"Outfit"', '-apple-system', 'BlinkMacSystemFont', '"PingFang SC"', '"Hiragino Sans GB"', '"Microsoft YaHei"', 'sans-serif'],
        brand: ['"Noto Serif SC"', 'Georgia', '"Songti SC"', 'STSong', 'serif'],
        mono: ['"Outfit"', '"SF Mono"', '"Fira Code"', 'monospace'],
      },
      colors: {
        bg: "#FDFCF8",
        ink: "#1A1A1A",
        accent: "#FFC107",
        "accent-dark": "#E5A800",
        muted: "#8C8A84",
        line: "#E8E3DC",
        yellow: {
          50: "#FFF8E1",
          100: "#FFECB3",
          200: "#FFE082",
          300: "#FFD54F",
          400: "#FFCA28",
          500: "#FFC107",
          600: "#FFB300",
          700: "#FFA000",
          800: "#FF8F00",
          900: "#FF6F00",
        },
      },
      boxShadow: {
        card: '0 2px 8px rgba(26, 26, 26, 0.04)',
        'card-hover': '0 4px 12px rgba(26, 26, 26, 0.08)',
        fab: '0 4px 16px rgba(229, 168, 0, 0.35)',
      },
    },
  },
};
