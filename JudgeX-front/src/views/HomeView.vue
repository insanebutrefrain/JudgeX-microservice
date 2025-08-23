<template>
  <CodeEditor :value="codeValue" :handle-change="onCodeChange"/>
  <MdEditor :value="mdValue" :handle-change="onMdChange"/>
  <div v-for="i in Array(50)">
    homeView
  </div>
</template>

<script setup lang="ts">

import {ref} from "vue";
import MdEditor from "@/components/MdEditor.vue";
import CodeEditor from "@/components/CodeEditor.vue";

const codeValue = ref('');
const mdValue = ref('');

function onCodeChange(v) {
  codeValue.value = v;
  console.log("code: ", v);
}

function onMdChange(v) {
  mdValue.value = v;
  console.log("md: ", v);
}
</script>
