<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" class="mb8">
      <el-form-item label="关键词">
        <el-input
          v-model="queryParams.word"
          placeholder="敏感词"
          clearable
          style="width:180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="queryParams.category" placeholder="全部分类" clearable style="width:120px">
          <el-option label="色情" value="色情" />
          <el-option label="暴恐" value="暴恐" />
          <el-option label="反动" value="反动" />
          <el-option label="民生" value="民生" />
          <el-option label="贪腐" value="贪腐" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增敏感词</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleBatchDelete">批量删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-refresh" size="mini" @click="handleReload">从数据库刷新缓存</el-button>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="敏感词" prop="word" min-width="150" show-overflow-tooltip />
      <el-table-column label="分类" prop="category" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.category" size="small">{{ scope.row.category }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="170" />
      <el-table-column label="操作" width="160" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="敏感词" prop="word">
          <el-input v-model="form.word" placeholder="请输入敏感词" maxlength="200" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" clearable style="width:100%">
            <el-option label="色情" value="色情" />
            <el-option label="暴恐" value="暴恐" />
            <el-option label="反动" value="反动" />
            <el-option label="民生" value="民生" />
            <el-option label="贪腐" value="贪腐" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSensitiveWords, getSensitiveWord, addSensitiveWord, updateSensitiveWord, delSensitiveWord, reloadSensitiveWords, batchDelSensitiveWords } from '@/api/zhw/sensitiveWord'

export default {
  name: 'ZhwSensitiveWord',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      ids: [],
      multiple: true,
      queryParams: { pageNum: 1, pageSize: 10, word: undefined, category: undefined },
      open: false,
      dialogTitle: '',
      form: {},
      rules: {
        word: [{ required: true, message: '敏感词不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSensitiveWords(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, word: undefined, category: undefined }
      this.getList()
    },
    reset() {
      this.form = { word: '', category: '' }
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleAdd() {
      this.reset()
      this.dialogTitle = '新增敏感词'
      this.open = true
    },
    handleEdit(row) {
      this.reset()
      getSensitiveWord(row.id).then(res => {
        this.form = res.data
        this.dialogTitle = '编辑敏感词'
        this.open = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const action = this.form.id ? updateSensitiveWord(this.form) : addSensitiveWord(this.form)
        action.then(() => {
          this.$modal.msgSuccess(this.form.id ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除敏感词「${row.word}」？`).then(() => delSensitiveWord(row.id)).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      })
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    handleBatchDelete() {
      const ids = this.ids.join(',')
      this.$modal.confirm(`确认删除选中的 ${this.ids.length} 个敏感词？`).then(() => batchDelSensitiveWords(ids)).then(res => {
        this.$modal.msgSuccess(res.msg || '已删除')
        this.getList()
      })
    },
    handleReload() {
      reloadSensitiveWords().then(res => {
        this.$modal.msgSuccess(res.msg || '缓存已刷新')
      })
    }
  }
}
</script>
