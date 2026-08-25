<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增横幅</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="bannerList">
      <el-table-column label="ID" prop="bannerId" width="70" />
      <el-table-column label="图片" width="120">
        <template slot-scope="scope">
          <el-image v-if="scope.row.image" :src="scope.row.image" style="width:90px;height:40px;border-radius:4px;" fit="cover" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="跳转路径" prop="linkUrl" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.linkUrl || '（不可点击）' }}
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="small">
            {{ scope.row.status === '0' ? '启用' : '禁用' }}
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

    <el-dialog :title="dialogTitle" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="横幅图片" prop="image">
          <image-upload v-model="form.image" :limit="1" />
        </el-form-item>
        <el-form-item label="跳转路径">
          <el-input v-model="form.linkUrl" placeholder="小程序内部路径，例如 /pages/topic/list，留空则不可点击" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
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
import { listBanners, getBanner, addBanner, updateBanner, delBanner } from '@/api/zhw/homeBanner'

export default {
  name: 'ZhwHomeBanner',
  data() {
    return {
      loading: false,
      bannerList: [],
      open: false,
      dialogTitle: '',
      form: {},
      rules: {
        image: [{ required: true, message: '请上传横幅图片', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBanners().then(res => {
        this.bannerList = res.data || []
        this.loading = false
      })
    },
    reset() {
      this.form = { status: '0', sortOrder: 0 }
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleAdd() {
      this.reset()
      this.dialogTitle = '新增横幅'
      this.open = true
    },
    handleEdit(row) {
      this.reset()
      getBanner(row.bannerId).then(res => {
        this.form = res.data
        this.dialogTitle = '编辑横幅'
        this.open = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const action = this.form.bannerId ? updateBanner(this.form) : addBanner(this.form)
        action.then(() => {
          this.$modal.msgSuccess(this.form.bannerId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该横幅？').then(() => delBanner(row.bannerId)).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      })
    }
  }
}
</script>
