<template>
  <footer class="geek-footer">
    <div class="footer-content">
      <span class="footer-logo geek-glow">🤖</span>
      <span class="footer-text">© 2024 HGL AI Agent. All Rights Reserved. | <span class="geek-accent">极客，让智能更有趣</span></span>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.geek-footer {
  width: 100%;
  background: #181c24;
  border-top: 2px solid #00ffe7;
  padding: 1.2rem 0 0.7rem 0;
  margin-top: 3rem;
  text-align: center;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  color: #00ffe7;
  box-shadow: 0 -2px 24px 0 #00ffe733;
}
.footer-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  font-size: 1.08rem;
}
.footer-logo {
  font-size: 1.5rem;
  filter: drop-shadow(0 0 8px #00ffe7);
}
.footer-text {
  color: #00ffe7;
  letter-spacing: 1px;
}
.geek-accent {
  color: #00bfff;
}
@media (max-width: 600px) {
  .footer-content {
    flex-direction: column;
    gap: 0.3rem;
    font-size: 0.95rem;
  }
  .footer-logo {
    font-size: 1.1rem;
  }
}
</style> 