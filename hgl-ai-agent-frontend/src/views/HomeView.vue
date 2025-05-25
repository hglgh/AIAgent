<template>
  <div class="home">
    <div class="hero">
      <div class="hero-content">
        <h1>AI 应用中心</h1>
        <p class="subtitle">探索智能对话的无限可能</p>
        <div class="hero-decoration">
          <div class="circle"></div>
          <div class="circle"></div>
          <div class="circle"></div>
        </div>
      </div>
    </div>
    <div class="app-grid">
      <router-link to="/love-master" class="app-card">
        <div class="card-content">
          <div class="card-icon">❤️</div>
          <h2>AI 恋爱大师</h2>
          <p>与 AI 恋爱大师进行对话，获取情感建议</p>
          <div class="card-footer">
            <span class="enter-button">开始对话 →</span>
          </div>
        </div>
        <div class="card-decoration">
          <div class="line"></div>
          <div class="line"></div>
          <div class="line"></div>
        </div>
      </router-link>
      <router-link to="/manus" class="app-card">
        <div class="card-content">
          <div class="card-icon">🤖</div>
          <h2>AI 超级智能体</h2>
          <p>与 AI 超级智能体进行对话，获取智能解答</p>
          <div class="card-footer">
            <span class="enter-button">开始对话 →</span>
          </div>
        </div>
        <div class="card-decoration">
          <div class="line"></div>
          <div class="line"></div>
          <div class="line"></div>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  position: relative;
  overflow: hidden;
}

.hero {
  text-align: center;
  margin-bottom: 4rem;
  padding: 4rem 0;
  position: relative;
  animation: fadeIn 1s ease-out;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(45deg, rgba(52, 152, 219, 0.1), rgba(41, 128, 185, 0.1));
  animation: float 6s infinite ease-in-out;
}

.circle:nth-child(1) {
  width: 300px;
  height: 300px;
  top: -150px;
  left: -150px;
  animation-delay: 0s;
}

.circle:nth-child(2) {
  width: 200px;
  height: 200px;
  top: 50%;
  right: -100px;
  animation-delay: -2s;
}

.circle:nth-child(3) {
  width: 150px;
  height: 150px;
  bottom: -75px;
  left: 50%;
  animation-delay: -4s;
}

.hero h1 {
  font-size: 3.5rem;
  color: #2c3e50;
  margin-bottom: 1rem;
  background: linear-gradient(45deg, #2c3e50, #3498db);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  animation: slideDown 1s ease-out;
}

.subtitle {
  font-size: 1.4rem;
  color: #666;
  opacity: 0;
  animation: slideUp 0.8s ease-out 0.3s forwards;
  margin-bottom: 2rem;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  padding: 1rem;
  position: relative;
  z-index: 1;
}

.app-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  padding: 2rem;
  text-decoration: none;
  color: inherit;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: fadeIn 0.5s ease-out;
}

.app-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.card-content {
  position: relative;
  z-index: 1;
}

.card-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.app-card:hover .card-decoration {
  opacity: 1;
}

.line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(52, 152, 219, 0.1), transparent);
  height: 1px;
  width: 100%;
  animation: lineMove 2s infinite linear;
}

.line:nth-child(1) {
  top: 20%;
  animation-delay: 0s;
}

.line:nth-child(2) {
  top: 50%;
  animation-delay: -0.5s;
}

.line:nth-child(3) {
  top: 80%;
  animation-delay: -1s;
}

.card-icon {
  font-size: 3.5rem;
  margin-bottom: 1.5rem;
  animation: bounce 2s infinite;
  display: inline-block;
}

.app-card h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
  font-size: 2rem;
  background: linear-gradient(45deg, #2c3e50, #3498db);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.app-card p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 2rem;
  font-size: 1.1rem;
}

.card-footer {
  text-align: right;
}

.enter-button {
  color: #3498db;
  font-weight: 600;
  transition: all 0.3s ease;
  font-size: 1.1rem;
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  background: rgba(52, 152, 219, 0.1);
}

.app-card:hover .enter-button {
  color: #2980b9;
  transform: translateX(5px);
  background: rgba(52, 152, 219, 0.2);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes lineMove {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 2.5rem;
  }
  
  .subtitle {
    font-size: 1.2rem;
  }
  
  .app-grid {
    grid-template-columns: 1fr;
  }
  
  .app-card {
    padding: 1.5rem;
  }
  
  .card-icon {
    font-size: 3rem;
  }
  
  .app-card h2 {
    font-size: 1.8rem;
  }
}
</style>