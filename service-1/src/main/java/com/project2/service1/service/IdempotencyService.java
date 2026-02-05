package com.project2.service1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
//    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate redisTemplate;

    @Value("${app.idempotency.ttl-hours:4872}")
    private long ttlHours;

    public boolean markIfFirstTime(String eventId){
        String key = "event:" + eventId;
        Boolean inserted = redisTemplate.opsForValue().
                setIfAbsent(key, "processed", Duration.ofHours(ttlHours));
        return Boolean.TRUE.equals(inserted);
    }
}
