<template>
  <div id="questionView">
    <!--搜索-->
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="id" label="题号" style="min-width: 240px">
        <a-input v-model="searchParams.id"/>
      </a-form-item>
      <a-form-item field="title" label="题目名称" style="min-width: 240px">
        <a-input v-model="searchParams.title"/>
      </a-form-item>
      <a-form-item field="content" label="题目内容" style="min-width: 240px">
        <a-input v-model="searchParams.content"/>
      </a-form-item>
      <a-form-item field="tagList" label="标签" style="min-width: 280px">
        <a-input-tag v-model="searchParams.tagList"/>
      </a-form-item>
      <a-form-item>
        <a-button type="outline" @click="onSearch">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider :size="0"/>
    <!--题目列表-->
    <a-table :ref="tableRef"
             :columns="columns"
             :data="dataList"
             :pagination="{
                showTotal: true,
                pageSize:searchParams.pageSize,
                current:searchParams.current,
                total: total,
             }"

             @page-change="onPageChange"
    >
      <template #tagList="{ record }">
        <a-space wrap>
          <a-tag v-for="(tag, index) in JSON.parse(record.tagList)" :key="index" color="green">
            {{ tag }}
          </a-tag>
        </a-space>
      </template>
      <template #acceptRate="{ record }">
        <div v-if="record.submitNum">
          {{ 100 * record.acceptNum / record.submitNum }}%
        </div>
        <div v-else>
          无提交
        </div>
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format('YYYY-MM-DD') }}
      </template>
      <template #optional="{ record }">
        <a-button type="primary" @click="toQuestionPage(record)">
          做题
        </a-button>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import {ref, watchEffect} from 'vue';
import {QuestionControllerService, QuestionQueryRequest} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import {useRouter} from "vue-router";
import moment from "moment";
import isLogin from "@/access/isLogin";

const router = useRouter();
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
  if (!isLogin()) {
    console.log("未登录/已过期");
    await router.push({path: "/user/login"});
    return;
  }
  const result = await QuestionControllerService.listQuestionVoByPageUsingPost(searchParams.value);
  console.log(result);
  if (result.code === 0) {
    dataList.value = result.data.records;
    total.value = parseInt(result.data.total);
    message.info("查找到", result.data.records.length, "个题目");
    console.log(result.data.records);
  } else {
    message.error("加载失败: " + result.message);
  }
};
watchEffect(() => {
  loadData();
});


const columns = [
  {
    title: '题号',
    dataIndex: 'id',
  },
  {
    title: '题目名称',
    dataIndex: 'title',
  },
  {
    title: '标签',
    slotName: 'tagList'
  },
  {
    title: '通过率',
    slotName: 'acceptRate'
  },
  {
    title: '创建时间',
    slotName: 'createTime',
  },
  {
    slotName: 'optional'
  }
];

const onPageChange = (page) => {
  searchParams.value = {
    ...searchParams.value,
    current: page
  }
}

const toQuestionPage = (question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};
const onSearch = () => {
  searchParams.value = {
    ...searchParams.value,
    current: 1
  }
  // loadData();  // 已监听,不需要自己执行loadData
};
</script>

<style>
#questionView {
  margin: 0 auto;
  max-width: 1400px;
}
</style>
