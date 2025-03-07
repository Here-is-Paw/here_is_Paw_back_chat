package com.ll.hereispaw.global.initData;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.member.service.MemberService;
import com.ll.hereispaw.domain.missing.Auhtor.entity.Author;
import com.ll.hereispaw.domain.missing.missing.entity.Missing;
import com.ll.hereispaw.domain.missing.missing.repository.MissingRepository;
import com.ll.hereispaw.domain.missing.missing.service.MissingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Profile("!prod")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitData {
    private final MemberService memberService;
    private final MissingService missingService;
    private final MissingRepository missingRepository;

    GeometryFactory geometryFactory = new GeometryFactory();

    @Bean
    @Order(3)
    public ApplicationRunner initDataNotProd() {
        return new ApplicationRunner() {

            @Transactional
            @Override
            public void run(ApplicationArguments args) {
                if (memberService.count() > 0) return;
                Member member1 = memberService.signup("user1", "1234", "유저1", "");
                Member member2 = memberService.signup("user2", "1234", "유저2", "");
                Member member3 = memberService.signup("user3", "1234", "유저3", "");

                Author author1 = missingService.of(member1);
                Author author2 = missingService.of(member2);
                Author author3 = missingService.of(member3);

                // 서울 강남역 좌표 (중심점)
                double centerLat = 37.498095;
                double centerLng = 127.027610;

                // 주변 위치들 (1km 내외 분포) - [위도, 경도, 위치설명]
                Object[][] locations = {
                        // 강남역 (중심점)
                        {centerLat, centerLng, "서울 강남구 강남역"},

                        // 300m 이내 위치들
                        {centerLat + 0.001, centerLng + 0.001, "서울 강남구 신논현역 방향"},  // 약 150m
                        {centerLat - 0.001, centerLng - 0.001, "서울 강남구 역삼역 방향"},    // 약 150m
                        {centerLat + 0.002, centerLng, "서울 강남구 논현동 방향"},           // 약 220m

                        // 500m 이내 위치들
                        {centerLat, centerLng + 0.004, "서울 강남구 신사동 방향"},           // 약 440m
                        {centerLat - 0.004, centerLng, "서울 강남구 역삼동 방향"},           // 약 440m

                        // 1km 이내 위치들
                        {centerLat + 0.006, centerLng + 0.006, "서울 강남구 압구정동 방향"}, // 약 950m
                        {centerLat - 0.006, centerLng - 0.006, "서울 강남구 대치동 방향"},   // 약 950m

                        // 2km 이상 떨어진 위치들
                        {centerLat + 0.015, centerLng + 0.015, "서울 강남구 청담동 방향"},   // 약 2.3km
                        {centerLat - 0.015, centerLng - 0.015, "서울 서초구 서초동 방향"}    // 약 2.3km
                };

                String[] names = {"초코", "바둑이", "뽀삐", "구름", "토리", "밤비", "라떼", "뭉치", "달이", "콩이"};
                String[] breeds = {"푸들", "말티즈", "포메라니안", "비숑", "닥스훈트", "치와와", "코카스파니엘", "슈나우저", "스피츠", "불독"};
                String[] colors = {"갈색", "흰색", "검정", "회색", "주황색", "베이지", "갈색", "흰색", "검정", "회색"};

                List<Missing> createdPosts = new ArrayList<>();

                for (int i = 0; i < locations.length; i++) {
                    Author assignedAuthor = (i % 3 == 0) ? author1 : (i % 3 == 1) ? author2 : author3;

                    // PostGIS POINT 형식으로 좌표 저장 (경도, 위도 순서)
                    Point geoPoint = geometryFactory.createPoint(
                            new Coordinate(
                                    (Double)locations[i][1],  // 경도
                                    (Double)locations[i][0]   // 위도
                            )
                    );

                    // 엔티티 직접 생성하여 저장 (DTO와 생성자 문제 회피)
                    Missing missing = Missing.builder()
                            .name(names[i % names.length])
                            .breed(breeds[i % breeds.length])
                            .geo(geoPoint)
                            .location(locations[i][2].toString())
                            .color(colors[i % colors.length])
                            .serialNumber("등록번호" + (i + 1))
                            .gender(0)
                            .neutered(0)
                            .age(3 + i)
                            .lostDate(Timestamp.from(Instant.now().minusSeconds(86400 * i)).toLocalDateTime())
                            .etc("특징: 활발함, 위치: " + locations[i][2])
                            .reward(i % 2 == 0 ? 100000 : 50000)
                            .missingState(1)
                            .pathUrl("https://example.com/photo" + (i + 1))
                            .author(assignedAuthor)
                            .build();

                    Missing savedMissing = missingRepository.save(missing);
                    createdPosts.add(savedMissing);

                    log.info("✅ 실종 등록 완료: {} ({} - {})", missing.getName(), missing.getBreed(), locations[i][2]);
                    log.info("📍 좌표: {}", geoPoint);
                }

                log.info("📋 총 {}개의 실종 게시글 생성 완료", createdPosts.size());
            }
        };
    }
}