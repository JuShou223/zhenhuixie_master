import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import { UnifiedViteWeappTailwindcssPlugin as uvwt } from "weapp-tailwindcss/vite";
import removeConsole from "vite-plugin-remove-console";

export default defineConfig({
  server: {
    proxy: {
      "/prod-api": {
        target: "http://8.147.71.151",
        changeOrigin: true,
      },
    },
  },
  plugins: [
    uni(),
    uvwt(),
    process.env.NODE_ENV === "production"
      ? removeConsole({ includes: ["debug"] })
      : null,
  ],
  css: {
    postcss: {
      plugins: [
        require("tailwindcss"),
        require("autoprefixer"),
        require("postcss-rem-to-responsive-pixel")({
          rootValue: 32,
          propList: ["*"],
          transformUnit: "rpx",
        }),
      ],
    },
    preprocessorOptions: {
      scss: {
        silenceDeprecations: ["legacy-js-api", "color-functions", "import"],
        additionalData: `@import "uview-plus/theme.scss";`,
      },
    },
  },
});
