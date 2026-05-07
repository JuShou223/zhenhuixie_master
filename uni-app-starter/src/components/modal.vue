<!--
 * @Date: 2025-12-09 16:40:17
 * @LastEditors: 徐一鸣
 * @LastEditTime: 2026-05-07
 * @Description: 多功能弹窗组件
-->
<template>
  <up-popup
    overlayStyle="background-color: rgba(26, 26, 26, 0.55); backdrop-filter: blur(6px);"
    :show="show"
    round="0"
    :safeAreaInsetBottom="false"
    mode="center"
    @close="handleCancel"
  >
    <view
      class="bg-white rounded-3xl px-6 py-8 box-border animate-fade-in-up"
      style="width: 300px; box-shadow: 0 8px 32px rgba(26, 26, 26, 0.10);"
    >
      <!-- 图标区域 -->
      <view
        class="w-14 h-14 rounded-full flex items-center justify-center mx-auto mb-5"
        :style="getIconBgStyle"
      >
        <text class="iconfont text-2xl" :class="getIconClass"></text>
      </view>

      <!-- 标题 -->
      <text
        class="block text-[17px] font-bold text-ink text-center mb-2"
        style="font-family: 'Outfit', 'PingFang SC', sans-serif; letter-spacing: -0.01em;"
      >{{ computedTitle }}</text>

      <!-- 内容 -->
      <text
        class="block text-sm text-muted text-center leading-relaxed mb-6"
        style="font-family: 'Outfit', 'PingFang SC', sans-serif;"
      >{{ computedContent }}</text>

      <!-- 输入框（仅当 type 为 input 时显示） -->
      <view v-if="type === 'input'" class="mb-6">
        <view class="border-b border-line flex items-center py-3">
          <input
            v-model="inputValue"
            class="flex-1 text-[15px] text-ink bg-transparent"
            style="border: none; outline: none; padding: 0; font-family: 'Outfit', 'PingFang SC', sans-serif;"
            :placeholder="placeholder"
            placeholder-style="color: #8C8A84; font-family: 'Outfit', 'PingFang SC', sans-serif;"
            :type="inputType"
            :maxlength="maxlength"
            :focus="autoFocus"
            @input="handleInput"
          />
        </view>
        <view v-if="showCharCount" class="text-right mt-1.5">
          <text class="text-xs text-muted" style="font-family: 'Outfit', sans-serif;">
            {{ inputValue.length }}/{{ maxlength }}
          </text>
        </view>
      </view>

      <!-- 多选项（仅当 type 为 multi-select 时显示） -->
      <view v-if="type === 'multi-select'" class="mb-6 flex flex-col gap-2" style="max-height: 200px; overflow-y: auto;">
        <view
          v-for="(option, index) in options"
          :key="index"
          class="flex items-center p-3 rounded-2xl active:scale-[0.98] transition-transform duration-150"
          :style="{
            border: '1px solid',
            borderColor: selectedOptions.includes(option.value) ? '#FFC107' : '#E8E3DC',
            backgroundColor: selectedOptions.includes(option.value) ? '#FFF8E1' : 'transparent'
          }"
          @tap="toggleOption(option.value)"
        >
          <view
            class="w-5 h-5 rounded-full flex items-center justify-center mr-3 flex-shrink-0"
            :style="{
              border: '1.5px solid',
              borderColor: selectedOptions.includes(option.value) ? '#FFC107' : '#E8E3DC'
            }"
          >
            <view v-if="selectedOptions.includes(option.value)" class="w-2.5 h-2.5 rounded-full bg-accent"></view>
          </view>
          <view class="flex-1">
            <text class="text-sm font-medium text-ink" style="font-family: 'Outfit', 'PingFang SC', sans-serif;">
              {{ option.label }}
            </text>
            <text v-if="option.description" class="block text-xs text-muted mt-0.5"
              style="font-family: 'Outfit', 'PingFang SC', sans-serif;">
              {{ option.description }}
            </text>
          </view>
        </view>
      </view>

      <!-- 按钮区域 -->
      <view class="flex gap-3" :class="getButtonLayoutClass">
        <!-- 取消按钮 -->
        <button
          v-if="showCancel"
          class="flex-1 flex items-center justify-center font-bold rounded-full active:scale-95 transition-transform duration-150"
          style="height: 44px; background: #F0EDE6; color: #8C8A84; font-family: 'Outfit', sans-serif; font-size: 14px; border: none;"
          @tap="handleCancel"
        >{{ computedCancelText }}</button>

        <!-- 确认按钮 -->
        <button
          v-if="showConfirm"
          class="flex-1 flex items-center justify-center font-bold rounded-full active:scale-95 transition-transform duration-150"
          :style="getConfirmButtonStyle"
          @tap="handleConfirm"
        >{{ computedConfirmText }}</button>
      </view>
    </view>
  </up-popup>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

