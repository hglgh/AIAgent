package com.hgl.hglaiagent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author 请别把我整破防
 */
@Configuration
   public class RedisConfig {

       @Bean
       public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
           RedisTemplate<String, String> template = new RedisTemplate<>();
           template.setConnectionFactory(factory);

           // 设置键的序列化器，避免 null
           template.setKeySerializer(new StringRedisSerializer());
           template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

           template.setHashKeySerializer(new StringRedisSerializer());
           template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

           template.afterPropertiesSet();
           return template;
       }
   }
   