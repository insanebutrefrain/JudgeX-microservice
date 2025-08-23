<template>
  <div id="doQuestionView" class="question-container">
    <div class="container-row">
      <div class="left-pane">
        <a-tabs default-active-key="question">
          <a-tab-pane key="question" title="题目描述">
            <div class="scrollable-content">
              <QuestionDescriptionCard :question="question"/>
            </div>
          </a-tab-pane>

          <a-tab-pane key="answer" title="题解">
            <div class="scrollable-content">
              <a-card class="answer-card">
                <div class="empty-placeholder">
                  <p>暂无题解</p>
                </div>
              </a-card>
            </div>
          </a-tab-pane>

          <a-tab-pane key="comment" title="提交记录">
            <div class="scrollable-content">
              <SubmissionRecords :question-id="id"/>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>

      <div class="right-pane">
        <CodeSection :question-id="id"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
#doQuestionView {
  height: 100%;
  display: flex;
  overflow: hidden;
}

.question-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--color-bg-2);
  border-radius: 8px;
}

.container-row {
  display: flex;
  flex: 1;
  min-height: 0;
}

.left-pane {
  min-width: 650px;
  max-width: 750px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.right-pane {
  flex: 1;
  min-width: 300px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.arco-tabs-content) {
  padding: 0;
  flex: 1;
  min-height: 0;
  display: flex;
}

:deep(.arco-tabs-pane) {
  height: 100%;
  flex: 1;
}

:deep(.arco-card-body) {
  padding: 16px;
  height: 100%;
}

.scrollable-content {
  height: 80vh;
  overflow-y: auto;
  padding: 4px 16px;
}
</style>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {QuestionControllerService, QuestionVO} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import QuestionDescriptionCard from "@/components/DoQuestionView/QuestionDescriptionCard.vue";
import CodeSection from "@/components/DoQuestionView/CodeSection.vue";
import SubmissionRecords from "@/components/DoQuestionView/SubmissionRecords.vue";

const question = ref<QuestionVO>();

interface Props {
  id: string
}

const props = withDefaults(defineProps<Props>(), {
  id: () => ""
})

const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(props.id);
  if (res.code === 0) {
    question.value = res.data;
  } else {
    message.error("题目加载失败: " + res.message);
  }
};

onMounted(() => {
  loadData();
});
</script>