const emit = defineEmits(['cancel', 'confirm', 'close', 'input-change', 'select-change'])

const openModal = (config) => {
  show.value = true;
  type.value = config.type || 'normal';
  title.value = config.title || '';
  content.value = config.content || '';
  showCancel.value = config.showCancel !== undefined ? config.showCancel : true;
  cancelText.value = config.cancelText || '取消';
  cancelColor.value = config.cancelColor || '';
  showConfirm.value = config.showConfirm !== undefined ? config.showConfirm : true;
  confirmText.value = config.confirmText || '确认';
  confirmColor.value = config.confirmColor || '';
  buttonLayout.value = config.buttonLayout || 'horizontal';
  maskClosable.value = config.maskClosable !== undefined ? config.maskClosable : true;
  placeholder.value = config.placeholder || '请输入内容';
  inputType.value = config.inputType || 'text';
  maxlength.value = config.maxlength || 140;
  showCharCount.value = config.showCharCount || false;
  autoFocus.value = config.autoFocus !== undefined ? config.autoFocus : true;
  options.value = config.options || [];
  defaultSelected.value = config.defaultSelected || [];
  onConfirm.value = config.onConfirm || (() => { });
  onCancel.value = config.onCancel || (() => { });
}

defineExpose({ openModal });

const show = ref(false)
const inputValue = ref('')
const type = ref('normal')
const title = ref('')
const content = ref('')
const showCancel = ref(true)
const showConfirm = ref(true)
const cancelText = ref('取消')
const confirmText = ref('确认')
const cancelColor = ref('')
const confirmColor = ref('')
const buttonLayout = ref('horizontal')
const maskClosable = ref(true)
const placeholder = ref('请输入内容')
const inputType = ref('text')
const maxlength = ref(140)
const showCharCount = ref(false)
const autoFocus = ref(true)
const options = ref([])
const defaultSelected = ref([])
const onConfirm = ref<(...args: any[]) => void>(() => { })
const onCancel = ref<(...args: any[]) => void>(() => { })
const selectedOptions = ref([...defaultSelected.value])

watch(() => defaultSelected.value, (newVal) => {
  selectedOptions.value = [...newVal]
}, { immediate: true })

const isDanger = computed(() => ['error', 'delete'].includes(type.value))

const computedTitle = computed(() => {
  if (title.value) return title.value
  const titleMap = {
    success: '操作成功',
    error: '操作失败',
    warning: '注意',
    info: '提示',
    confirm: '确认操作',
    delete: '确认删除',
    normal: '提示',
    input: '输入信息',
    'multi-select': '请选择'
  }
  return titleMap[type.value] || '提示'
})

const computedContent = computed(() => {
  if (content.value) return content.value
  const contentMap = {
    success: '操作已成功完成。',
    error: '操作失败，请稍后重试。',
    warning: '请确认是否要继续此操作？',
    info: '这是一条重要提示信息。',
    confirm: '确定要执行此操作吗？',
    delete: '删除后数据将无法恢复，确定要继续吗？',
    input: '请输入相关信息：',
    'multi-select': '请选择相关选项：'
  }
  return contentMap[type.value] || '确定要执行此操作吗？'
})

