<template>
  <div class="home geek-bg">
    <div class="hero geek-card geek-glow">
      <div class="hero-content">
        <h1 class="geek-title">HGL AI 应用中心</h1>
        <p class="geek-subtitle">探索智能对话的无限可能，极客风格，酷炫体验</p>
        <div class="hero-decoration">
          <div class="circle geek-glow"></div>
          <div class="circle geek-glow"></div>
          <div class="circle geek-glow"></div>
        </div>
      </div>
    </div>
    <div class="app-grid">
      <router-link to="/love-master" class="app-card geek-card">
        <div class="card-content">
          <div class="card-icon geek-avatar">❤️</div>
          <h2>AI 恋爱大师</h2>
          <p>与 AI 恋爱大师进行对话，获取情感建议</p>
          <div class="card-footer">
            <span class="enter-button geek-btn">开始对话 →</span>
          </div>
        </div>
      </router-link>
      <router-link to="/manus" class="app-card geek-card">
        <div class="card-content">
          <div class="card-icon geek-avatar">🤖</div>
          <h2>AI 超级智能体</h2>
          <p>与 AI 超级智能体进行对话，获取智能解答</p>
          <div class="card-footer">
            <span class="enter-button geek-btn">开始对话 →</span>
          </div>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { useHead } from '@vueuse/head'
useHead({
  title: 'HGL AI 应用中心 - 极客风格AI应用集',
  meta: [
    { name: 'description', content: 'HGL AI 应用中心，极客风格，酷炫体验，探索智能对话的无限可能。' },
    { name: 'keywords', content: 'AI, 极客, 智能对话, 酷炫, 应用中心, HGL' },
    { property: 'og:title', content: 'HGL AI 应用中心 - 极客风格AI应用集' },
    { property: 'og:description', content: '极客风格，酷炫体验，探索智能对话的无限可能。' },
    { property: 'og:type', content: 'website' }
  ]
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem 0 1rem;
  min-height: 80vh;
  position: relative;
  overflow: hidden;
}
.hero {
  text-align: center;
  margin-bottom: 3rem;
  padding: 3rem 0 2rem 0;
  position: relative;
  background: linear-gradient(135deg, #181c24 60%, #00ffe733 100%);
  border: 2px solid #00ffe7;
  box-shadow: 0 0 32px 0 #00ffe733;
}
.hero-content {
  position: relative;
  z-index: 1;
}
.hero-decoration {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  z-index: 0;
}
.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(45deg, rgba(0,255,231,0.08), rgba(0,191,255,0.08));
  animation: float 6s infinite ease-in-out;
}
.circle:nth-child(1) {
  width: 300px; height: 300px;
  top: -120px; left: -120px;
  animation-delay: 0s;
}
.circle:nth-child(2) {
  width: 180px; height: 180px;
  top: 60%; right: -90px;
  animation-delay: -2s;
}
.circle:nth-child(3) {
  width: 120px; height: 120px;
  bottom: -60px; left: 60%;
  animation-delay: -4s;
}
.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2.2rem;
  padding: 1rem 0;
  position: relative;
  z-index: 1;
}
.card-icon {
  font-size: 3.2rem;
  margin-bottom: 1.2rem;
  animation: bounce 2s infinite;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.app-card h2 {
  color: var(--geek-accent);
  margin-bottom: 1rem;
  font-size: 2rem;
  background: none;
  -webkit-background-clip: unset;
  -webkit-text-fill-color: unset;
}
.app-card p {
  color: #b0eaff;
  line-height: 1.6;
  margin-bottom: 2rem;
  font-size: 1.1rem;
}
.card-footer {
  text-align: right;
}
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}
@media (max-width: 900px) {
  .hero { padding: 2rem 0 1.2rem 0; }
  .app-grid { gap: 1.2rem; }
}
@media (max-width: 600px) {
  .home { padding: 0.5rem 0.2rem 0 0.2rem; }
  .hero { padding: 1.2rem 0 0.7rem 0; }
  .app-grid { grid-template-columns: 1fr; }
  .card-icon { font-size: 2.1rem; }
  .app-card h2 { font-size: 1.2rem; }
}
</style>