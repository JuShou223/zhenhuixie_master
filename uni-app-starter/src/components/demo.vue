<template>
  <view class="min-h-screen bg-slate-50 relative z-50">

    <view
      class="animate-fade-in bg-emerald-600 h-64 relative overflow-hidden rounded-b-[2.5rem] shadow-lg shadow-emerald-200">
      <view
        class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full blur-3xl -mr-16 -mt-16 pointer-events-none">
      </view>
      <view
        class="absolute bottom-0 left-0 w-48 h-48 bg-teal-500/30 rounded-full blur-2xl -ml-10 -mb-10 pointer-events-none">
      </view>

      <view class="px-6 py-4 flex justify-between items-center relative z-10 text-white"
        :style="{ paddingTop: safeAreaTop + 'px' }">
        <!-- <button
          class="p-2 bg-white/20 rounded-full hover:bg-white/30 transition-colors backdrop-blur-md m-0 leading-none border-none">
          <text class="leading-none iconfont icon-lucide-chevron-left text-2xl text-white"></text>
        </button> -->
        <text class="font-bold text-lg">个人中心</text>
        <!-- <button class="p-2 hover:bg-white/20 rounded-full transition-colors m-0 leading-none bg-transparent">
          <text class="leading-none iconfont icon-lucide-settings text-2xl text-white"></text>
        </button> -->
      </view>

      <view class="px-6 pt-2 flex items-center gap-4 relative z-10">
        <view
          class="flex items-center justify-center w-20 h-20 rounded-full border-4 border-solid border-white/30 shadow-xl overflow-hidden bg-white shrink-0"
          @click="handleToAvatar">
          <!-- <text class="leading-none iconfont icon-lucide-sprout text-2xl text-emerald-600 leading-none"></text> -->
          <image v-if="avatarUrl" :src="avatarUrl" mode="aspectFill" class="w-full h-full" />
          <view v-else class="w-full h-full flex justify-center items-center">
            <text class="iconfont icon-lucide-sprout text-4xl text-emerald-600"></text>
          </view>
        </view>
        <view class="text-white">
          <text class="block text-2xl font-bold">{{ isGuest ? '访客用户' : profile.nickName || '未登录' }}</text>
          <view class="flex items-center gap-2 mt-1">
            <text v-if="isGuest"
              class="bg-emerald-700/50 backdrop-blur-md px-2 py-0.5 rounded text-[10px] font-bold border border-solid border-emerald-400/30">
              体验模式
            </text>
            <text v-else
              class="bg-emerald-700/50 backdrop-blur-md px-2 py-0.5 rounded text-[10px] font-bold border border-solid border-emerald-400/30"
              @click="handleVipUpgrade">
              {{ isVipModel ? 'VIP会员' : '养植爱好者' }}
            </text>
            <!-- <text class="text-xs text-emerald-100 opacity-80">ID: {{ profile.userId || '884920' }}</text> -->
          </view>
        </view>
      </view>
    </view>

    <view class="animate-fade-in px-6 -mt-12 relative z-20 pb-4 space-y-6">

      <!-- <view class="bg-white rounded-2xl p-6 shadow-xl shadow-slate-200/50 flex justify-between items-center">
				<view class="text-center flex-1 border-r border-slate-100">
					<text class="block text-2xl font-bold text-slate-800">342</text>
					<text class="block text-xs text-slate-400 mt-1">种植天数</text>
				</view>
				<view class="text-center flex-1 border-r border-slate-100">
					<text class="block text-2xl font-bold text-slate-800">12</text>
					<text class="block text-xs text-slate-400 mt-1">我的植物</text>
				</view>
				<view class="text-center flex-1">
					<text class="block text-2xl font-bold text-slate-800">5</text>
					<text class="block text-xs text-slate-400 mt-1">诊断记录</text>
				</view>
			</view> -->
      <!-- Guest Mode: Call to Action Banner -->
      <view v-if="isGuest" style="background-image: linear-gradient(to right, #10b981, #14b8a6)"
        class="bg-gradient-to-r from-emerald-500 to-teal-500 rounded-2xl p-6 text-white shadow-lg relative overflow-hidden">
        <view class="relative z-10">
          <view class="flex items-center gap-2 mb-1">
            <text class="iconfont icon-lucide-shield text-xl"></text>
            <text class="font-bold text-lg">注册正式账号</text>
          </view>
          <text class="text-xs text-emerald-100 mb-4 leading-relaxed opacity-90 block">
            解锁云端数据同步、历史订单查询及专属 AI 诊断报告永久保存功能。
          </text>
          <button @click="handleLoginClick"
            class="bg-white text-emerald-600 px-6 py-2.5 rounded-xl text-xs font-bold shadow-md hover:bg-emerald-50 transition-colors w-fit m-0 border-none">
            立即注册 / 登录
          </button>
        </view>
        <text
          class="iconfont icon-lucide-log-in absolute -right-4 -bottom-4 text-[128px] text-white/10 rotate-[-15deg]"></text>
      </view>

      <view v-if="!isGuest" class="bg-white rounded-2xl p-5 shadow-sm border border-solid border-slate-100">
        <view class="flex justify-between items-center mb-4">
          <text class="font-bold text-slate-800 text-sm">我的订单</text>
          <view @click="goOrders('all')" class="text-xs text-slate-400 flex items-center hover:text-emerald-600">
            <text>全部订单</text>
            <text class="leading-none iconfont icon-lucide-chevron-right text-xs ml-0.5"></text>
          </view>
        </view>
        <view class="flex justify-between px-2">
          <view @click="goOrders(item.value)" v-for="(item, idx) in orderItems" :key="idx"
            class="flex flex-col items-center gap-2 group">
            <view
              class="w-10 h-10 rounded-full bg-slate-50 text-slate-500 flex items-center justify-center group-hover:bg-emerald-50 group-hover:text-emerald-600 transition-colors relative">
              <text class="leading-none iconfont text-xl" :class="item.iconClass"></text>
              <view v-if="orderStats && orderStats[item.value]"
                class="absolute -top-1 -right-1 w-4 h-4 bg-rose-500 text-white text-[10px] flex items-center justify-center rounded-full border-2 border-white">
                {{ orderStats[item.value] }}
              </view>
            </view>
            <text class="text-xs text-slate-600">{{ item.label }}</text>
          </view>
        </view>
      </view>

      <view v-if="!isGuest"
        class="bg-white rounded-2xl shadow-sm border border-solid border-slate-100 overflow-hidden mt-6">
        <view @click="gotoAccount"
          class="box-border w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors border-b border-slate-50 cursor-pointer">
          <view class="flex items-center gap-3">
            <view class="p-2 bg-blue-50 text-blue-600 rounded-lg">
              <text class="leading-none iconfont icon-lucide-user text-xl"></text>
            </view>
            <text class="text-sm font-bold text-slate-700">账号信息</text>
          </view>
          <text class="leading-none iconfont icon-lucide-chevron-right text-base text-slate-400"></text>
        </view>

        <view @click="gotoAddressList"
          class="box-border w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors border-b border-slate-50 cursor-pointer">
          <view class="flex items-center gap-3">
            <view class="p-2 bg-blue-50 text-blue-600 rounded-lg">
              <text class="leading-none iconfont icon-lucide-map-pin text-xl"></text>
            </view>
            <text class="text-sm font-bold text-slate-700">地址管理</text>
          </view>
          <text class="leading-none iconfont icon-lucide-chevron-right text-base text-slate-400"></text>
        </view>

        <!-- <view @click="onNavigate('DIAGNOSIS_HISTORY')"
          class="box-border w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors border-b border-slate-50 cursor-pointer">
          <view class="flex items-center gap-3">
            <view class="p-2 bg-orange-50 text-orange-600 rounded-lg">
              <text class="leading-none iconfont icon-lucide-clipboard-list text-xl"></text>
            </view>
            <text class="text-sm font-bold text-slate-700">植物诊断档案</text>
          </view>
          <text class="leading-none iconfont icon-lucide-chevron-right text-base text-slate-400"></text>
        </view> -->

        <!-- <view @click="gotoResetPsd"
					class="box-border w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors border-b border-slate-50 cursor-pointer">
					<view class="flex items-center gap-3">
						<view class="p-2 bg-purple-50 text-purple-600 rounded-lg">
							<text class="leading-none iconfont icon-lucide-lock text-xl"></text>
						</view>
						<text class="text-sm font-bold text-slate-700">修改密码</text>
					</view>
					<text class="leading-none iconfont icon-lucide-chevron-right text-base text-slate-400"></text>
				</view> -->

        <view @click="handleShowModal('clearStorage')"
          class="box-border w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors border-b border-slate-50 cursor-pointer">
          <view class="flex items-center gap-3">
            <view class="p-2 bg-rose-50 text-rose-600 rounded-lg">
              <text class="leading-none iconfont icon-lucide-trash-2 text-xl"></text>
            </view>
            <text class="text-sm font-bold text-slate-700">清除缓存</text>
          </view>
          <text class="leading-none iconfont icon-lucide-chevron-right text-base text-slate-400"></text>
        </view>

        <!-- <view @click="handleBindWeChart"
          class="box-border w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors border-b border-slate-50 cursor-pointer">
          <view class="flex items-center gap-3">
            <view class="p-2 bg-green-50 text-green-600 rounded-lg">
              <text class="leading-none iconfont icon-lucide-link text-xl"></text>
            </view>
            <text class="text-sm font-bold text-slate-700">{{ wxStatus ? '解绑微信' : '绑定微信' }}</text>
          </view>
          <text class="leading-none iconfont icon-lucide-chevron-right text-base text-slate-400"></text>
        </view> -->

        <!-- <view
          class="box-border w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors cursor-pointer">
          <view class="flex items-center gap-3">
            <view class="p-2 bg-teal-50 text-teal-600 rounded-lg">
              <text class="leading-none iconfont icon-lucide-help-circle text-xl"></text>
            </view>
            <text class="text-sm font-bold text-slate-700">帮助与反馈</text>
          </view>
          <text class="leading-none iconfont icon-lucide-chevron-right text-base text-slate-400"></text>
        </view> -->

      </view>

      <button v-if="isGuest" @click="handleLoginClick"
        class="mt-4 w-full py-4 bg-slate-900 text-white font-bold rounded-2xl flex items-center justify-center gap-2 shadow-lg hover:bg-slate-800 active:scale-95 transition-all">
        <text class="iconfont icon-lucide-log-in text-xl"></text>
        <text>切换 / 登录账号</text>
      </button>
      <template v-else>
        <button @click="handleShowModal('exit')"
          class="mt-4 w-full py-4 bg-slate-100 text-slate-500 font-bold rounded-2xl flex items-center justify-center gap-2 hover:bg-rose-50 hover:text-rose-600 transition-colors m-0 border-none">
          <text class="leading-none iconfont icon-lucide-log-out text-xl"></text>
          <text>退出登录</text>
        </button>

        <button @click="handleShowModal('signout')"
          class="mt-4 w-full py-4 bg-slate-50 text-slate-400 font-bold rounded-2xl flex items-center justify-center gap-2 hover:bg-rose-50 hover:text-rose-600 transition-colors m-0 border-none mt-2">
          <text class="leading-none iconfont icon-lucide-trash-2 text-xl"></text>
          <text>注销账户</text>
        </button>
      </template>
      <text class="block text-center text-[10px] text-slate-300 pt-4 pb-4">
        猴卫士智能养植 v1.0.0 • Build 20251231
      </text>
    </view>

    <vip-model v-if="isVipModel" @close="isVipModel = false"></vip-model>
    <hws-modal ref="hwsModalRef"></hws-modal>
    <hws-tab-bar defaultTab="user"></hws-tab-bar>
  </view>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { useUserStore } from '@/stores/user';
