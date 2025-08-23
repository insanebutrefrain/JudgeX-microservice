<template>
  <a-card v-if="question" :title="question.title" class="question-card">
    <a-descriptions :column="{xs:2,md:2,lg:2}" bordered>
      <a-descriptions-item label="时间限制">
        <span class="limit-value">{{ question.judgeConfig.timeLimit }}</span> ms
      </a-descriptions-item>
      <a-descriptions-item label="内存限制">
        <span class="limit-value">{{ question.judgeConfig.memoryLimit }}</span> KB
      </a-descriptions-item>
    </a-descriptions>

    <div class="content-wrapper">
      <MdView :value="question.content"/>
    </div>

    <template #extra>
      <a-space wrap>
        <a-tag v-for="(tag, index) of question.tags"
               :key="index"
               color="arcoblue"
               class="tag">
          {{ tag }}
        </a-tag>
      </a-space>
    </template>
  </a-card>
</template>

<script setup lang="ts">
import {QuestionVO} from "../../../generated";
import MdView from "@/components/MdView.vue";

defineProps<{
  question: QuestionVO | undefined
}>();
</script>

<style scoped>
.question-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.content-wrapper {
  margin-top: 16px;
  padding: 4px;
  background-color: var(--color-fill-1);
  border-radius: 4px;
}

.limit-value {
  font-weight: bold;
}

.tag {
  margin-right: 8px;
  border-radius: 12px;
}
</style>
