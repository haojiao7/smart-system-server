<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="单位ID">
              <a-input placeholder="请输入单位ID" v-model="queryParam.departId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="会议时间">
              <j-date placeholder="请选择会议时间" v-model="queryParam.meetTime"></j-date>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="会议地点">
                <a-input placeholder="请输入会议地点" v-model="queryParam.meetLocation"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="会议名称">
                <a-input placeholder="请输入会议名称" v-model="queryParam.meetName"></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('党内谈话表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <smart-inner-party-talk-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import SmartInnerPartyTalkModal from './modules/SmartInnerPartyTalkModal'
  import '@/assets/less/TableExpand.less'

  export default {
    name: "SmartInnerPartyTalkList",
    mixins:[JeecgListMixin],
    components: {
      SmartInnerPartyTalkModal
    },
    data () {
      return {
        description: '党内谈话表管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'会议时间',
            align:"center",
            dataIndex: 'meetTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'会议地点',
            align:"center",
            dataIndex: 'meetLocation'
          },
          {
            title:'会议名称',
            align:"center",
            dataIndex: 'meetName'
          },
          {
            title:'主持人工号',
            align:"center",
            dataIndex: 'hostNo_dictText'
          },
          {
            title:'受约谈函询人工号',
            align:"center",
            dataIndex: 'talkedNo_dictText'
          },
          {
            title:'受诫勉谈话人工号',
            align:"center",
            dataIndex: 'inquirerNo_dictText'
          },
          {
            title:'受党纪处分人工号',
            align:"center",
            dataIndex: 'punisherNo_dictText'
          },
          {
            title:'记录人工号',
            align:"center",
            dataIndex: 'recorderNo_dictText'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/SmartInnerPartyTalk/smartInnerPartyTalk/list",
          delete: "/SmartInnerPartyTalk/smartInnerPartyTalk/delete",
          deleteBatch: "/SmartInnerPartyTalk/smartInnerPartyTalk/deleteBatch",
          exportXlsUrl: "/SmartInnerPartyTalk/smartInnerPartyTalk/exportXls",
          importExcelUrl: "SmartInnerPartyTalk/smartInnerPartyTalk/importExcel",
          
        },
        dictOptions:{},
        superFieldList:[],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
         fieldList.push({type:'string',value:'departId',text:'单位ID',dictCode:''})
         fieldList.push({type:'date',value:'meetTime',text:'会议时间'})
         fieldList.push({type:'string',value:'meetLocation',text:'会议地点',dictCode:''})
         fieldList.push({type:'string',value:'meetName',text:'会议名称',dictCode:''})
         fieldList.push({type:'sel_user',value:'hostNo',text:'主持人工号'})
         fieldList.push({type:'sel_user',value:'talkedNo',text:'受约谈函询人工号'})
         fieldList.push({type:'sel_user',value:'inquirerNo',text:'受诫勉谈话人工号'})
         fieldList.push({type:'sel_user',value:'punisherNo',text:'受党纪处分人工号'})
         fieldList.push({type:'string',value:'abs',text:'会议摘要',dictCode:''})
         fieldList.push({type:'sel_user',value:'recorderNo',text:'记录人工号'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>