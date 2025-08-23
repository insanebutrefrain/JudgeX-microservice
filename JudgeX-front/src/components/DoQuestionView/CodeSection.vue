<template>
  <div class="code-section">
    <a-form :model="form" layout="inline" class="language-selector">
      <a-form-item field="language" label="编程语言">
        <a-select v-model="form.language" :style="{width: '200px'}" placeholder="选择语言">
          <a-option value="java">Java</a-option>
          <a-option value="cpp">C++</a-option>
          <a-option value="python">Python</a-option>
        </a-select>
      </a-form-item>
    </a-form>

    <div class="editor-wrapper">
      <CodeEditor :value="form.code"
                  :language="form.language"
                  :handle-change="handleChange"
      style="height: 100%"/>
    </div>

    <div class="submit-area">
      <a-button type="primary" class="submit-btn" @click="doSubmit" :loading="submitting">
        <template #icon>
          <icon-send/>
        </template>
        提交代码
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import CodeEditor from "@/components/CodeEditor.vue";
import {QuestionControllerService} from "../../../generated";
import message from "@arco-design/web-vue/es/message";

const props = defineProps({
  questionId: {
    type: String,
    required: true
  }
});

const form = ref({
  questionId: props.questionId,
  language: "java",
  code: "",
});

const submitting = ref(false);


const handleChange = (v: string) => {
  form.value.code = v;
  console.log("code:", form.value.code);
}

const doSubmit = async () => {
  if (!form.value.questionId) {
    message.error("题目不存在");
    return;
  }
  if (!form.value.code || form.value.code.trim() === "") {
    message.error("提交代码为空");
    return;
  }

  submitting.value = true;
  try {
    const res = await QuestionControllerService.doSubmitUsingPost(form.value);
    if (res.code === 0) {
      message.success("提交成功");
    } else {
      message.error("提交失败: " + res.message);
    }
  } catch (e) {
    message.error("提交出错");
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.code-section {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--color-bg-2);
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.language-selector {
  margin-bottom: 12px;
  flex: 0 0 auto;
}

.editor-wrapper {
  flex: 1;
  min-height: 0;
  border: 1px solid var(--color-border-2);
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.submit-area {
  margin-top: 16px;
  text-align: right;
  flex: 0 0 auto;
}

.submit-btn {
  width: 180px;
  height: 40px;
  border-radius: 20px;
  font-weight: 500;
}
</style>
