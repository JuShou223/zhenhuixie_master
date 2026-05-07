<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="内容关键词">
        <el-input v-model="queryParams.keyword" placeholder="评论内容关键词" clearable @keyup.enter.native="handleQuery" style="width:200px" />
      </el-form-item>
      <el-form-item label="故事ID">
        <el-input v-model="queryParams.storyId" placeholder="指定故事ID" clearable style="width:120px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleBatchDelete">批量删除</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="commentId" width="70" />
      <el-table-column label="评论内容" prop="content" min-width="220" show-overflow-tooltip />
      <el-table-column label="作者" prop="authorNickName" width="110" />
      <el-table-column label="故事ID" prop="storyId" width="80" align="center" />
      <el-table-column label="分支ID" prop="branchId" width="80" align="center">
        <template slot-scope="scope">
          {{ scope.row.branchId === 0 ? '—' : scope.row.branchId }}
        </template>
      </el-table-column>
      <el-table-column label="回复目标" prop="replyToNickName" width="110">
        <template slot-scope="scope">
          {{ scope.row.replyToNickName || '—' }}
        </template>
      </el-table-column>
      <el-table-column label="点赞" prop="likeCount" width="70" align="center" />
      <el-table-column label="发布时间" prop="createTime" width="155" />
      <el-table-column label="操作" width="80" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listComments, delComment, batchDelComments } from '@/api/zhw/comment'

export default {
  name: 'ZhwComment',
  data() {
    return {
      loading: false,
      total: 0,
      ids: [],
      multiple: true,
      commentList: [],
      queryParams: { pageNum: 1, pageSize: 10, keyword: undefined, storyId: undefined }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listComments(this.queryParams).then(res => {
        this.commentList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, keyword: undefined, storyId: undefined }
      this.getList()
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该评论？删除后用户不可见。').then(() => delComment(row.commentId)).then(() => {
        this.$modal.msgSuccess('已删除')
        this.getList()
      })
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.commentId)
      this.multiple = !selection.length
    },
    handleBatchDelete() {
      const ids = this.ids.join(',')
      this.$modal.confirm(`确认删除选中的 ${this.ids.length} 条评论？删除后用户不可见。`).then(() => batchDelComments(ids)).then(res => {
        this.$modal.msgSuccess(res.msg || '已删除')
        this.getList()
      })
    }
  }
}
</script>
