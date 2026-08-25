import request from '@/utils/request'

export function listBanners() {
  return request({ url: '/admin/zhw/banner/list', method: 'get' })
}

export function getBanner(bannerId) {
  return request({ url: '/admin/zhw/banner/' + bannerId, method: 'get' })
}

export function addBanner(data) {
  return request({ url: '/admin/zhw/banner', method: 'post', data })
}

export function updateBanner(data) {
  return request({ url: '/admin/zhw/banner', method: 'put', data })
}

export function delBanner(bannerId) {
  return request({ url: '/admin/zhw/banner/' + bannerId, method: 'delete' })
}
