<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24" >
            <a-form-model-item label="会议名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="meetingName">
              <a-input v-model="model.meetingName" placeholder="请输入会议名称" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="会议地点" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="address">
              <a-input v-model="model.address" placeholder="请输入会议地点" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="会议时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="meetingTime">
              <j-date placeholder="请选择会议时间" v-model="model.meetingTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="上报时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="reportingTime">
              <j-date placeholder="请选择上报时间" v-model="model.reportingTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="主持人姓名" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="hostName">
              <a-input v-model="model.hostName" placeholder="请输入主持人姓名" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="会议记录人姓名" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="recorderName">
              <a-input v-model="model.recorderName" placeholder="请输入会议记录人姓名" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="会议内容摘要" :labelCol="labelCol2" :wrapperCol="wrapperCol2" prop="summary">
              <a-textarea v-model="model.summary" rows="4" placeholder="请输入会议内容摘要" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="会议记录" :labelCol="labelCol2" :wrapperCol="wrapperCol2" prop="record">
              <a-textarea v-model="model.record" rows="4" placeholder="请输入会议记录" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="审核状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="verifyStatus">
              <a-input v-model="model.verifyStatus" placeholder="请输入审核状态" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="附件说明" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="explanation">
              <a-input v-model="model.explanation" placeholder="请输入附件说明" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="附件文件" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="path">
              <j-upload v-model="model.path"  ></j-upload>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
      <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="民主生活参会人员表" :key="refKeys[0]" :forceRender="true">
        <j-vxe-table
          keep-source
          :ref="refKeys[0]"
          :loading="smartDemocraticLifePeopleTable.loading"
          :columns="smartDemocraticLifePeopleTable.columns"
          :dataSource="smartDemocraticLifePeopleTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"
          />
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

  import { getAction } from '@/api/manage'
  import { JVxeTableModelMixin } from '@/mixins/JVxeTableModelMixin.js'
  import { JVXETypes } from '@/components/jeecg/JVxeTable'
  import { getRefPromise,VALIDATE_FAILED} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import { validateDuplicateValue } from '@/utils/util'
  import JFormContainer from '@/components/jeecg/JFormContainer'

  export default {
    name: 'SmartDemocraticLifeMeetingForm',
    mixins: [JVxeTableModelMixin],
    components: {
      JFormContainer,
    },
    data() {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        labelCol2: {
          xs: { span: 24 },
          sm: { span: 3 },
        },
        wrapperCol2: {
          xs: { span: 24 },
          sm: { span: 20 },
        },
        model:{
         },
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {
           meetingName: [
              { required: true, message: '请输入会议名称!'},
           ],
           address: [
              { required: true, message: '请输入会议地点!'},
           ],
           meetingTime: [
              { required: true, message: '请输入会议时间!'},
           ],
           reportingTime: [
              { required: true, message: '请输入上报时间!'},
           ],
           hostName: [
              { required: true, message: '请输入主持人姓名!'},
           ],
           recorderName: [
              { required: true, message: '请输入会议记录人姓名!'},
           ],
           summary: [
              { required: true, message: '请输入会议内容摘要!'},
           ],
           record: [
              { required: true, message: '请输入会议记录!'},
           ],
        },
        refKeys: ['smartDemocraticLifePeople', ],
        tableKeys:['smartDemocraticLifePeople', ],
        activeKey: 'smartDemocraticLifePeople',
        // 民主生活参会人员表
        smartDemocraticLifePeopleTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '参会人员姓名',
              key: 'participantName',
               type: JVXETypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
          ]
        },
        url: {
          add: "/smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/add",
          edit: "/smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/edit",
          queryById: "/smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/queryById",
          smartDemocraticLifePeople: {
            list: '/smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/querySmartDemocraticLifePeopleByMainId'
          },
        }
      }
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
    },
    created () {
    },
    methods: {
      addBefore(){
        this.smartDemocraticLifePeopleTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        this.$nextTick(() => {
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.smartDemocraticLifePeople.list, params, this.smartDemocraticLifePeopleTable)
        }
      },
      //校验所有一对一子表表单
        validateSubForm(allValues){
            return new Promise((resolve,reject)=>{
              Promise.all([
              ]).then(() => {
                resolve(allValues)
              }).catch(e => {
                if (e.error === VALIDATE_FAILED) {
                  // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
                  this.activeKey = e.index == null ? this.activeKey : this.refKeys[e.index]
                } else {
                  console.error(e)
                }
              })
            })
        },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          smartDemocraticLifePeopleList: allValues.tablesValue[0].tableData,
        }
      },
      validateError(msg){
        this.$message.error(msg)
      },

    }
  }
</script>

<style scoped>
</style>