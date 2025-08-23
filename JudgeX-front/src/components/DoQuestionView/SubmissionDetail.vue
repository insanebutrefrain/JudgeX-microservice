<template>
  <div class="submission-detail">
    <!-- 基本信息 -->
    <div class="basic-info">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="语言">
          {{ getLanguageText(submission.language) }}
        </a-descriptions-item>
        <a-descriptions-item label="判题结果">
          <a-tag :color="getStatusColor(submission.judgeResult)">
            {{ getStatusText(submission.judgeResult) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="提交时间">
          {{ formatTime(submission.createTime) }}
        </a-descriptions-item>
      </a-descriptions>
    </div>

    <!-- 测试用例结果 -->
    <div class="test-cases" v-if="submission.judgeInfoList && Array.isArray(submission.judgeInfoList)">
      <h3>测试用例结果</h3>
      <a-table
          :data="submission.judgeInfoList"
          :columns="testCaseColumns"
          :pagination="false"
      >
        <template #judgeResult="{ record }">
          <div>
            <a-tag :color="getStatusColor(record.judgeCaseResult)">
              {{ getStatusText(record.judgeCaseResult) }}
            </a-tag>
            <div v-if="record.errorMessage" class="error-message">
              {{ record.errorMessage }}
            </div>
          </div>
        </template>
      </a-table>
    </div>

    <!-- 代码展示 -->
    <div class="code-section">
      <div class="code-header">
        <h3>代码</h3>
        <a-button
            type="outline"
            class="copy-btn"
            @click="handleCopyCode"
        >
          复制代码
        </a-button>
      </div>
      <div id="code-viewer" ref="codeViewerRef" class="code-viewer" style="max-height: 400px"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import message from '@arco-design/web-vue/es/message';
import * as monaco from 'monaco-editor';
import {onMounted, ref} from 'vue';
import getStatusText from "../../commonTs/getStatusText.ts";
import getStatusColor from "../../commonTs/getStatusColor.ts";

const props = defineProps({
  submission: {
    type: Object,
    required: true
  }
});


const testCaseColumns = [
  {
    title: '用例结果',
    slotName: 'judgeResult',
    width: 200,
  },
  {
    title: '运行时间(ms)',
    dataIndex: 'time',
    width: 120,
  },
  {
    title: '使用内存(KB)',
    dataIndex: 'memory',
    width: 120,
  },
];

const getLanguageText = (language: string) => {
  const languageMap: Record<string, string> = {
    'java': 'Java',
    'python': 'Python',
    'cpp': 'C++'
  };
  return languageMap[language] || language;
};

const formatTime = (timeStr: string) => {
  return new Date(timeStr).toLocaleString();
};

const handleCopyCode = () => {
  navigator.clipboard.writeText(props.submission.code)
      .then(() => {
        message.success('代码已复制到剪贴板');
      })
      .catch(() => {
        message.error('复制失败');
      });
};

const codeViewerRef = ref();

onMounted(() => {
  if (!codeViewerRef.value) return;

  codeViewerRef.value = monaco.editor.create(
      codeViewerRef.value,
      {
        value: props.submission.code,
        language: props.submission.language,
        automaticLayout: true,
        theme: "vs-dark",
        lineNumbers: "on",
        minimap: {
          enabled: false,
        },
        readOnly: false,
        scrollBeyondLastLine: false,
        renderWhitespace: "selection",
        fontSize: 14,
        lineHeight: 20,
        roundedSelection: true,
        contextmenu: false,
      });
});
</script>

<style scoped>
.submission-detail {
  padding: 16px;
}

.basic-info {
  margin-bottom: 24px;
}

.test-cases {
  margin-bottom: 24px;
}

.test-cases h3, .code-section h3 {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 500;
}

.code-section {
  margin-top: 24px;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.code-viewer {
  height: 500px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  overflow: hidden;
}

.copy-btn {
  margin-left: 12px;
}

.error-message {
  color: var(--color-text-3);
  font-size: 12px;
  margin-top: 4px;
}
</style>
