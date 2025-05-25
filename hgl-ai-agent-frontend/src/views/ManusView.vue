<template>
  <div class="chat-panel">
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(message, index) in messages" :key="index"
        :class="['message', message.type === 'user' ? 'user-message' : 'ai-message']">
        <div class="message-row">
          <div v-if="message.type === 'ai'" class="ai-avatar geek-avatar">🤖</div>
          <div class="message-content geek-bubble" :class="message.type">
            {{ message.content }}
          </div>
        </div>
      </div>
    </div>
    <div class="input-row">
      <input v-model="userInput" class="geek-input" :placeholder="isLoading ? 'AI思考中...' : '输入你的问题...'" @keyup.enter="sendMessage" :disabled="isLoading" />
      <button class="geek-btn" @click="sendMessage" :disabled="isLoading || !userInput.trim()">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { useHead } from '@vueuse/head'
const messages = ref([])
const userInput = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)
let chatId = ref(Date.now())
let currentEventSource = null

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!userInput.value.trim() || isLoading.value) return
  const message = userInput.value
  messages.value.push({ type: 'user', content: message })
  userInput.value = ''
  isLoading.value = true
  scrollToBottom()
  messages.value.push({ type: 'ai', content: '' })
  scrollToBottom()
  try {
    currentEventSource = new EventSource(`http://localhost:8223/api/ai/manus/chat?message=${encodeURIComponent(message)}&chatId=${chatId.value}`)
    currentEventSource.onmessage = (event) => {
      const { content, isEnd } = JSON.parse(event.data)
      messages.value[messages.value.length - 1].content += content
      scrollToBottom()
      if (isEnd) {
        isLoading.value = false
        currentEventSource.close()
      }
    }
    currentEventSource.onerror = () => {
      isLoading.value = false
      currentEventSource.close()
      messages.value.pop()
      messages.value.push({ type: 'ai', content: '抱歉，发生了错误，请稍后重试。' })
      scrollToBottom()
    }
  } catch (e) {
    isLoading.value = false
    messages.value.pop()
    messages.value.push({ type: 'ai', content: '抱歉，发生了错误，请稍后重试。' })
    scrollToBottom()
  }
}
onMounted(scrollToBottom)

useHead({
  title: 'AI超级智能体 - 极客风格AI对话',
  meta: [
    { name: 'description', content: '与AI超级智能体极客风格对话，获取智能解答，体验酷炫AI。' },
    { name: 'keywords', content: 'AI, 超级智能体, 极客, 智能对话, 酷炫, HGL' },
    { property: 'og:title', content: 'AI超级智能体 - 极客风格AI对话' },
    { property: 'og:description', content: '极客风格，酷炫体验，AI超级智能体为你解答各种问题。' },
    { property: 'og:type', content: 'website' }
  ]
})
</script>

<style scoped>
.chat-panel {
  max-width: 700px;
  margin: 2.5rem auto 0 auto;
  background: var(--geek-panel);
  border-radius: var(--geek-radius);
  box-shadow: var(--geek-shadow);
  border: 1.5px solid var(--geek-border);
  padding: 2rem 1rem 1rem 1rem;
  min-height: 60vh;
  display: flex;
  flex-direction: column;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 1.2rem;
  padding-right: 0.5rem;
}
.message {
  margin-bottom: 0.7rem;
}
.message-row {
  display: flex;
  align-items: flex-start;
}
.ai-avatar {
  margin-right: 0.7rem;
}
.user-message .message-row {
  flex-direction: row-reverse;
}
.user-message .geek-avatar {
  margin-left: 0.7rem;
  margin-right: 0;
}
.user-message .message-content {
  margin-left: 0;
  margin-right: 0.7rem;
}
.ai-message .message-content {
  margin-right: 0;
  margin-left: 0.7rem;
}
.input-row {
  display: flex;
  gap: 0.7rem;
  margin-top: 0.5rem;
}
@media (max-width: 700px) {
  .chat-panel {
    padding: 1rem 0.2rem 0.7rem 0.2rem;
  }
}
</style> 