<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-download" size="mini" @click="handleExportCollection">
          导出精选（含分支树）
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="showHotExportDialog = true">
          导出热门故事
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleBatchUncollect">批量取消精选</el-button>
      </el-col>
      <el-col>
        <span style="color:#909399;font-size:13px;line-height:32px">共 {{ storyList.length }} 篇精选故事</span>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="storyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="storyId" width="70" />
      <el-table-column label="故事标题" prop="title" min-width="180" show-overflow-tooltip />
      <el-table-column label="作者" prop="authorNickName" width="120" />
      <el-table-column label="分支数" prop="branchCount" width="80" align="center" sortable />
      <el-table-column label="参与人数" prop="participantCount" width="90" align="center" />
      <el-table-column label="热度" prop="heatScore" width="90" align="center" sortable />
      <el-table-column label="点赞" prop="likeCount" width="70" align="center" />
      <el-table-column label="发布时间" prop="createTime" width="155" />
      <el-table-column label="操作" width="90" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" style="color:#F56C6C" @click="handleUnCollect(scope.row)">取消精选</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 导出热门对话框 -->
    <el-dialog title="导出热门故事" :visible.sync="showHotExportDialog" width="400px" append-to-body>
      <el-form label-width="110px">
        <el-form-item label="最少分支数">
          <el-input-number v-model="hotParams.minBranches" :min="1" :max="100" />
          <span style="margin-left:8px;color:#909399;font-size:12px">续写越多越活跃</span>
        </el-form-item>
        <el-form-item label="最多导出条数">
          <el-input-number v-model="hotParams.limit" :min="10" :max="500" :step="10" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="handleExportHot">确认导出</el-button>
        <el-button @click="showHotExportDialog = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCollection, collectStory, exportCollection, exportHot, batchUncollect } from '@/api/zhw/collection'

export default {
  name: 'ZhwCollection',
  data() {
    return {
      loading: false,
      ids: [],
      multiple: true,
      storyList: [],
      showHotExportDialog: false,
      hotParams: { minBranches: 3, limit: 50 }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listCollection().then(res => {
        this.storyList = res.data || []
        this.loading = false
      })
    },
    handleUnCollect(row) {
      this.$modal.confirm(`确认将「${row.title}」移出精选库？`).then(() => collectStory(row.storyId, false)).then(() => {
        this.$modal.msgSuccess('已取消精选')
        this.getList()
      })
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.storyId)
      this.multiple = !selection.length
    },
    handleBatchUncollect() {
      const ids = this.ids.join(',')
      this.$modal.confirm(`确认将选中的 ${this.ids.length} 个故事移出精选库？`).then(() => batchUncollect(ids)).then(res => {
        this.$modal.msgSuccess(res.msg || '已取消精选')
        this.getList()
      })
    },
    handleExportCollection() {
      this.$modal.msgSuccess('正在生成导出文件...')
      exportCollection().then(blob => this.downloadBlob(blob, 'zhw_collection'))
    },
    handleExportHot() {
      this.showHotExportDialog = false
      this.$modal.msgSuccess('正在生成导出文件...')
      exportHot(this.hotParams.minBranches, this.hotParams.limit).then(blob => this.downloadBlob(blob, 'zhw_hot_stories'))
    },
    downloadBlob(blob, prefix) {
      const url = URL.createObjectURL(new Blob([blob]))
      const a = document.createElement('a')
      a.href = url
      a.download = `${prefix}_${new Date().toISOString().slice(0, 10)}.json`
      a.click()
      URL.revokeObjectURL(url)
    }
  }
}
</script>
