<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="标题关键词">
        <el-input v-model="queryParams.keyword" placeholder="故事标题关键词" clearable @keyup.enter.native="handleQuery" style="width:180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:110px">
          <el-option label="正常" value="0" />
          <el-option label="禁用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="warning" plain icon="el-icon-turn-off" size="mini" :disabled="multiple" @click="handleBatchDisable">批量禁用</el-button>
        <el-button type="success" plain icon="el-icon-open" size="mini" :disabled="multiple" @click="handleBatchEnable">批量恢复</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="storyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="storyId" width="70" />
      <el-table-column label="封面" width="80">
        <template slot-scope="scope">
          <el-image v-if="scope.row.cover" :src="scope.row.cover" style="width:54px;height:54px;border-radius:4px;" fit="cover" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="故事标题" prop="title" min-width="160" show-overflow-tooltip />
      <el-table-column label="作者" prop="authorNickName" width="110" />
      <el-table-column label="热度" prop="heatScore" width="80" align="center" sortable />
      <el-table-column label="分支" prop="branchCount" width="70" align="center" />
      <el-table-column label="评论" prop="commentCount" width="70" align="center" />
      <el-table-column label="点赞" prop="likeCount" width="70" align="center" />
      <el-table-column label="状态" width="80" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" size="small">
            {{ scope.row.status === '0' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="精选" width="60" align="center">
        <template slot-scope="scope">
          <span style="font-size:18px;cursor:pointer" :style="scope.row.isCollected ? 'color:#F7BA2A' : 'color:#C0C4CC'"
                :title="scope.row.isCollected ? '取消精选' : '加入精选'"
                @click="handleCollect(scope.row)">★</span>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" prop="createTime" width="155" />
      <el-table-column label="操作" width="140" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === '0'" size="mini" type="text" style="color:#E6A23C" @click="handleDisable(scope.row)">禁用</el-button>
          <el-button v-else size="mini" type="text" style="color:#67C23A" @click="handleEnable(scope.row)">恢复</el-button>
          <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listStories, updateStoryStatus, delStory, batchDelStories, batchUpdateStoryStatus } from '@/api/zhw/story'
import { collectStory } from '@/api/zhw/collection'

export default {
  name: 'ZhwStory',
  data() {
    return {
      loading: false,
      total: 0,
      ids: [],
      multiple: true,
      storyList: [],
      queryParams: { pageNum: 1, pageSize: 10, keyword: undefined, status: undefined }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listStories(this.queryParams).then(res => {
        this.storyList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, keyword: undefined, status: undefined }
      this.getList()
    },
    handleCollect(row) {
      const toCollect = !row.isCollected
      collectStory(row.storyId, toCollect).then(() => {
        row.isCollected = toCollect ? 1 : 0
        this.$modal.msgSuccess(toCollect ? '已加入精选' : '已取消精选')
      })
    },
    handleDisable(row) {
      this.$modal.confirm(`确认禁用故事「${row.title}」？`).then(() => updateStoryStatus(row.storyId, '1')).then(() => {
        this.$modal.msgSuccess('已禁用')
        this.getList()
      })
    },
    handleEnable(row) {
      updateStoryStatus(row.storyId, '0').then(() => {
        this.$modal.msgSuccess('已恢复')
        this.getList()
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除故事「${row.title}」？此操作不可恢复。`).then(() => delStory(row.storyId)).then(() => {
        this.$modal.msgSuccess('已删除')
        this.getList()
      })
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.storyId)
      this.multiple = !selection.length
    },
    handleBatchDelete() {
      const ids = this.ids.join(',')
      this.$modal.confirm(`确认删除选中的 ${this.ids.length} 个故事？此操作不可恢复。`).then(() => batchDelStories(ids)).then(res => {
        this.$modal.msgSuccess(res.msg || '已删除')
        this.getList()
      })
    },
    handleBatchDisable() {
      const ids = this.ids.join(',')
      this.$modal.confirm(`确认禁用选中的 ${this.ids.length} 个故事？`).then(() => batchUpdateStoryStatus(ids, '1')).then(res => {
        this.$modal.msgSuccess(res.msg || '已禁用')
        this.getList()
      })
    },
    handleBatchEnable() {
      const ids = this.ids.join(',')
      batchUpdateStoryStatus(this.ids.join(','), '0').then(res => {
        this.$modal.msgSuccess(res.msg || '已恢复')
        this.getList()
      })
    }
  }
}
</script>
