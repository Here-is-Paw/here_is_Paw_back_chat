package com.ll.hereispaw.domain.search.search.global.config;

import com.ll.hereispaw.domain.search.search.dto.request.PostEventDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Bean
    public ConsumerFactory<String, PostEventDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<PostEventDto> jsonDeserializer = new JsonDeserializer<>(PostEventDto.class);
        jsonDeserializer.addTrustedPackages("com.ll.hereispaw.domain.search.search.dto");

        // 헤더에 상대가 보낸 클래스값이 자동 포함됨. ex. A객체(id, username, nickname)
        // typeHeaders(ture)의 경우 이 클래스 값을 받아 자동으로 매핑함.
        // 하지만 컨슈머 쪽에서 사용하는 객체가 B객체(id, nickname)이라면 타입 오류 발생
        // 이를 방지하기 위해 false 설정 > json으로 넘어온 속성 중 동일한 속성(id, nickname) 자동 주입
        jsonDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostEventDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PostEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
