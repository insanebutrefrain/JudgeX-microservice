<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh; display: flex; flex-direction: column">
      <a-layout-header class="header">
        <Header />
      </a-layout-header>

      <a-layout-content class="content">
        <router-view />
      </a-layout-content>

      <a-layout-footer class="footer"> JudgeX by insane </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped lang="less">
#basicLayout {
  height: 100vh;
  display: flex;
  flex-direction: column;

  .header {
    box-shadow: #c3c3c3 1px 1px 3px;
    flex: 0 0 auto;
    //padding-right: 28px;
  }

  .content {
    flex: 1;
    min-height: 0;
    overflow: auto;
    width: 100%;
    padding: 4px;
  }

  .footer {
    background: #efefef;
    padding: 10px;
    text-align: center;
    flex: 0 0 auto;
  }
}
</style>

<script setup lang="ts">
import Header from "@/components/GlobalHeader/Header.vue";
</script>
