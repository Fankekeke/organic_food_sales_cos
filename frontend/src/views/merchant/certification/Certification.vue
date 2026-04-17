
<template>
  <a-drawer
    title="商家认证申报"
    :maskClosable="false"
    width=1000
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="show"    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="24">
          <a-alert
            v-if="isEditMode"
            message="当前为编辑模式"
            description="检测到您已提交过认证信息，可以修改后重新提交审核"
            type="info"
            show-icon
            style="margin-bottom: 20px" />
          <a-alert
            v-else
            message="当前为新增模式"
            description="请填写完整的认证信息并提交审核"
            type="success"
            show-icon
            style="margin-bottom: 20px" />
        </a-col>
      </a-row>

      <a-divider orientation="left">基础信息</a-divider>

      <a-row :gutter="20">
        <a-col :span="12">
          <a-form-item label='审核状态'>
            <a-select v-decorator="['status']" disabled>
              <a-select-option :value="0">待审核</a-select-option>
              <a-select-option :value="1">通过</a-select-option>
              <a-select-option :value="2">驳回</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-divider orientation="left">资质证明</a-divider>

      <a-row :gutter="20">
        <a-col :span="24">
          <a-form-item label='身份证明图片' v-bind="formItemLayout">
            <a-upload
              name="idCardImg"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="idCardFileList"
              @preview="handlePreview"
              @change="handleIdCardChange"
              :maxCount="1">
              <div v-if="idCardFileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">上传身份证明</div>
              </div>
            </a-upload>
          </a-form-item>
        </a-col>

        <a-col :span="24">
          <a-form-item label='营业执照图片' v-bind="formItemLayout">
            <a-upload
              name="businessLicenseImg"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="businessLicenseFileList"
              @preview="handlePreview"
              @change="handleBusinessLicenseChange"
              :maxCount="1">
              <div v-if="businessLicenseFileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">上传营业执照</div>
              </div>
            </a-upload>
          </a-form-item>
        </a-col>

        <a-col :span="24">
          <a-form-item label='土地租赁合同或产权证明' v-bind="formItemLayout">
            <a-upload
              name="landProofImg"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="landProofFileList"
              @preview="handlePreview"
              @change="handleLandProofChange"
              :maxCount="1">
              <div v-if="landProofFileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">上传土地证明</div>
              </div>
            </a-upload>
          </a-form-item>
        </a-col>
      </a-row>

      <a-divider orientation="left">检测报告</a-divider>

      <a-row :gutter="20">
        <a-col :span="24">
          <a-form-item label='环境检测报告' v-bind="formItemLayout">
            <a-upload
              name="envTestReport"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="envTestFileList"
              @preview="handlePreview"
              @change="handleEnvTestChange"
              :maxCount="1">
              <div v-if="envTestFileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">上传环境检测报告</div>
              </div>
            </a-upload>
          </a-form-item>
        </a-col>

        <a-col :span="24">
          <a-form-item label='产品检验报告' v-bind="formItemLayout">
            <a-upload
              name="productTestReport"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="productTestFileList"
              @preview="handlePreview"
              @change="handleProductTestChange"
              :maxCount="1">
              <div v-if="productTestFileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">上传产品检验报告</div>
              </div>
            </a-upload>
          </a-form-item>
        </a-col>

        <a-col :span="24">
          <a-form-item label='投入品证明（种子、肥料等）' v-bind="formItemLayout">
            <a-upload
              name="inputMaterialProof"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="inputMaterialFileList"
              @preview="handlePreview"
              @change="handleInputMaterialChange"
              :maxCount="1">
              <div v-if="inputMaterialFileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">上传投入品证明</div>
              </div>
            </a-upload>
          </a-form-item>
        </a-col>
      </a-row>

      <a-divider orientation="left">其他信息</a-divider>

      <a-row :gutter="20">
        <a-col :span="24">
          <a-form-item label='商家的其他要求或问题' v-bind="formItemLayout">
            <a-textarea
              :rows="4"
              v-decorator="['additionalRequests']"
              placeholder="请输入您的其他要求或问题（选填）" />
          </a-form-item>
        </a-col>

        <a-col :span="24" v-if="isEditMode && certificationData.adminRemark">
          <a-form-item label='管理员审核备注/驳回原因' v-bind="formItemLayout">
            <a-textarea
              :rows="3"
              :value="certificationData.adminRemark"
              disabled />
          </a-form-item>
        </a-col>
      </a-row>

      <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
        <img alt="预览" style="width: 100%" :src="previewImage" />
      </a-modal>
    </a-form>

    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        提交审核
      </a-button>
    </div>
  </a-drawer>
