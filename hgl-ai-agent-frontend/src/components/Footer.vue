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
  background: #fff;
  border-top: 2px solid #00eaff;
  padding: 1.2rem 0 0.7rem 0;
  margin-top: 1rem;
  text-align: center;
  font-family: var(--font-main);
  color: #2563eb;
  box-shadow: 0 -2px 24px 0 #00eaff22;
  display: flex;
  justify-content: center;
  align-items: center;
}
.footer-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  font-size: 1.08rem;
  flex-wrap: wrap;
}
.footer-logo {
  font-size: 1.5rem;
  color: #00eaff;
  filter: drop-shadow(0 0 8px #00eaff);
}
.footer-text {
  color: #2563eb;
  letter-spacing: 1px;
}
.geek-accent {
  color: #00eaff;
}
@media (max-width: 900px) {
  .footer-content {
    font-size: 0.98rem;
    gap: 0.6rem;
  }
}
@media (max-width: 600px) {
  .footer-content {
    flex-direction: column;
    gap: 0.3rem;
    font-size: 0.92rem;
  }
  .footer-logo {
    font-size: 1.1rem;
  }
}
</style> 