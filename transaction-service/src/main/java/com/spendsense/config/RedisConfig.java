package com.spendsense.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@Slf4j
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public String testRedisConnection(
            StringRedisTemplate stringRedisTemplate) {
        try {
            stringRedisTemplate.opsForValue()
                    .set("test:connection", "ok");
            String val = stringRedisTemplate
                    .opsForValue().get("test:connection");
            log.info("✅ Redis connection working — got: {}", val);
        } catch (Exception e) {
            log.error("❌ Redis connection FAILED: {}",
                    e.getMessage());
        }
        return "done";
    }
}