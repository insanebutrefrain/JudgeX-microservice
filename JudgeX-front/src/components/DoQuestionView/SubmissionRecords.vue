<template>
  <div class="submission-records">
    <div class="filter-container">
      <a-space :size="16">
        <a-select
            v-model="filter.judgeResult"
            placeholder="提交状态"
            style="width: 120px"
            allow-clear
        >
          <a-option value="">所有状态</a-option>
          <a-option value="Accepted">答案正确</a-option>
          <a-option value="Wrong Answer">答案错误</a-option>
          <a-option value="Time Limit Exceeded">运行超时</a-option>
          <a-option value="Memory Limit Exceeded">内存超限</a-option>
          <a-option value="Compile Error">编译错误</a-option>
          <a-option value="Runtime Error">运行错误</a-option>
          <a-option value="Waiting">等待中</a-option>
        </a-select>

        <a-select
            v-model="filter.language"
            placeholder="提交语言"
            style="width: 120px"
            allow-clear
        >
          <a-option value="">所有语言</a-option>
          <a-option value="java">Java</a-option>
          <a-option value="python" disabled>Python</a-option>
          <a-option value="cpp" disabled>C++</a-option>
        </a-select>

        <!-- 添加的刷新按钮 -->
        <a-button type="primary" @click="handleRefresh">
          <template #icon>
            <icon-refresh/>
          </template>
          刷新
        </a-button>
      </a-space>
    </div>

    <a-table
        :columns="columns"
        :data="data"
        :pagination="pagination"
        @page-change="handlePageChange"
        :loading="loading"
        class="submission-table"
    >
      <template #index="{ rowIndex }">
        {{ pagination.total - (pagination.current - 1) * pagination.pageSize - rowIndex }}
      </template>

      <template #judgeResult="{ record }">
        <a-tag :color="getStatusColor(record.judgeResult)">
          {{ getStatusText(record.judgeResult) }}
        </a-tag>
      </template>

      <template #time="{ record }">
        <div class="right-align" v-if="record.judgeInfoList && Array.isArray(record.judgeInfoList)">
          {{ getMaxTime(record.judgeInfoList) }} ms
        </div>
        <div class="right-align" v-else>-</div>
      </template>
      <template #memory="{ record }">
        <div class="right-align" v-if="record.judgeInfoList && Array.isArray(record.judgeInfoList)">
          {{ getMaxMemory(record.judgeInfoList) / 1024 }} KB
        </div>
        <div class="right-align" v-else>-</div>
      </template>
      <template #action="{ record }">
        <a-button type="text" size="small" @click="handleViewDetail(record)">
          查看详情
        </a-button>
      </template>
    </a-table>

    <!-- 提交详情对话框 -->
    <a-modal
        v-model:visible="detailVisible"
        title="提交详情"
        width="800px"
        :footer="false"
    >
      <SubmissionDetail :submission="currentSubmission" v-if="detailVisible"/>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, watch} from 'vue';
import {QuestionControllerService} from '../../../generated';
import message from '@arco-design/web-vue/es/message';
import SubmissionDetail from './SubmissionDetail.vue';
import {IconRefresh} from '@arco-design/web-vue/es/icon';
import getStatusText from "../../commonTs/getStatusText.ts";
import getStatusColor from "../../commonTs/getStatusColor.ts";

interface Props {
  questionId: string;
}

const props = defineProps<Props>();

interface Filter {
  judgeResult: string;
  language: string;
}

interface Pagination {
  current: number;
  pageSize: number;
  total: number;
}

interface JudgeInfoItem {
  time?: number;
  memory?: number;
  judgeCaseResult?: string;
  errorMessage?: string;
}

interface Submission {
  id: number;
  questionId: number;
  userId: number;
  language: string;
  code: string;
  status: number;
  judgeInfoList?: JudgeInfoItem[];
  judgeResult?: string;
  createTime: string;
}

const filter = ref<Filter>({
  judgeResult: '',
  language: '',
});

const pagination = ref<Pagination>({
  current: 1,
  pageSize: 10,
  total: 0,
});

const data = ref<Submission[]>([]);
const loading = ref(false);
const detailVisible = ref(false);
const currentSubmission = ref<Submission | null>(null);

const columns = [
  {
    title: '行号',
    slotName: 'index',
  },
  {
    title: '判题结果',
    slotName: 'judgeResult',
  },
  {
    title: '提交语言',
    dataIndex: 'language',
  },
  {
    title: '执行时间',
    slotName: 'time',
  },
  {
    title: '消耗内存',
    slotName: 'memory',
  },
  {
    title: '操作',
    slotName: 'action',
  },
];

const getMaxTime = (judgeInfoList: JudgeInfoItem[]) => {
  if (!judgeInfoList || judgeInfoList.length === 0) return '-';
  return Math.max(...judgeInfoList.map(item => item.time || 0));
};

const getMaxMemory = (judgeInfoList: JudgeInfoItem[]) => {
  if (!judgeInfoList || judgeInfoList.length === 0) return '-';
  return Math.max(...judgeInfoList.map(item => item.memory || 0));
};

const getSubmitRecord = async () => {
  try {
    loading.value = true;
    const params = {
      questionId: props.questionId,
      judgeResult: filter.value.judgeResult || undefined,
      language: filter.value.language || undefined,
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
    };

    const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(params);
    if (res.code === 0) {
      message.info('获取提交记录成功');
      data.value = res.data.records || [];
      pagination.value.total = res.data.total || 0;
      console.log(res.data.records);
    } else {
      message.error('获取提交记录失败: ' + res.message);
    }
  } catch (err) {
    message.error('获取提交记录失败');
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (page: number) => {
  pagination.value.current = page;
  getSubmitRecord();
};

const handleViewDetail = (record: Submission) => {
  currentSubmission.value = record;
  detailVisible.value = true;
};

// 新增的刷新方法
const handleRefresh = () => {
  getSubmitRecord();
};

// 监听筛选条件变化
watch(
    () => [filter.value.judgeResult, filter.value.language],
    () => {
      pagination.value.current = 1;
      getSubmitRecord();
    }
);

onMounted(() => {
  getSubmitRecord();
});
</script>

<style scoped>
.submission-records {
  padding: 16px;
}

.filter-container {
  margin-bottom: 16px;
}

.submission-table {
  margin-top: 16px;
}

.right-align {
  text-align: right;
}
</style>
