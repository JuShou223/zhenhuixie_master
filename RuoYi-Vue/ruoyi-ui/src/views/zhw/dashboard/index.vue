<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom:20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num">{{ stats.totalStories || 0 }}</div>
          <div class="stat-label">故事总数</div>
          <div class="stat-sub">今日新增 +{{ stats.todayNewStories || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num">{{ stats.totalBranches || 0 }}</div>
          <div class="stat-label">分支总数</div>
          <div class="stat-sub">今日新增 +{{ stats.todayNewBranches || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num">{{ stats.totalComments || 0 }}</div>
          <div class="stat-label">评论总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">注册用户数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 趋势图 -->
      <el-col :span="14">
        <el-card shadow="hover">
          <div slot="header">
            近 7 天新增故事趋势
            <el-radio-group v-model="trendDays" size="mini" style="margin-left:16px" @change="loadTrend">
              <el-radio-button :label="7">7天</el-radio-button>
              <el-radio-button :label="30">30天</el-radio-button>
            </el-radio-group>
          </div>
          <div v-if="trendData.length === 0" style="text-align:center;padding:40px;color:#999">暂无数据</div>
          <el-table v-else :data="trendData" size="small">
            <el-table-column label="日期" prop="date" />
            <el-table-column label="新增故事数" prop="count" align="right" />
          </el-table>
        </el-card>
      </el-col>

      <!-- 热度 Top10 -->
      <el-col :span="10">
        <el-card shadow="hover">
          <div slot="header">热度 Top 10 故事</div>
          <el-table :data="hotList" size="small">
            <el-table-column label="标题" prop="title" show-overflow-tooltip min-width="120" />
            <el-table-column label="热度" prop="heatScore" width="70" align="right" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getPlatformStats, getStoryTrend, getHotStories } from '@/api/zhw/dashboard'

export default {
  name: 'ZhwDashboard',
  data() {
    return {
      stats: {},
      trendDays: 7,
      trendData: [],
      hotList: []
    }
  },
  created() {
    this.loadStats()
    this.loadTrend()
    this.loadHot()
  },
  methods: {
    loadStats() {
      getPlatformStats().then(res => { this.stats = res.data || {} })
    },
    loadTrend() {
      getStoryTrend(this.trendDays).then(res => { this.trendData = (res.data && res.data.dailyStories) || [] })
    },
    loadHot() {
      getHotStories().then(res => { this.hotList = res.data || [] })
    }
  }
}
</script>

<style scoped>
.stat-card { text-align: center; padding: 8px 0; }
.stat-num { font-size: 36px; font-weight: 700; color: #409EFF; line-height: 1.2; }
.stat-label { font-size: 14px; color: #606266; margin-top: 4px; }
.stat-sub { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
