<script setup lang="ts">
import { RouterView } from 'vue-router';

import CommonHeader from './components/CommonHeader.vue';


</script>

<template>

  <div class="app-layout">

    <CommonHeader />

    <main class="app-content">
      <RouterView />
    </main>

  </div>

</template>

<style>
/* アプリ全体の共通リセット・ベーススタイル */
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #fafafa;
  color: #333;
}

.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-content {
  padding: 20px;
  box-sizing: border-box;
}

@media screen and (max-width: 440px) {
  .app-layout {
    width: 440px;
  }

  .app-content {
    width: 440px;
    padding: 0;
  }
}
</style>