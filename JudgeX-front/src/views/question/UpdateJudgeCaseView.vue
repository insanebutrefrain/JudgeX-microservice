<template>
  <div class="judge-case-upload-container">
    <a-card title="测试用例管理">
      <a-button type="primary" status="success" style=" margin-bottom: 16px" @click="goBack">
        ←返回题目管理
      </a-button>

      <a-alert v-if="!questionId" type="error" show-icon>
        未获取到题目ID，请从题目管理页面进入
      </a-alert>
      <template v-else>
        <a-space direction="vertical" size="large" fill>
          <a-card title="上传测试用例">
            <a-form layout="vertical">
              <a-form-item label="输入文件" extra="请上传多个输入文件（.txt格式）">
                <a-upload
                    multiple
                    accept=".txt"
                    :file-list="inputFiles"
                    @change="handleInputFilesChange"
                    :custom-request="() => {}"
                >
                  <template #upload-button>
                    <a-button type="outline">
                      <icon-upload/>
                      选择输入文件
                    </a-button>
                  </template>
                </a-upload>
              </a-form-item>

              <a-form-item label="输出文件" extra="请上传与输入文件对应的输出文件（.txt格式）">
                <a-upload
                    multiple
                    accept=".txt"
                    :file-list="outputFiles"
                    @change="handleOutputFilesChange"
                    :custom-request="() => {}"
                >
                  <template #upload-button>
                    <a-button type="outline">
                      <icon-upload/>
                      选择输出文件
                    </a-button>
                  </template>
                </a-upload>
              </a-form-item>

              <a-form-item>
                <a-button
                    type="primary"
                    :loading="uploading"
                    :disabled="inputFiles.length === 0 || outputFiles.length === 0"
                    @click="handleUpload"
                >
                  上传测试用例
                </a-button>
              </a-form-item>
            </a-form>
          </a-card>

          <a-card title="现有测试用例">
            <a-table
                :columns="columns"
                :data="testCases"
                :loading="loading"
                row-key="id"
            >
              <template #input="{ record }">
                <div class="test-case-content">{{ record.input }}</div>
              </template>
              <template #output="{ record }">
                <div class="test-case-content">{{ record.output }}</div>
              </template>
            </a-table>
          </a-card>
        </a-space>
      </template>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRoute, useRouter} from 'vue-router';

import message from '@arco-design/web-vue/es/message';
import {IconUpload} from '@arco-design/web-vue/es/icon';
import {FileRecordControllerService} from "../../../generated";

const route = useRoute();
const router = useRouter();
const questionId = ref<string | null>(null);

const inputFiles = ref<any[]>([]);
const outputFiles = ref<any[]>([]);
const uploading = ref(false);
const loading = ref(false);
const testCases = ref<any[]>([]);

const columns = [
  {
    title: '输入',
    slotName: 'input',
    width: '50%'
  },
  {
    title: '输出',
    slotName: 'output',
    width: '50%'
  }
];

onMounted(() => {
  questionId.value = route.query.id as string || null;
  if (questionId.value) {
    fetchTestCases();
  }
});

const handleInputFilesChange = (files: any[]) => {
  inputFiles.value = files.map(file => {
    return {
      ...file,
      name: file.name,
      status: file.status || 'done'
    };
  });
};

const handleOutputFilesChange = (files: any[]) => {
  outputFiles.value = files.map(file => {
    return {
      ...file,
      name: file.name,
      status: file.status || 'done'
    };
  });
};
const handleUpload = async () => {
  if (!questionId.value) return;

  if (inputFiles.value.length !== outputFiles.value.length) {
    message.error('输入文件和输出文件数量必须相同');
    return;
  }

  try {
    uploading.value = true;

    const formData = new FormData();

    inputFiles.value.forEach(file => {
      formData.append('inputFiles', file.file);  // 字段名必须为 inputFiles
    });

    outputFiles.value.forEach(file => {
      formData.append('outputFiles', file.file); // 字段名必须为 outputFiles
    });

    const res = await FileRecordControllerService.uploadJudgecaseUsingPost(
        Number(questionId.value),
        formData.getAll('outputFiles') as File[],
        formData.getAll('inputFiles') as File[]
    );

    if (res.code === 0) {
      message.success('测试用例上传成功');
      inputFiles.value = [];
      outputFiles.value = [];
      await fetchTestCases();
    } else {
      message.error('测试用例上传失败: ' + res.message);
    }
  } catch (error) {
    message.error('上传过程中发生错误');
    console.error(error);
  } finally {
    uploading.value = false;
  }
};

const fetchTestCases = async () => {
  if (!questionId.value) return;

  try {
    loading.value = true;
    const res = await FileRecordControllerService.getJudgecaseUsingPost(Number(questionId.value));
    if (res.code === 0) {
      testCases.value = res.data;
    } else {
      message.error('获取测试用例失败: ' + res.message);
    }
  } catch (error) {
    message.error('获取测试用例过程中发生错误');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.push({
    path: '/update/question',
    query: {
      id: questionId.value
    }
  });
};
</script>

<style scoped>
.judge-case-upload-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-case-content {
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 100px;
  overflow-y: auto;
}
</style>
