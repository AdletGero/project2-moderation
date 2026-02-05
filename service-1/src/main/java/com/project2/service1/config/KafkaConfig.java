package com.project2.service1.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topic1(@Value("${app.kafka.topic-in}") String TopicIn) {
        return TopicBuilder.name(TopicIn).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic topic2(@Value("${app.kafka.topic-out}") String TopicOut) {
        return TopicBuilder.name(TopicOut).partitions(1).replicas(1).build();
    }
}
