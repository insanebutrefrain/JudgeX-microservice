<template>
  <div id="manageQuestionView" class="container">
    <!-- Search Form -->
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="id" label="题号" style="min-width: 240px">
        <a-input v-model="searchParams.id" placeholder="请输入题号"/>
      </a-form-item>
      <a-form-item field="title" label="题目名称" style="min-width: 240px">
        <a-input v-model="searchParams.title" placeholder="请输入题目名称"/>
      </a-form-item>
      <a-form-item field="content" label="题目内容" style="min-width: 240px">
        <a-input v-model="searchParams.content" placeholder="请输入题目内容"/>
      </a-form-item>
      <a-form-item field="tagList" label="标签" style="min-width: 280px">
        <a-input-tag v-model="searchParams.tagList" placeholder="请输入标签"/>
      </a-form-item>
      <a-form-item>
        <a-button type="outline" @click="onSearch">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider :size="0"/>

    <a-table
        :ref="tableRef"
        :columns="columns"
        :data="dataList"
        :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total: total,
      }"
        @page-change="onPageChange"
        :scroll="{ x: 'max-content' }"
        row-key="id"
        bordered
    >

      <template #tagList="{ record }">
        <a-space wrap>
          <a-tag v-for="(tag, index) in JSON.parse(record.tagList)" :key="index" color="green">
            {{ tag }}
          </a-tag>
        </a-space>
      </template>

      <template #content="{ record }">
        <div class="content-cell">
          <a-tooltip :content="record.content" position="top">
            <div class="truncate-text">{{ record.content }}</div>
          </a-tooltip>
        </div>
      </template>

      <template #answer="{ record }">
        <div class="answer-cell">
          <a-tooltip :content="record.answer" position="top">
            <div class="truncate-text">{{ record.answer }}</div>
          </a-tooltip>
        </div>
      </template>

      <template #judgeConfig="{ record }">
        <a-tooltip :content="record.judgeConfig" position="top">
          <div class="ellipsis-text" :title="JSON.stringify(record.judgeConfig)">
            {{ record.judgeConfig }}
          </div>
        </a-tooltip>
      </template>

      <template #judgeCaseVersion="{ record }">
        <a-tooltip :content="record.judgeCaseVersion" position="top">
          <div class="ellipsis-text" :title="JSON.stringify(record.judgeCaseVersion)">
            {{ record.judgeCaseVersion }}
          </div>
        </a-tooltip>

      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
      </template>
      <template #updateTime="{ record }">
        {{ moment(record.updateTime).format('YYYY-MM-DD HH:mm:ss') }}
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="doUpdate(record)" size="small">
            修改
          </a-button>
          <a-button status="danger" @click="doDelete(record)" size="small">
            删除
          </a-button>
        </a-space>
      </template>

    </a-table>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, watchEffect} from 'vue';
import {QuestionControllerService, QuestionQueryRequest} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import {useRouter} from "vue-router";
import moment from "moment/moment";

const tableRef = ref();
const dataList = ref([]);
const total = ref(0);
const searchParams = ref({
  id: null,
  title: "",
  content: "",
  tagList: [],
  pageSize: 10,
  current: 1,
} as QuestionQueryRequest);

const loadData = async () => {
  const result = await QuestionControllerService.listQuestionVoByPageAdminUsingPost(searchParams.value);
  if (result.code === 0) {
    dataList.value = result.data.records;
    total.value = parseInt(result.data.total);
  } else {
    message.error("加载失败: " + result.message);
  }
};

watchEffect(() => {
  loadData();
});

onMounted(() => {
  loadData();
});

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '标题',
    dataIndex: 'title',
    width: 180,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '标签',
    slotName: 'tagList',
    width: 120,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '内容',
    dataIndex: 'content',
    width: 200,
    ellipsis: true,
    slotName: 'content'
  },
  {
    title: '答案',
    dataIndex: 'answer',
    width: 150,
    ellipsis: true,
    slotName: 'answer'
  },
  {
    title: '判题配置',
    dataIndex: 'judgeConfig',
    width: 150,
    ellipsis: true,
    slotName: 'judgeConfig'
  },
  {
    title: '测试用例版本号',
    dataIndex: 'judgeCaseVersion',
    width: 150,
    ellipsis: true,
    slotName: 'judgeCaseVersion'
  },
  {
    title: '提交数',
    dataIndex: 'submitNum',
    width: 100,
    align: 'center'
  },
  {
    title: '通过数',
    dataIndex: 'acceptNum',
    width: 100,
    align: 'center'
  },
  {
    title: '创建用户ID',
    dataIndex: 'userId',
    width: 100,
    ellipsis: true
  },
  {
    title: '创建时间',
    slotName: 'createTime',
    width: 180,
    ellipsis: true
  },
  {
    title: '更新时间',
    slotName: 'updateTime',
    width: 180,
    ellipsis: true
  },
  {
    title: '操作',
    slotName: 'optional',
    width: 150,
    fixed: 'right',
    align: 'center'
  }
];

const onPageChange = (page) => {
  searchParams.value = {
    ...searchParams.value,
    current: page
  }
}

const onSearch = () => {
  searchParams.value = {
    ...searchParams.value,
    current: 1
  }
}

const doDelete = async (question) => {
  const res = await QuestionControllerService.deleteQuestionUsingPost({id: question.id});
  if (res.code === 0) {
    message.success("删除成功");
    await loadData();
  } else {
    message.error("删除失败: " + res.message);
  }
};

const router = useRouter();
const doUpdate = (question) => {
  router.push({
    path: "/update/question",
    query: {
      id: question.id
    }
  });
};
</script>

<style scoped>
.container {
  padding: 16px;
  background: #fff;
  border-radius: 4px;
}

.truncate-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.content-cell {
  max-width: 200px;
}

.answer-cell {
  max-width: 150px;
}

:deep(.arco-table-th) {
  background-color: #f7f8fa;
  font-weight: 500;
}

:deep(.arco-table-cell) {
  padding: 8px 12px;
}

:deep(.arco-tooltip-content) {
  max-width: 400px;
  word-wrap: break-word;
  white-space: normal;
}
</style>