import projectConfig from '@/env.config.js';
import vipModel from '@/components/model/vip-model.vue';
import { logout, wechatBind, getProfile as getProfileApi } from '@/apis/modules/common';
import { useTabbar } from '@/hooks/useTabbar'
import { onLoad, onShow, onHide, onPullDownRefresh } from '@dcloudio/uni-app';
import { useStoreProducts } from '@/hooks/useShop'
import bus from '@/common/bus.js';

const emit = defineEmits(['back', 'logout', 'navigate']);
const userStore = useUserStore();
const { handleSetTabbar } = useTabbar()
// const orderStats =
const {
  ORDER_STATUS,
  getOrderStats,
  orders
} = useStoreProducts()

const isGuest = computed(() => userStore.isGuest);

// Safe Area
const safeAreaTop = ref(44);
const hwsModalRef = ref(null);
onMounted(() => {
  try {
    const res = uni.getSystemInfoSync();
    if (res.safeAreaInsets && res.safeAreaInsets.top) {
      safeAreaTop.value = res.safeAreaInsets.top;
    }
  } catch (e) {
    console.error(e);
  }

  // Fetch profile on mount
});

onLoad(() => {
  getProfile();
  // #ifndef MP-WEIXIN
  try {
    uni.hideTabBar()
  } catch (err) {
    //
  }
  // #endif
})