</template>

<script>import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}

const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}

export default {
  name: 'certificationForm',
  props: {
    certificationVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.certificationVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      isEditMode: false,
      certificationData: {},
      previewVisible: false,
      previewImage: '',
      idCardFileList: [],
      businessLicenseFileList: [],
      landProofFileList: [],
      envTestFileList: [],
      productTestFileList: [],
      inputMaterialFileList: []
    }
  },
  watch: {
    certificationVisiable: function (value) {
      if (value) {
        this.loadCertificationData()
      }
    }
  },
  methods: {
    moment,
    loadCertificationData () {
      this.loading = true
      this.$get('/cos/certification/queryCertificationByMerchant', {
        merchantId: this.currentUser.userId
      }).then((r) => {
        if (r.data && r.data.data) {
          this.isEditMode = true
          this.certificationData = r.data.data
          this.setFormValues(this.certificationData)
        } else {
          this.isEditMode = false
          this.certificationData = {}
          this.resetForm()
        }
        this.loading = false
      }).catch(() => {
        this.isEditMode = false
        this.certificationData = {}
        this.resetForm()
        this.loading = false
      })
    },
    setFormValues (data) {
      let fields = ['merchantId', 'status', 'additionalRequests']
      let obj = {}
      Object.keys(data).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = data[key]
        }
      })
      this.form.setFieldsValue(obj)

      this.initImageList('idCardImg', data.idCardImg, this.idCardFileList)
      this.initImageList('businessLicenseImg', data.businessLicenseImg, this.businessLicenseFileList)
      this.initImageList('landProofImg', data.landProofImg, this.landProofFileList)
      this.initImageList('envTestReport', data.envTestReport, this.envTestFileList)
      this.initImageList('productTestReport', data.productTestReport, this.productTestFileList)
      this.initImageList('inputMaterialProof', data.inputMaterialProof, this.inputMaterialFileList)
    },
    initImageList (fieldName, imagePath, fileList) {
      if (imagePath) {
        fileList.splice(0, fileList.length)
        fileList.push({
          uid: -1,
          name: imagePath,
          status: 'done',
          url: 'http://127.0.0.1:9527/imagesWeb/' + imagePath
        })
      } else {
        fileList.splice(0, fileList.length)
      }
    },
    resetForm () {
      this.form.resetFields()
      this.idCardFileList = []
      this.businessLicenseFileList = []
      this.landProofFileList = []
      this.envTestFileList = []
      this.productTestFileList = []
      this.inputMaterialFileList = []

      this.form.setFieldsValue({
        status: 0
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    handleIdCardChange ({ fileList }) {
      this.idCardFileList = fileList
    },
    handleBusinessLicenseChange ({ fileList }) {
      this.businessLicenseFileList = fileList
    },
    handleLandProofChange ({ fileList }) {
      this.landProofFileList = fileList
    },
    handleEnvTestChange ({ fileList }) {
      this.envTestFileList = fileList
    },
    handleProductTestChange ({ fileList }) {
      this.productTestFileList = fileList
    },
    handleInputMaterialChange ({ fileList }) {
      this.inputMaterialFileList = fileList
    },
    getImagePath (fileList) {
      if (!fileList || fileList.length === 0) return null
      const file = fileList[0]
      if (file.response) {
        return file.response
      } else if (file.name) {
        return file.name
      }
      return null
    },
    onClose () {
      this.resetForm()
      this.$emit('close')
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          let submitData = {
            ...values,
            idCardImg: this.getImagePath(this.idCardFileList),
            businessLicenseImg: this.getImagePath(this.businessLicenseFileList),
            landProofImg: this.getImagePath(this.landProofFileList),
            envTestReport: this.getImagePath(this.envTestFileList),
            productTestReport: this.getImagePath(this.productTestFileList),
            inputMaterialProof: this.getImagePath(this.inputMaterialFileList)
          }

          if (this.isEditMode) {
            submitData.id = this.certificationData.id
            this.loading = true
            this.$put('/cos/certification', submitData).then((r) => {
              this.$message.success('修改成功')
              this.resetForm()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          } else {
            this.loading = true
            this.$post('/cos/certification', submitData).then((r) => {
              this.$message.success('提交成功，请等待审核')
              this.resetForm()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped></style>
