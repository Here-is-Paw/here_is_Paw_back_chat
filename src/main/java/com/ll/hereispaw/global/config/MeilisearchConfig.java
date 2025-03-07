package com.ll.hereispaw.global.config;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.TypoTolerance;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Slf4j
@Configuration
public class MeilisearchConfig {

    @Bean
    public Client meilisearchClient() {
        return new Client(new Config("http://localhost:7700", "masterKey"));
    }

    @PostConstruct
    private void settingMeilisearch() {
        Client client = new Client(new Config("http://localhost:7700", "masterKey"));

        try {
            Index index;
            try {
                index = client.index("post");
                index.getStats(); // 인덱스 존재 여부 확인
            } catch (MeilisearchException e) {
                log.info("post 인덱스가 존재하지 않아 새로 생성합니다.");
                client.createIndex("post");
                index = client.index("post");
            }

            HashMap<String, Integer> typoSettings = new HashMap<>();
            typoSettings.put("oneTypo", 40);
            typoSettings.put("twoTypos", 50);

            TypoTolerance typoTolerance = new TypoTolerance().setMinWordSizeForTypos(typoSettings);
            Settings settings = new Settings().setTypoTolerance(typoTolerance);

//            settings.setRankingRules(new String[]{"words", "typo", "proximity", "attribute", "sort", "exactness"});

            index.updateSettings(settings);
            log.debug("📌 메일리서치 세팅 적용 완료");

        } catch (Exception e) {
            log.error("❌ 메일리서치 설정 적용 중 오류 발생", e);
        }
    }
}