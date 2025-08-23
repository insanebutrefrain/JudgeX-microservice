<template>
  <div id="addQuestionView">
    <a-form :model="form"
            :style="{ width: '600px', marginLeft: '32px' }"
            label-align="left">

      <a-form-item field="title" label="标题">
        <a-input v-model="form.title"/>
      </a-form-item>

      <a-form-item field="answer" label="答案">
        <MdEditor :value="form.answer" :handle-change="onAnswerChange"
                  style="min-width: 800px"/>
      </a-form-item>

      <a-form-item field="content" label="题目内容">
        <MdEditor :value="form.content" :handle-change="onContentChange"
                  style="min-width: 800px"/>
      </a-form-item>

      <a-form-item field="tagList" label="标签">
        <a-input-tag v-model="form.tagList" allow-clear/>
      </a-form-item>


      <a-form-item label="判题配置" :content-flex="false" :merge-props="false"
                   style="min-width: 480px;">
        <a-space direction="vertical">
          <a-form-item field="judgeConfig.timeLimit" label="时间限制(ms)">
            <a-input-number v-model="form.judgeConfig.timeLimit"
                            mode="button" size="large" :min="0"/>
          </a-form-item>
          <a-form-item field="judgeConfig.memoryLimit" label="内存限制(kb)">
            <a-input-number v-model="form.judgeConfig.memoryLimit"
                            mode="button" size="large" :min="0"/>
          </a-form-item>
        </a-space>
      </a-form-item>

      <div style="margin-top: 16px"></div>
      <a-form-item>
        <a-button type="primary" style="min-width: 160px" html-type="submit" @click="doSubmit">提交</a-button>
        <a-button v-if="form.id" type="outline" style="margin-left: 16px" @click="navigateToTestCaseManagement">
          管理测试用例
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import MdEditor from "@/components/MdEditor.vue";
import {QuestionAddRequest, QuestionControllerService} from "../../../generated";
import {useRoute, useRouter} from "vue-router";
import message from "@arco-design/web-vue/es/message";

const form = ref({
  title: "",
  answer: "",
  content: "",
  tagList: [],
  judgeConfig: {
    memoryLimit: 1000,
    timeLimit: 1000
  },
} as QuestionAddRequest);

const route = useRoute();
const router = useRouter();
const updatePage = route.path.includes("update");

const loadData = async () => {
  const id = route.query.id;
  if (!id) return;
  const res = await QuestionControllerService.getQuestionByIdUsingGet(id);
  if (res.code === 0) {
    form.value = res.data;
    form.value.tagList = JSON.parse(res.data?.tagList);
    form.value.judgeConfig = JSON.parse(res.data?.judgeConfig);
    if (!form.value.tagList) form.value.tagList = [];
    if (!form.value.judgeConfig) form.value.judgeConfig = {
      memoryLimit: 1000,
      timeLimit: 1000
    };
  } else {
    message.error("加载失败: " + res.message);
  }
};
onMounted(() => {
  loadData();
});

const onAnswerChange = (v) => {
  form.value.answer = v;
};

const onContentChange = (v) => {
  form.value.content = v;
};

const doSubmit = async () => {
  if (updatePage) {
    const res = await QuestionControllerService.updateQuestionUsingPost(form.value);
    if (res.code === 0) {
      message.info("更新成功");
    } else {
      message.error("更新失败: " + res.message);
    }
  } else {
    const res = await QuestionControllerService.addQuestionUsingPost(form.value);
    if (res.code === 0) {
      message.success("提交成功");
      form.value.id = res.data;
    } else {
      message.error("提交失败: " + res.message);
    }
  }
};

const navigateToTestCaseManagement = () => {
  if (form.value.id) {
    router.push({
      path: "/update/judgecase",
      query: {
        id: form.value.id
      }
    });
  }
};
</script>


<style scoped>
.arco-form-item-label-col > .arco-form-item-label {
  min-width: 200px;
}
</style>
