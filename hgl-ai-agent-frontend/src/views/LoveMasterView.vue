<template>
  <div class="chat-panel geek-card">
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(message, index) in messages" :key="index"
        :class="['message', message.type === 'user' ? 'user-message' : 'ai-message']">
        <div class="message-row">
          <div v-if="message.type === 'ai'" class="ai-avatar geek-avatar geek-glow">🤖</div>
          <div class="message-content geek-bubble" :class="message.type">
            {{ message.content }}
          </div>
        </div>
      </div>
    </div>
    <div class="input-row">
      <input v-model="userInput" class="geek-input" :placeholder="isLoading ? 'AI思考中...' : '输入你的问题...'
        " @keyup.enter="sendMessage" :disabled="isLoading" />
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
    currentEventSource = new EventSource(`http://localhost:8223/api/ai/love_master_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${chatId.value}`)
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
  title: 'AI恋爱大师 - 极客白色主色调AI情感对话',
  meta: [
    { name: 'description', content: '与AI恋爱大师极客白色主色调对话，获取情感建议，体验酷炫智能。' },
    { name: 'keywords', content: 'AI, 恋爱, 情感, 极客, 智能对话, 白色, 酷炫, HGL' },
    { property: 'og:title', content: 'AI恋爱大师 - 极客白色主色调AI情感对话' },
    { property: 'og:description', content: '极客白色主色调，酷炫体验，AI恋爱大师为你解答情感问题。' },
    { property: 'og:type', content: 'website' }
  ]
})
</script>

<style scoped>
.chat-panel {
  max-width: 700px;
  margin: 2.5rem auto 0 auto;
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  padding: 2rem 1rem 1rem 1rem;
  box-sizing: border-box;
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
@media (max-width: 900px) {
  .chat-panel {
    max-width: 98vw;
    padding: 1.2rem 0.5rem 0.7rem 0.5rem;
  }
}
@media (max-width: 767px) {
  .chat-panel {
    max-width: 100vw;
    margin: 0.5rem 0 0 0;
    padding: 0.5rem 0.1rem 0.3rem 0.1rem;
    border-radius: 8px;
  }
  .chat-messages {
    margin-bottom: 0.5rem;
    padding-right: 0.1rem;
  }
  .input-row {
    gap: 0.3rem;
    margin-top: 0.2rem;
  }
}
</style>