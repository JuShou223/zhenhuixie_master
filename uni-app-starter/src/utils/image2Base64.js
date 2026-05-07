/**
 * 图片转 base64（三端兼容）
 *
 * 用法：
 *   import { h5ImageToBase64, appImageToBase64, mpImageToBase64 } from '@/utils/image2Base64.js'
 *
 *   // #ifdef H5
 *   const base64 = await h5ImageToBase64(tempFilePath)
 *   // #endif
 *   // #ifdef MP-WEIXIN
 *   const base64 = await mpImageToBase64(tempFilePath)
 *   // #endif
 *   // #ifdef APP-PLUS
 *   const base64 = await appImageToBase64(tempFilePath)
 *   // #endif
 */

export const h5ImageToBase64 = (tempFilePath) => {
  return new Promise((resolve) => {
    const img = new Image();
    img.src = tempFilePath;
    img.onload = () => {
      const canvas = document.createElement("canvas");
      canvas.width = img.width;
      canvas.height = img.height;
      canvas.getContext("2d").drawImage(img, 0, 0);
      const base64 = canvas.toDataURL("image/jpeg", 0.8);
      resolve(base64.replace(/^data:image\/jpeg;base64,/, ""));
    };
  });
};

export const mpImageToBase64 = (tempFilePath) => {
  return new Promise((resolve, reject) => {
    wx.getFileSystemManager().readFile({
      filePath: tempFilePath,
      encoding: "base64",
      success: (res) => resolve(res.data),
      fail: reject,
    });
  });
};

export const appImageToBase64 = (tempFilePath) => {
  return new Promise((resolve, reject) => {
    plus.io.resolveLocalFileSystemURL(tempFilePath.replace("file://", ""), (entry) => {
      entry.file((file) => {
        const reader = new plus.io.FileReader();
        reader.onloadend = (e) => resolve(e.target.result);
        reader.onerror = reject;
        reader.readAsDataURL(file);
      });
    });
  });
};
