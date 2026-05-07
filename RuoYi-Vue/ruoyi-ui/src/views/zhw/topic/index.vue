<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增话题</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleBatchDelete">批量删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="topicList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="topicId" width="70" />
      <el-table-column label="话题标题" prop="title" min-width="160" show-overflow-tooltip />
      <el-table-column label="Banner" width="90">
        <template slot-scope="scope">
          <el-image v-if="scope.row.banner" :src="scope.row.banner" style="width:60px;height:36px;border-radius:4px;" fit="cover" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="故事数" prop="storyCount" width="80" align="center" />
      <el-table-column label="开始时间" prop="startTime" width="160" />
      <el-table-column label="结束时间" prop="endTime" width="160" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="small">
            {{ scope.row.status === '0' ? '进行中' : '已结束' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" width="160" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" :visible.sync="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="话题标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入话题标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="Banner图">
          <el-input v-model="form.banner" placeholder="Banner图片URL（可选）" />
        </el-form-item>
        <el-form-item label="公告/简介">
          <el-input v-model="form.announcement" type="textarea" :rows="4" placeholder="话题公告或简介" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">进行中</el-radio>
            <el-radio label="1">已结束</el-radio>
          </el-radio-group>
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
import { listTopics, getTopic, addTopic, updateTopic, delTopic, batchDelTopics } from '@/api/zhw/topic'

export default {
  name: 'ZhwTopic',
  data() {
    return {
      loading: false,
      ids: [],
      multiple: true,
      topicList: [],
      open: false,
      dialogTitle: '',
      form: {},
      rules: {
        title: [{ required: true, message: '话题标题不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listTopics().then(res => {
        this.topicList = res.data || []
        this.loading = false
      })
    },
    reset() {
      this.form = { status: '0' }
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleAdd() {
      this.reset()
      this.dialogTitle = '新增话题'
      this.open = true
    },
    handleEdit(row) {
      this.reset()
      getTopic(row.topicId).then(res => {
        this.form = res.data
        this.dialogTitle = '编辑话题'
        this.open = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const action = this.form.topicId ? updateTopic(this.form) : addTopic(this.form)
        action.then(() => {
          this.$modal.msgSuccess(this.form.topicId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除话题「${row.title}」？`).then(() => delTopic(row.topicId)).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      })
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.topicId)
      this.multiple = !selection.length
    },
    handleBatchDelete() {
      const ids = this.ids.join(',')
      this.$modal.confirm(`确认删除选中的 ${this.ids.length} 个话题？`).then(() => batchDelTopics(ids)).then(res => {
        this.$modal.msgSuccess(res.msg || '已删除')
        this.getList()
      })
    }
  }
}
</script>
