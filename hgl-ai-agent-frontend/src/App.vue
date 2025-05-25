<script setup>
import Footer from './components/Footer.vue'
</script>

<template>
  <div class="app-root">
    <router-view />
    <Footer />
  </div>
</template>

<style>
@import './assets/main.css';
@import './assets/clean-geek.css';
html, body, #app, .app-root {
  height: 100%;
  width: 100%;
  min-width: 0;
  min-height: 100vh;
}
body {
  font-family: var(--font-main);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: var(--main-bg);
}
.app-root {
  min-height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
}
.app-root > *:not(footer) {
  flex: 1 0 auto;
  display: flex;
  flex-direction: column;
}
footer, .Footer {
  flex-shrink: 0;
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>
