<template>
  <div class="app-container">
    <!-- 高级搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="关键词">
        <el-input v-model="queryParams.keyword" placeholder="用户名/昵称" clearable @keyup.enter.native="handleQuery" style="width:180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:110px">
          <el-option label="正常" value="0" /><el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="注册时间">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
          start-placeholder="开始" end-placeholder="结束" value-format="yyyy-MM-dd"
          style="width:240px" @change="onDateChange" />
      </el-form-item>
      <el-form-item label="排序">
        <el-select v-model="queryParams.orderBy" placeholder="默认" clearable style="width:130px" @change="handleQuery">
          <el-option label="注册时间（升序）" value="createTimeAsc" />
          <el-option label="积分（降序）" value="score" />
          <el-option label="故事数（降序）" value="storyCount" />
          <el-option label="分支数（降序）" value="branchCount" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleBatchDisable">批量停用</el-button>
        <el-button type="success" plain icon="el-icon-open" size="mini" :disabled="multiple" @click="handleBatchEnable">批量启用</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport">导出</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="userId" width="80" />
      <el-table-column label="头像" width="60">
        <template slot-scope="scope">
          <el-image v-if="scope.row.avatar" :src="scope.row.avatar" style="width:36px;height:36px;border-radius:50%;" fit="cover" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="用户名" prop="userName" width="120" />
      <el-table-column label="昵称" prop="nickName" width="120" />
      <el-table-column label="积分" prop="score" width="80" align="center" sortable />
      <el-table-column label="故事" prop="storyCount" width="60" align="center" />
      <el-table-column label="分支" prop="branchCount" width="60" align="center" />
      <el-table-column label="评论" prop="commentCount" width="60" align="center" />
      <el-table-column label="获赞" prop="likeCount" width="60" align="center" />
      <el-table-column label="状态" width="70" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" size="small">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后登录" prop="loginTime" width="155" />
      <el-table-column label="注册时间" prop="createTime" width="155" />
      <el-table-column label="操作" width="190" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === '0'" size="mini" type="text" style="color:#E6A23C" @click="handleDisable(scope.row)">停用</el-button>
          <el-button v-else size="mini" type="text" style="color:#67C23A" @click="handleEnable(scope.row)">启用</el-button>
          <el-button size="mini" type="text" style="color:#F56C6C" @click="handleResetPwd(scope.row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情弹窗 -->
    <el-dialog title="用户详情" :visible.sync="detailOpen" width="550px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-image v-if="detail.avatar" :src="detail.avatar" style="width:100px;height:100px;border-radius:50%;" fit="cover" />
          <span v-else>—</span>
        </el-col>
        <el-col :span="16">
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ detail.userName }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ detail.nickName }}</el-descriptions-item>
            <el-descriptions-item label="签名">{{ detail.remark || '—' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ detail.phone || '—' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ detail.gender === '1' ? '男' : detail.gender === '2' ? '女' : '未知' }}</el-descriptions-item>
            <el-descriptions-item label="生日">{{ detail.birthday || '—' }}</el-descriptions-item>
            <el-descriptions-item label="状态"><el-tag :type="detail.status === '0' ? 'success' : 'danger'" size="small">{{ detail.status === '0' ? '正常' : '停用' }}</el-tag></el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ detail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ detail.loginTime || '—' }}</el-descriptions-item>
            <el-descriptions-item label="登录IP">{{ detail.loginIp || '—' }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
      <el-divider>创作统计</el-divider>
      <el-row :gutter="12">
        <el-col :span="6"><el-card shadow="hover"><div style="text-align:center"><div style="font-size:24px;color:#409EFF;font-weight:bold">{{ detail.score || 0 }}</div><div style="font-size:12px;color:#999;margin-top:4px">积分</div></div></el-card></el-col>
        <el-col :span="4"><el-card shadow="hover"><div style="text-align:center"><div style="font-size:24px;color:#67C23A;font-weight:bold">{{ detail.storyCount || 0 }}</div><div style="font-size:12px;color:#999;margin-top:4px">故事</div></div></el-card></el-col>
        <el-col :span="4"><el-card shadow="hover"><div style="text-align:center"><div style="font-size:24px;color:#E6A23C;font-weight:bold">{{ detail.branchCount || 0 }}</div><div style="font-size:12px;color:#999;margin-top:4px">分支</div></div></el-card></el-col>
        <el-col :span="4"><el-card shadow="hover"><div style="text-align:center"><div style="font-size:24px;color:#909399;font-weight:bold">{{ detail.commentCount || 0 }}</div><div style="font-size:12px;color:#999;margin-top:4px">评论</div></div></el-card></el-col>
        <el-col :span="6"><el-card shadow="hover"><div style="text-align:center"><div style="font-size:24px;color:#F56C6C;font-weight:bold">{{ detail.likeCount || 0 }}</div><div style="font-size:12px;color:#999;margin-top:4px">获赞</div></div></el-card></el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import { listUsers, getUser, updateUserStatus, batchUpdateUserStatus, resetUserPwd, exportUsers } from '@/api/zhw/user'

export default {
  name: 'ZhwUser',
  data() {
    return {
      loading: false, list: [], total: 0, ids: [], multiple: true,
      dateRange: null,
      queryParams: { pageNum: 1, pageSize: 10, keyword: undefined, status: undefined, beginTime: undefined, endTime: undefined, orderBy: undefined },
      detailOpen: false, detail: {}
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listUsers(this.queryParams).then(res => {
        this.list = res.rows || []; this.total = res.total || 0; this.loading = false
      })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.dateRange = null
      this.queryParams = { pageNum: 1, pageSize: 10, keyword: undefined, status: undefined, beginTime: undefined, endTime: undefined, orderBy: undefined }
      this.getList()
    },
    onDateChange(val) {
      this.queryParams.beginTime = val ? val[0] : undefined
      this.queryParams.endTime = val ? val[1] : undefined
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId); this.multiple = !selection.length
    },
    handleDetail(row) {
      getUser(row.userId).then(res => { this.detail = res.data || {}; this.detailOpen = true })
    },
    handleDisable(row) {
      this.$modal.confirm(`确认停用用户「${row.userName}」？`).then(() => updateUserStatus(row.userId, '1')).then(() => { this.$modal.msgSuccess('已停用'); this.getList() })
    },
    handleEnable(row) {
      updateUserStatus(row.userId, '0').then(() => { this.$modal.msgSuccess('已启用'); this.getList() })
    },
    handleBatchDisable() {
      const ids = this.ids.join(',')
      this.$modal.confirm(`确认停用选中的 ${this.ids.length} 个用户？`).then(() => batchUpdateUserStatus(ids, '1')).then(res => { this.$modal.msgSuccess(res.msg || '已停用'); this.getList() })
    },
    handleBatchEnable() {
      const ids = this.ids.join(',')
      batchUpdateUserStatus(ids, '0').then(res => { this.$modal.msgSuccess(res.msg || '已启用'); this.getList() })
    },
    handleResetPwd(row) {
      this.$modal.confirm(`确认重置「${row.userName}」的密码为 Zhw@123456？`).then(() => resetUserPwd(row.userId)).then(() => { this.$modal.msgSuccess('密码已重置') })
    },
    handleExport() {
      this.$modal.msgSuccess('正在生成导出文件...')
      exportUsers(this.queryParams).then(res => {
        const blob = new Blob([JSON.stringify(res.data || [], null, 2)], { type: 'application/json' })
        const url = URL.createObjectURL(blob); const a = document.createElement('a')
        a.href = url; a.download = `zhw_users_${new Date().toISOString().slice(0, 10)}.json`; a.click()
        URL.revokeObjectURL(url)
      })
    }
  }
}
</script>
