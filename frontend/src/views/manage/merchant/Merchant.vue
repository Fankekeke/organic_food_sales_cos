
<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="商家编号"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.code"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="商家名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.name"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="负责人"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.principal"/>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button @click="batchDelete">删除</a-button>
      </div>
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="operation" slot-scope="text, record">
          <a-icon type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修 改"></a-icon>
          <a-icon type="file-search" @click="merchantViewOpen(record)" title="详 情" style="margin-left: 15px"></a-icon>
          <a-icon v-if="record.ocId != null" type="audit" @click="openCertificationAudit(record)" title="审 核" style="margin-left: 15px"></a-icon>
          <a-icon v-if="record.status === '1'" type="caret-down" @click="audit(record.id, 0)" title="上 架" style="margin-left: 15px"></a-icon>
          <a-icon v-if="record.status === '0'" type="caret-up" @click="audit(record.id, 1)" title="下 架" style="margin-left: 15px"></a-icon>
        </template>
      </a-table>
    </div>
    <merchant-add
      v-if="merchantAdd.visiable"
      @close="handlemerchantAddClose"
      @success="handlemerchantAddSuccess"
      :merchantAddVisiable="merchantAdd.visiable">
    </merchant-add>
    <merchant-edit
      ref="merchantEdit"
      @close="handlemerchantEditClose"
      @success="handlemerchantEditSuccess"
      :merchantEditVisiable="merchantEdit.visiable">
    </merchant-edit>
    <merchant-view
      @close="handlemerchantViewClose"
      :merchantShow="merchantView.visiable"
      :merchantData="merchantView.data">
    </merchant-view>

    <a-modal
      v-model="certificationAuditVisible"
      title="商家认证审核"
      @cancel="handleCertificationAuditClose"
      :width="1200"
      :footer="null">
      <div style="font-size: 13px;font-family: SimHei" v-if="certificationData !== null">
        <a-row style="padding-left: 24px;padding-right: 24px;">
          <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">基础信息</span></a-col>
          <a-col :span="8"><b>ID：</b>
            {{ certificationData.id }}
          </a-col>
          <a-col :span="8"><b>商家ID：</b>
            {{ certificationData.merchantId }}
          </a-col>
          <a-col :span="8"><b>审核状态：</b>
            <a-tag v-if="certificationData.status === 0" color="orange">待审核</a-tag>
            <a-tag v-else-if="certificationData.status === 1" color="green">通过</a-tag>
            <a-tag v-else-if="certificationData.status === 2" color="red">驳回</a-tag>
          </a-col>
        </a-row>
        <br/>
        <a-row style="padding-left: 24px;padding-right: 24px;">
          <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">资质图片</span></a-col>
          <a-col :span="24"><b>身份证明：</b></a-col>
          <a-col :span="24" style="margin-top: 10px;margin-bottom: 15px;">
            <img
              v-if="certificationData.idCardImg"
              :src="'http://127.0.0.1:9527/imagesWeb/' + certificationData.idCardImg"
              style="max-width: 300px;cursor: pointer;border: 1px solid #d9d9d9;border-radius: 4px;"
              @click="previewImage(certificationData.idCardImg)" />
            <span v-else>未上传</span>
          </a-col>

          <a-col :span="24"><b>营业执照：</b></a-col>
          <a-col :span="24" style="margin-top: 10px;margin-bottom: 15px;">
            <img
              v-if="certificationData.businessLicenseImg"
              :src="'http://127.0.0.1:9527/imagesWeb/' + certificationData.businessLicenseImg"
              style="max-width: 300px;cursor: pointer;border: 1px solid #d9d9d9;border-radius: 4px;"
              @click="previewImage(certificationData.businessLicenseImg)" />
            <span v-else>未上传</span>
          </a-col>

          <a-col :span="24"><b>土地租赁合同或产权证明：</b></a-col>
          <a-col :span="24" style="margin-top: 10px;margin-bottom: 15px;">
            <img
              v-if="certificationData.landProofImg"
              :src="'http://127.0.0.1:9527/imagesWeb/' + certificationData.landProofImg"
              style="max-width: 300px;cursor: pointer;border: 1px solid #d9d9d9;border-radius: 4px;"
              @click="previewImage(certificationData.landProofImg)" />
            <span v-else>未上传</span>
          </a-col>
        </a-row>
        <br/>
        <a-row style="padding-left: 24px;padding-right: 24px;">
          <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">检测报告</span></a-col>
          <a-col :span="24"><b>环境检测报告：</b></a-col>
          <a-col :span="24" style="margin-top: 10px;margin-bottom: 15px;">
            <img
              v-if="certificationData.envTestReport"
              :src="'http://127.0.0.1:9527/imagesWeb/' + certificationData.envTestReport"
              style="max-width: 300px;cursor: pointer;border: 1px solid #d9d9d9;border-radius: 4px;"
              @click="previewImage(certificationData.envTestReport)" />
            <span v-else>未上传</span>
          </a-col>

          <a-col :span="24"><b>产品检验报告：</b></a-col>
          <a-col :span="24" style="margin-top: 10px;margin-bottom: 15px;">
            <img
              v-if="certificationData.productTestReport"
              :src="'http://127.0.0.1:9527/imagesWeb/' + certificationData.productTestReport"
              style="max-width: 300px;cursor: pointer;border: 1px solid #d9d9d9;border-radius: 4px;"
              @click="previewImage(certificationData.productTestReport)" />
            <span v-else>未上传</span>
          </a-col>

          <a-col :span="24"><b>投入品证明（种子、肥料等）：</b></a-col>
          <a-col :span="24" style="margin-top: 10px;margin-bottom: 15px;">
            <img
              v-if="certificationData.inputMaterialProof"
              :src="'http://127.0.0.1:9527/imagesWeb/' + certificationData.inputMaterialProof"
              style="max-width: 300px;cursor: pointer;border: 1px solid #d9d9d9;border-radius: 4px;"
              @click="previewImage(certificationData.inputMaterialProof)" />
            <span v-else>未上传</span>
          </a-col>
        </a-row>
        <br/>
        <a-row style="padding-left: 24px;padding-right: 24px;">
          <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">其他信息</span></a-col>
          <a-col :span="24"><b>商家的其他要求或问题：</b></a-col>
          <a-col :span="24" style="margin-top: 10px;margin-bottom: 15px;">
            {{ certificationData.additionalRequests || '- -' }}
          </a-col>

          <a-col :span="24" v-if="certificationData.adminRemark"><b>管理员审核备注/驳回原因：</b></a-col>
          <a-col :span="24" style="margin-top: 10px;margin-bottom: 15px;" v-if="certificationData.adminRemark">
            {{ certificationData.adminRemark }}
          </a-col>

          <a-col :span="8"><b>已通知管理员：</b>
            {{ certificationData.hasNotifiedAdmin ? '是' : '否' }}
          </a-col>
        </a-row>

        <a-divider v-if="certificationData.status === 0" />

        <a-row v-if="certificationData.status === 0" style="padding-left: 24px;padding-right: 24px;">
          <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">审核操作</span></a-col>
          <a-col :span="24">
            <a-textarea
              v-model="auditRemark"
              placeholder="请输入审核备注（驳回时必填）"
              :rows="4"              style="margin-bottom: 15px" />
          </a-col>
          <a-col :span="24" style="text-align: center;">
            <a-button type="primary" @click="handleApprove" style="margin-right: 10px;">
              审核通过
            </a-button>
            <a-button type="danger" @click="handleReject">
              驳回
            </a-button>
          </a-col>
        </a-row>
      </div>
    </a-modal>

    <a-modal v-model="previewVisible" :footer="null" @cancel="previewVisible = false">
      <img alt="预览" style="width: 100%" :src="previewImageUrl" />
    </a-modal>
  </a-card>
