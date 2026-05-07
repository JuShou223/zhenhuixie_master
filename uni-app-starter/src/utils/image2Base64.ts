export const h5ImageToBase64 = (tempFilePath: string): Promise<string> => {
  return new Promise((resolve) => {
    const img = new Image()
    img.src = tempFilePath
    img.onload = () => {
      const canvas = document.createElement('canvas')
      canvas.width = img.width
      canvas.height = img.height
      canvas.getContext('2d')!.drawImage(img, 0, 0)
      const base64 = canvas.toDataURL('image/jpeg', 0.8)
      resolve(base64.replace(/^data:image\/jpeg;base64,/, ''))
    }
  })
}

export const mpImageToBase64 = (tempFilePath: string): Promise<string> => {
  return new Promise((resolve, reject) => {
    wx.getFileSystemManager().readFile({
      filePath: tempFilePath,
      encoding: 'base64',
      success: (res: { data: string }) => resolve(res.data),
      fail: reject,
    })
  })
}

export const appImageToBase64 = (tempFilePath: string): Promise<string> => {
  return new Promise((resolve, reject) => {
    plus.io.resolveLocalFileSystemURL(tempFilePath.replace('file://', ''), (entry: any) => {
      entry.file((file: any) => {
        const reader = new plus.io.FileReader()
        reader.onloadend = (e: any) => resolve(e.target.result)
        reader.onerror = reject
        reader.readAsDataURL(file)
      })
    })
  })
}
