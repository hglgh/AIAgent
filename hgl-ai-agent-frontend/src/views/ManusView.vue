<template>
  <div class="chat-container">
    <div class="chat-header">
      <h1>AI 超级智能体</h1>
      <router-link to="/" class="back-button">返回首页</router-link>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(message, index) in messages" :key="index" 
           :class="['message', message.type === 'user' ? 'user-message' : 'ai-message']">
        <div class="message-content">
          {{ message.content }}
        </div>
      </div>
    </div>

    <div class="chat-input">
      <div class="input-wrapper">
        <input 
          v-model="userInput" 
          @keyup.enter="sendMessage"
          placeholder="输入你的问题..."
          :disabled="isLoading"
        />
        <button @click="sendMessage" :disabled="isLoading || !userInput.trim()">
          {{ isLoading ? '发送中...' : '发送' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import '../assets/chat.css'

const messages = ref([])
const userInput = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)
const chatId = ref('')
let currentEventSource = null
let hasReceivedData = false

// 生成随机的聊天室ID
const generateChatId = () => {
  return 'chat_' + Math.random().toString(36).substr(2, 9)
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 关闭当前的 EventSource 连接
const closeCurrentConnection = () => {
  if (currentEventSource) {
    currentEventSource.close()
    currentEventSource = null
  }
  isLoading.value = false
}

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || isLoading.value) return

  // 关闭之前的连接（如果存在）
  closeCurrentConnection()

  const message = userInput.value
  userInput.value = ''
  messages.value.push({ type: 'user', content: message })
  await scrollToBottom()

  isLoading.value = true
  hasReceivedData = false

  try {
    console.log('开始发送请求...')
    
    // 添加一个空的 AI 消息
    messages.value.push({ type: 'ai', content: '' })
    
    // 创建新的 EventSource 连接
    currentEventSource = new EventSource(`http://localhost:8223/api/ai/manus/chat?message=${encodeURIComponent(message)}&chatId=${chatId.value}`)
    
    currentEventSource.onmessage = (event) => {
      console.log('收到消息:', event.data)
      hasReceivedData = true
      const content = event.data
      messages.value[messages.value.length - 1].content += content
      scrollToBottom()
    }

    currentEventSource.onerror = (error) => {
      console.log('SSE 连接状态:', currentEventSource.readyState)
      // 只有在没有收到任何数据的情况下才显示错误消息
      if (!hasReceivedData) {
        console.error('SSE 错误:', error)
        closeCurrentConnection()
        messages.value.pop() // 移除空的 AI 消息
        messages.value.push({ type: 'ai', content: '抱歉，发生了错误，请稍后重试。' })
      } else {
        closeCurrentConnection()
      }
    }

  } catch (error) {
    console.error('请求错误:', error)
    closeCurrentConnection()
    messages.value.pop() // 移除空的 AI 消息
    messages.value.push({ type: 'ai', content: '抱歉，发生了错误，请稍后重试。' })
  }
}

onMounted(() => {
  chatId.value = generateChatId()
  console.log('聊天室ID:', chatId.value)
})

// 组件卸载时关闭连接
onUnmounted(() => {
  closeCurrentConnection()
})
</script>

<style scoped>
.chat-container {
  max-width: 1000px;
  margin: 0 auto;
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 1rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
}

.chat-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%239C92AC' fill-opacity='0.05' fill-rule='evenodd'/%3E%3C/svg%3E"),
    linear-gradient(135deg, rgba(52, 152, 219, 0.05) 0%, rgba(41, 128, 185, 0.05) 100%);
  opacity: 0.8;
  z-index: 0;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.1);
  margin-bottom: 1.5rem;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: slideDown 0.5s ease-out;
}

.chat-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3498db, #2980b9);
  border-radius: 20px 20px 0 0;
}

.chat-header h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
  background: linear-gradient(45deg, #2c3e50, #3498db);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: -0.5px;
}

.back-button {
  text-decoration: none;
  color: #666;
  padding: 0.8rem 1.5rem;
  border-radius: 12px;
  background: rgba(52, 152, 219, 0.1);
  transition: all 0.3s ease;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
  letter-spacing: 0.3px;
}

.back-button:hover {
  background: rgba(52, 152, 219, 0.2);
  transform: translateX(-5px);
  color: #3498db;
}

.back-button::before {
  content: '←';
  font-size: 1.2rem;
  font-weight: 600;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.1);
}

.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(52, 152, 219, 0.3);
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(52, 152, 219, 0.5);
}

.message {
  max-width: 70%;
  padding: 1.2rem 1.5rem;
  border-radius: 20px;
  margin: 0.5rem 0;
  position: relative;
  animation: messageSlide 0.3s ease-out;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  font-size: 1.05rem;
  line-height: 1.6;
  letter-spacing: 0.2px;
}

.user-message {
  align-self: flex-end;
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border-bottom-right-radius: 4px;
  font-weight: 500;
}

.user-message::after {
  content: '';
  position: absolute;
  bottom: 0;
  right: -8px;
  width: 16px;
  height: 16px;
  background: linear-gradient(135deg, #2980b9, #2980b9);
  clip-path: polygon(0 0, 100% 100%, 0 100%);
}

.ai-message {
  align-self: flex-start;
  background: rgba(255, 255, 255, 0.95);
  color: #2c3e50;
  border-bottom-left-radius: 4px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  font-weight: 400;
}

.ai-message::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: -8px;
  width: 16px;
  height: 16px;
  background: rgba(255, 255, 255, 0.95);
  clip-path: polygon(0 100%, 100% 100%, 100% 0);
}

.message-content {
  line-height: 1.6;
  font-size: 1.05rem;
  white-space: pre-wrap;
  word-break: break-word;
  letter-spacing: 0.2px;
}

.chat-input {
  display: flex;
  gap: 1rem;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.1);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: slideUp 0.5s ease-out;
  margin-top: 1.5rem;
}

.chat-input::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3498db, #2980b9);
  border-radius: 20px 20px 0 0;
}

.input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 1rem;
}

input {
  flex: 1;
  padding: 1rem 1.5rem;
  border: 2px solid rgba(52, 152, 219, 0.2);
  border-radius: 16px;
  font-size: 1.05rem;
  background: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  color: #2c3e50;
  font-family: inherit;
  letter-spacing: 0.2px;
}

input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
  background: rgba(255, 255, 255, 1);
}

input::placeholder {
  color: #95a5a6;
  font-weight: 400;
}

button {
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  font-size: 1.05rem;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.2);
  letter-spacing: 0.3px;
  font-family: inherit;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.3);
  background: linear-gradient(135deg, #2980b9, #3498db);
}

button:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.2);
}

button:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

@keyframes messageSlide {
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

@media (max-width: 768px) {
  .chat-container {
    padding: 0.5rem;
  }

  .chat-header {
    padding: 1rem;
    margin-bottom: 1rem;
    border-radius: 16px;
  }

  .chat-header h1 {
    font-size: 1.5rem;
  }

  .chat-messages {
    padding: 1rem;
    gap: 1rem;
    border-radius: 16px;
  }

  .message {
    max-width: 85%;
    padding: 1rem;
    font-size: 1rem;
  }

  .message-content {
    font-size: 1rem;
  }

  .chat-input {
    padding: 1rem;
    border-radius: 16px;
    margin-top: 1rem;
  }

  input {
    padding: 0.8rem 1rem;
    font-size: 1rem;
    border-radius: 12px;
  }

  button {
    padding: 0.8rem 1.5rem;
    font-size: 1rem;
    border-radius: 12px;
  }
}
</style> 