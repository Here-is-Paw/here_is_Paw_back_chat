package com.ll.here_is_paw_back_chatting.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.ll.here_is_paw_back_chatting.domain.chatMessage.repository")
public class MongoDBConfig {
  // MongoDB 관련 추가 설정
}