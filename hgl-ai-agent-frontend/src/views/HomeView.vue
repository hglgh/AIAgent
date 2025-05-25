<template>
  <div class="home">
    <div class="hero geek-card geek-glow">
      <div class="hero-content">
        <h1 class="geek-title">HGL AI 应用中心</h1>
        <p class="geek-subtitle">探索智能对话的无限可能，极客白色主色调，帅气酷炫</p>
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
  title: 'HGL AI 应用中心 - 极客白色主色调AI应用集',
  meta: [
    { name: 'description', content: 'HGL AI 应用中心，极客白色主色调，帅气酷炫，探索智能对话的无限可能。' },
    { name: 'keywords', content: 'AI, 极客, 智能对话, 白色, 酷炫, 应用中心, HGL' },
    { property: 'og:title', content: 'HGL AI 应用中心 - 极客白色主色调AI应用集' },
    { property: 'og:description', content: '极客白色主色调，帅气酷炫，探索智能对话的无限可能。' },
    { property: 'og:type', content: 'website' }
  ]
})
</script>

<style scoped>
.home {
  width: 100%;
  min-height: 80vh;
  padding: 2rem 1rem 0 1rem;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
.hero {
  text-align: center;
  margin-bottom: 3rem;
  padding: 3rem 0 2rem 0;
  position: relative;
  background: var(--card-bg);
  border: 1.5px solid var(--primary-glow);
  box-shadow: 0 2px 16px 0 var(--primary-glow);
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
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
  background: linear-gradient(45deg, #e0edff 0%, #00eaff 100%);
  opacity: 0.13;
  animation: float 6s infinite ease-in-out;
}
.circle:nth-child(1) {
  width: 220px; height: 220px;
  top: -80px; left: -80px;
  animation-delay: 0s;
}
.circle:nth-child(2) {
  width: 140px; height: 140px;
  top: 60%; right: -70px;
  animation-delay: -2s;
}
.circle:nth-child(3) {
  width: 90px; height: 90px;
  bottom: -45px; left: 60%;
  animation-delay: -4s;
}
.app-grid {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  box-sizing: border-box;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
  gap: 2.2rem;
  padding: 1rem 0;
  position: relative;
  z-index: 1;
}
.card-icon {
  font-size: 2.8rem;
  margin-bottom: 1.2rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.app-card.geek-card {
  box-shadow: 0 2px 16px 0 var(--primary-glow);
  border: 1.5px solid var(--primary-light);
}
.geek-title, h1 {
  color: var(--primary);
  text-shadow: 0 2px 8px var(--primary-glow);
  font-size: 2.5rem;
}
.app-card h2 {
  color: var(--primary);
  margin-bottom: 1rem;
  font-size: 2rem;
  text-shadow: 0 1px 4px var(--primary-light);
}
.app-card p {
  color: var(--sub-text);
  line-height: 1.6;
  margin-bottom: 2rem;
  font-size: 1.1rem;
}
.card-footer {
  text-align: right;
}
.geek-btn, .enter-button {
  box-shadow: 0 2px 8px 0 var(--primary-glow);
  text-shadow: none;
}
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}
@media (max-width: 1200px) {
  .app-grid { grid-template-columns: repeat(2, 1fr); gap: 1.5rem; }
  .hero { max-width: 95vw; }
}
@media (max-width: 900px) {
  .hero { padding: 2rem 0 1.2rem 0; max-width: 100%; }
  .app-grid { gap: 1.2rem; }
}
@media (max-width: 767px) {
  .home { padding: 0.5rem 0.2rem 0 0.2rem; }
  .hero { padding: 1.2rem 0 0.7rem 0; margin-bottom: 1.2rem; }
  .app-grid { grid-template-columns: 1fr; gap: 0.7rem; }
  .card-icon { font-size: 1.5rem; }
  .app-card h2 { font-size: 1.1rem; }
  .circle { display: none; }
}
</style>