const computedCancelText = computed(() => {
  if (cancelText.value) return cancelText.value
  const textMap = { delete: '取消删除' }
  return textMap[type.value] || '取消'
})

const computedConfirmText = computed(() => {
  if (confirmText.value) return confirmText.value
  const textMap = {
    success: '知道了',
    error: '重试',
    warning: '继续',
    delete: '确认删除',
    'multi-select': '确定选择',
  }
  return textMap[type.value] || '确定'
})

const getIconBgStyle = computed(() => isDanger.value
  ? 'background: #FDF0ED;'
  : 'background: #FFF8E1;'
)

const getIconClass = computed(() => {
  const iconMap = {
    success: 'icon-lucide-circle-check',
    error: 'icon-lucide-circle-x',
    warning: 'icon-lucide-triangle-alert',
    info: 'icon-lucide-info',
    confirm: 'icon-lucide-circle-question-mark',
    delete: 'icon-lucide-trash-2',
    input: 'icon-lucide-edit-3',
    'multi-select': 'icon-lucide-list',
    normal: 'icon-lucide-info'
  }
  const icon = iconMap[type.value] || 'icon-lucide-info'
  const color = isDanger.value ? 'text-[#C0503A]' : 'text-accent-dark'
  return `${icon} ${color}`
})

const getConfirmButtonStyle = computed(() => {
  if (confirmColor.value) {
    return {
      backgroundColor: confirmColor.value,
      color: '#FFFFFF',
      border: 'none',
      height: '44px',
      fontFamily: 'Outfit, sans-serif',
      fontSize: '14px',
      borderRadius: '999px'
    }
  }
  if (isDanger.value) {
    return {
      backgroundColor: '#C0503A',
      color: '#FFFFFF',
      border: 'none',
      height: '44px',
      fontFamily: 'Outfit, sans-serif',
      fontSize: '14px',
      borderRadius: '999px'
    }
  }
  return {
    backgroundColor: '#FFC107',
    color: '#1A1A1A',
    boxShadow: '0 4px 16px rgba(229, 168, 0, 0.30)',
    border: 'none',
    height: '44px',
    fontFamily: 'Outfit, sans-serif',
    fontSize: '14px',
    borderRadius: '999px'
  }
})

const getButtonLayoutClass = computed(() =>
  buttonLayout.value === 'vertical' ? 'flex-col' : 'flex-row'
)

const handleCancel = () => {
  const result = {
    type: 'cancel',
    value: type.value === 'input' ? inputValue.value : null,
    selected: type.value === 'multi-select' ? selectedOptions.value : null
  }
  if (onCancel.value && typeof onCancel.value === 'function') onCancel.value(result)
  show.value = false;
}

const handleConfirm = () => {
  const result = {
    type: 'confirm',
    value: type.value === 'input' ? inputValue.value : null,
    selected: type.value === 'multi-select' ? selectedOptions.value : null,
    data: {
      inputValue: type.value === 'input' ? inputValue.value : null,
      selectedOptions: type.value === 'multi-select' ? selectedOptions.value : null
    }
  }
  if (onConfirm.value && typeof onConfirm.value === 'function') onConfirm.value(result)
  show.value = false;
}

const handleInput = (e) => {
  inputValue.value = e.detail.value
  emit('input-change', inputValue.value)
}

const toggleOption = (value) => {
  const index = selectedOptions.value.indexOf(value)
  if (index === -1) {
    selectedOptions.value.push(value)
  } else {
    selectedOptions.value.splice(index, 1)
  }
  emit('select-change', selectedOptions.value)
}

const handleMaskClick = () => {
  if (maskClosable.value) handleCancel()
}
</script>

<style scoped>
:deep(.u-popup__content) {
  background-color: transparent !important;
}

.animate-fade-in-up {
  animation: fadeInUp 0.25s cubic-bezier(0.22, 0.61, 0.36, 1) both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
