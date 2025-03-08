package com.ll.here_is_paw_back_chatting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HereIsPawBackChattingApplication {

  public static void main(String[] args) {
    SpringApplication.run(HereIsPawBackChattingApplication.class, args);
  }

}
