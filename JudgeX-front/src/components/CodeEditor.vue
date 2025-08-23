<template>
  <div id="code-editor" ref="codeEditorRef"></div>
</template>

<script setup lang="ts">
import * as monaco from 'monaco-editor'
import {onMounted, ref, toRaw, watch} from "vue";

const codeEditorRef = ref();
const codeEditor = ref();

interface Props {
  value: string;
  language?: string;
  handleChange: (v: string) => void;
}

const props = withDefaults(defineProps<Props>(), {
  value: () => '',
  language: () => "java",
  handleChange: (v) => {
    console.log("????");
  }
});


// 监听语言变化
watch(() => props.language, () => {
  if (codeEditor.value) {
    monaco.editor.setModelLanguage(toRaw(codeEditor.value).getModel(), props.language);
  }
});

onMounted(() => {
  if (!codeEditorRef.value) return;
  codeEditor.value = monaco.editor.create(
      codeEditorRef.value,
      {
        value: props.modelValue,
        language: props.language,
        automaticLayout: true,
        theme: "vs-dark",
        lineNumbers: "on",
        minimap: {
          enabled: true,
          autohide: true,
        },
        readOnly: false,
        colorDecorators: true,
      }
  );
  codeEditor.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(codeEditor.value).getValue());
  });
});

</script>

<style scoped>
</style>