</template>

<script>import RangeDate from '@/components/datetime/RangeDate'
import merchantAdd from './MerchantAdd'
import merchantEdit from './MerchantEdit'
import merchantView from './MerchantView.vue'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'merchant',
  components: {merchantAdd, merchantEdit, merchantView, RangeDate},
  data () {
    return {
      advanced: false,
      merchantAdd: {
        visiable: false
      },
      merchantEdit: {
        visiable: false
      },
      merchantView: {
        visiable: false,
        data: null
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: [],
      certificationAuditVisible: false,
      certificationData: null,
      previewVisible: false,
      previewImageUrl: '',
      auditRemark: ''
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '商家编号',
        dataIndex: 'code',
        ellipsis: true
      }, {
        title: '商家名称',
        dataIndex: 'name',
        ellipsis: true
      }, {
        title: '商家图片',
        dataIndex: 'images',
        customRender: (text, record, index) => {
          if (!record.images) return <a-avatar shape="square" icon="user" />
          return <a-popover>
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images.split(',')[0] } />
            </template>
            <a-avatar shape="square" icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images.split(',')[0] } />
          </a-popover>
        }
      }, {
        title: '状态',
        dataIndex: 'status',
        customRender: (text, row, index) => {
          switch (text) {
            case '0':
              return <a-tag color="red">休业</a-tag>
            case '1':
              return <a-tag color="green">营业</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '具体地址',
        dataIndex: 'address',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '负责人',
        dataIndex: 'principal',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '联系方式',
        dataIndex: 'phone',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '创建时间',
        dataIndex: 'createDate',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    audit (id, status) {
      this.$get('/cos/merchant-info/audit', {merchantId: id, status}).then((r) => {
        this.$message.success('修改成功')
        this.search()
      })
    },
    merchantViewOpen (row) {
      this.merchantView.data = row
      this.merchantView.visiable = true
    },
    handlemerchantViewClose () {
      this.merchantView.visiable = false
    },
    openCertificationAudit (record) {
      this.certificationData = null
      this.auditRemark = ''
      this.certificationAuditVisible = true
      this.$get('/cos/organic-certifications/queryCertificationByMerchantUser', {
        merchantId: record.id
      }).then((r) => {
        if (r.data && r.data.data) {
          this.certificationData = r.data.data
        } else {
          this.$message.warning('该商家暂无认证信息')
          this.certificationAuditVisible = false
        }
      }).catch(() => {
        this.$message.error('获取认证信息失败')
        this.certificationAuditVisible = false
      })
    },
    handleCertificationAuditClose () {
      this.certificationAuditVisible = false
      this.certificationData = null
      this.auditRemark = ''
    },
    previewImage (imagePath) {
      this.previewImageUrl = 'http://127.0.0.1:9527/imagesWeb/' + imagePath
      this.previewVisible = true
    },
    handleApprove () {
      this.$confirm({
        title: '确认审核通过?',
        content: '通过后将无法撤销',
        centered: true,
        onOk: () => {
          this.$put('/cos/organic-certifications', {
            id: this.certificationData.id,
            status: 1,
            adminRemark: this.auditRemark || ''
          }).then((r) => {
            this.$message.success('审核通过')
            this.handleCertificationAuditClose()
            this.search()
          })
        }
      })
    },
    handleReject () {
      if (!this.auditRemark) {
        this.$message.warning('请输入驳回原因')
        return
      }
      this.$confirm({
        title: '确认驳回?',
        content: '驳回后商家可以重新提交认证信息',
        centered: true,
        onOk: () => {
          this.$put('/cos/organic-certifications/audit', {
            id: this.certificationData.id,
            status: 2,
            adminRemark: this.auditRemark
          }).then((r) => {
            this.$message.success('已驳回')
            this.handleCertificationAuditClose()
            this.search()
          })
        }
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.merchantAdd.visiable = true
    },
    handlemerchantAddClose () {
      this.merchantAdd.visiable = false
    },
    handlemerchantAddSuccess () {
      this.merchantAdd.visiable = false
      this.$message.success('新增商家成功')
      this.search()
    },
    edit (record) {
      this.$refs.merchantEdit.setFormValues(record)
      this.merchantEdit.visiable = true
    },
    handlemerchantEditClose () {
      this.merchantEdit.visiable = false
    },
    handlemerchantEditSuccess () {
      this.merchantEdit.visiable = false
      this.$message.success('修改商家成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/merchant-info/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      this.selectedRowKeys = []
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      this.filteredInfo = null
      this.sortedInfo = null
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      this.loading = true
      if (this.paginationInfo) {
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      this.$get('/cos/merchant-info/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>@import "../../../../static/less/Common";
</style>