onShow(() => {
  loaderStats()
  handleSetTabbar('user')
})


// Data
const orderItems = [
  { label: '待付款', value: ORDER_STATUS.PENDING_PAYMENT, iconClass: 'icon-lucide-credit-card' },
  { label: '待发货', value: ORDER_STATUS.PENDING_SHIPPING, iconClass: 'icon-lucide-package' },
  { label: '待收货', value: ORDER_STATUS.PENDING_RECEIVING, iconClass: 'icon-lucide-truck' },
  { label: '已完成', value: ORDER_STATUS.COMPLETED, iconClass: 'icon-lucide-message-circle' },
];

// Reactive State
const avatarUrl = ref('');
const wxStatus = ref(false);
const isVipModel = ref(false);
const profile = ref({});
const orderStats = ref(null)
// Methods

// 获取用户信息
const getProfile = () => {
  getProfileApi().then(res => {
    // console.log('res', res)
    if (res.code === 200) {
      userStore.setProfile(res.data);
      profile.value = res.data;
      avatarUrl.value = profile.value.avatar && projectConfig.baseUrl + profile.value.avatar;
      wxStatus.value = res.wxBind;
    }
  }).catch(err => {
    console.error('获取用户信息失败', err);
    uni.showToast({ title: err.msg || '获取用户信息失败', icon: 'none' });
  });
};

const handleShowModal = (key) => {
  if (!hwsModalRef.value) return;
  switch (key) {
    case 'clearStorage':
      hwsModalRef.value.openModal({
        type: 'delete',
        title: '清除缓存？',
        content: '这将重置应用引导状态、登录信息及本地存储的所有数据。确定要继续吗？',
        confirmText: '确认清除',
        onConfirm: () => {
          handleClearConfirm()
        }
      })
      break;
    case 'exit':
      hwsModalRef.value.openModal({
        type: 'delete',
        title: '退出登录？',
        content: '确定要退出当前账号吗？',
        confirmText: '确认退出',
        onConfirm: () => {
          confirmExit()
        }
      })
      break;
    case 'signout':
      hwsModalRef.value.openModal({
        type: 'delete',
        title: '注销账户？',
        content: '注销后账号无法找回，确定要注销吗？',
        confirmText: '确认',
        onConfirm: () => {
          confirmUnsubscribe()
        }
      })
      break;
    default:
      break;
  }
};

const clearToken = () => {
  uni.setStorageSync('token', '');
};

const getAllStorageKeys = () => {
  return new Promise((resolve, reject) => {
    // 方法1: 使用 uni.getStorageInfo
    uni.getStorageInfo({
      success: (res) => {
        resolve(res.keys || [])
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

const handleClearConfirm = async () => {

  // 获取所有 storage key
  const allKeys = await getAllStorageKeys()

  // 需要保留的key
  const excludeKeys = [
    'token',                      // 登录token
    'lifeData',              // 刷新token
    // ...customExcludeKeys          // 自定义保留
  ].filter(Boolean)

  // 需要删除的key
  const keysToRemove = allKeys.filter(key => !excludeKeys.includes(key))

  // let successCount = 0
  // let failCount = 0

  // 批量删除
  for (let i = 0; i < keysToRemove.length; i++) {
    const key = keysToRemove[i]
    try {
      uni.removeStorageSync(key)
      // successCount++
    } catch (error) {
      console.warn(`删除缓存失败: ${key}`, error)
      // failCount++
    }
  }

};

const confirmExit = () => {
  logout().then(() => {
    clearToken();
    getProfile();
    uni.showToast({ title: '已退出登录', icon: 'success' });
    bus.emit('refreshIndex')
    // uni.reLaunch({
    // 	url: '/pages/login/index'
    // });
  });
};

const gotoAddressList = () => {
  uni.navigateTo({ url: '/pages-plants/shop/addressList/addressList?mode=edit' });
};

const gotoAccount = () => {
  uni.navigateTo({ url: '/pagesB/user/account' });
};

const gotoResetPsd = () => {
  uni.navigateTo({ url: '/pagesB/user/resetPsd' });
};

const confirmUnsubscribe = () => {
  logout().then(() => {
    clearToken();
    uni.reLaunch({
      url: '/pages/login/index'
    });
  });
};

const handleToAvatar = () => {
  const source = {
    album: '从手机相册选择',
    camera: '拍照',
  };


  const success = (res) => {
    const image = res.tempFiles[0].tempFilePath;
    uni.navigateTo({
      url: `/pagesB/user/avatar?url=${image}`
    });
  };

  const _uploadImage = (type) => {
    const sizeType = ['original', 'compressed'];
    uni.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType,
      sourceType: [type],
      success
    });
  }

  const list = Object.entries(source);
  // #ifdef H5
  _uploadImage(list[0][0]);
  return;
  // #endif

  uni.showActionSheet({
    itemList: list.map(v => v[1]),
    success: ({ tapIndex: i }) => {
      // Note: Permissions handling for APP-PLUS might need specific plugin or API
      // dispatching to store is one way if you have that setup
      _uploadImage(list[i][0]);
    }
  });
};

const handleVipUpgrade = () => {
  isVipModel.value = true;
};

const goOrders = (activeTab) => {
  uni.navigateTo({
    url: '/pages-plants/shop/orders/orders?activeTab=' + activeTab
  })
};

const loaderStats = async () => {
  console.log('loaderStats', orders.value)
  orderStats.value = await getOrderStats()
  console.log('loaderStats', orderStats.value)
}

const handleLoginClick = () => {
  uni.reLaunch({
    url: '/pages/login/index'
  });
};

</script>

<style scoped>
/* leading-none iconfont Helper */
.leading-none iconfont {
  display: inline-block;
  line-height: 1;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeInUp 0.3s ease-out;
}
</style>