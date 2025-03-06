package com.ll.hereispaw.global.initData;

import com.ll.hereispaw.domain.find.find.entity.FindPost;
import com.ll.hereispaw.domain.find.find.entity.Photo;
import com.ll.hereispaw.domain.find.find.repository.FindPhotoRepository;
import com.ll.hereispaw.domain.find.find.repository.FindRepository;
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
import org.springframework.data.geo.Point;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Profile("!prod")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitData {
    private final MemberService memberService;
    private final MissingService missingService;
    private final MissingRepository missingRepository;
    private final FindRepository findRepository;
    private final FindPhotoRepository findPhotoRepository;
//    private final PostDocumentRepository postDocumentRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Bean
    @Order(3)
    public ApplicationRunner initDataNotProd() {
        return new ApplicationRunner() {

            @Transactional
            @Override
            public void run (ApplicationArguments args) {
//                postDocumentRepository.clear("post");

                if (memberService.count() > 0)  return;
                Member member1 = memberService.signup("user1", "1234", "유저1", "");
                Member member2 = memberService.signup("user2", "1234", "유저2", "");
                Member member3 = memberService.signup("user3", "1234", "유저3", "");

                Author author1 = missingService.of(member1);
                Author author2 = missingService.of(member2);
                Author author3 = missingService.of(member3);

                // 🟢 실종 동물 데이터 추가
                String[] names = {"초코", "바둑이", "뽀삐", "구름", "토리", "밤비", "라떼", "뭉치", "달이", "콩이"};
                String[] breeds = {"푸들", "말티즈", "포메라니안", "비숑", "닥스훈트", "치와와", "코카스파니엘", "슈나우저", "스피츠", "불독"};
                String[] locations = {"서울 강남구", "부산 해운대구", "대구 중구", "인천 연수구", "광주 동구",
                        "대전 서구", "울산 남구", "수원 장안구", "제주 서귀포시", "청주 상당구"};
                String[] colors = {"갈색", "흰색", "검정", "회색", "주황색", "베이지", "갈색", "흰색", "검정", "회색"};
                String[] imageUrls = {
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg"
                };

                for (int i = 0; i < 10; i++) {
                    Author assignedAuthor = (i % 3 == 0) ? author1 : (i % 3 == 1) ? author2 : author3;

                    // 좌표를 Point 객체로 변환
                    double lat = 37.5 + (i * 0.01);
                    double lng = 127.0 + (i * 0.01);
                    org.locationtech.jts.geom.Point geoPoint = geometryFactory.createPoint(new Coordinate(lng, lat));

                    // Missing 객체 직접 생성 및 저장
                    Missing missing = Missing.builder()
                            .name(names[i])
                            .breed(breeds[i])
                            .geo(geoPoint)
                            .location(locations[i])
                            .color(colors[i])
                            .serialNumber("등록번호" + (i + 1))
                            .gender((i % 2 == 0) ? 1 : 0)
                            .neutered((i % 2 == 0) ? 1 : 0)
                            .age(3 + i)
                            .lostDate(LocalDateTime.now().minusDays(i))
                            .etc("특징: 활발함")
                            .reward((i % 2 == 0) ? 100000 : 50000)
                            .missingState(1)
                            .pathUrl(imageUrls[i])
                            .author(assignedAuthor)
                            .build();

                    missingRepository.save(missing);

//                    PostDocument postDoc = new PostDocument(missing);
//                    postDocumentRepository.save(postDoc, IndexName.POST.getIndexName());
                    log.info("✅ 실종 등록 완료: {} ({} - {})", missing.getName(), missing.getBreed(), missing.getLocation());
                }

                // 더미 데이터 정보
                String[] titles = {
                        "골든리트리버 발견했어요",
                        "작은 말티즈 발견",
                        "갈색 푸들 발견! 주인 찾습니다",
                        "하얀색 비숑 발견했어요",
                        "공원에서 시베리안 허스키 발견"
                };

                String[] situations = {
                        "어제 저녁 산책하다가 공원에서 배회하는 것을 발견했습니다. 목줄이 없었고 다가가면 경계하는 모습이었어요.",
                        "오늘 아침 아파트 단지에서 혼자 돌아다니는 것을 봤어요. 경비실에 맡겨두었습니다.",
                        "3일 전 동네 마트 앞에서 발견했어요. 비가 많이 와서 일단 데려왔습니다. 주인을 찾는 중입니다.",
                        "어제 대로변에서 차량을 피해 달리는 걸 발견했습니다. 다행히 안전하게 포획했고 현재 임시보호 중입니다.",
                        "오늘 오후 지하철역 근처에서 목줄 없이 돌아다니는 걸 발견했어요. 접근하면 친근하게 반응합니다."
                };

                String[] findBreeds = {"골든리트리버", "말티즈", "토이푸들", "비숑프리제", "시베리안 허스키"};

                Point[] geoPoints = {
                        new Point(37.5665, 126.9780), // 서울 중심부
                        new Point(37.5209, 127.1230), // 서울 강동구
                        new Point(37.4989, 127.0299), // 서울 강남구
                        new Point(37.6511, 127.0481), // 서울 노원구
                        new Point(37.5665, 126.8001)  // 서울 강서구
                };

                String[] findLocations = {
                        "서울시 중구 명동 인근 공원",
                        "서울시 강동구 길동 아파트단지",
                        "서울시 강남구 역삼동 마트 앞",
                        "서울시 노원구 상계동 대로변",
                        "용산시 강서구 화곡동 지하철역 근처"
                };

                String[] findNames = {"모름", "모름", "모름", "모름", "모름"};

                String[] findColors = {"황금색", "하얀색", "갈색", "하얀색", "회색/흰색"};

                String[] genders = {"수컷", "암컷", "수컷", "암컷", "수컷"};

                String[] etcs = {
                        "체격이 크고 온순한 성격, 목줄 흔적 있음",
                        "발톱이 길고 귀 부분에 갈색 얼룩이 있음",
                        "꼬리가 짧고 왼쪽 앞발에 약간의 절음이 있음",
                        "얼굴 주변 털이 노랗게 변색되어 있음, 목에 작은 방울 달린 목걸이 착용",
                        "파란 눈동자가 특징적이고 왼쪽 귀에 작은 흠집 있음"
                };

                int[] ages = {3, 2, 4, 1, 5};

                int[] neutered = {1, 0, 1, 0, 1};

                String[] findImageUrls = {
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg",
                        "https://paw-bucket-1.s3.ap-northeast-2.amazonaws.com/profile-img/defaultAvatar.jpg"
                };

                Long[] memberIds = {
                        member1.getId(),
                        member2.getId(),
                        member3.getId(),
                        member1.getId(),
                        member2.getId()
                };

                // 데이터 생성 및 저장
                for (int i = 0; i < 5; i++) {
                    // FindPost 생성
                    FindPost findPost = new FindPost();
                    findPost.setTitle(titles[i]);
                    findPost.setSituation(situations[i]);
                    findPost.setBreed(findBreeds[i]);
                    findPost.setGeo(geoPoints[i]);
                    findPost.setLocation(findLocations[i]);
                    findPost.setName(findNames[i]);
                    findPost.setColor(findColors[i]);
                    findPost.setGender(genders[i].equals("수컷") ? 0 : 1);
                    findPost.setEtc(etcs[i]);
                    findPost.setAge(ages[i]);
                    findPost.setState(0); // 0: 발견, 1: 보호, 2: 완료
                    findPost.setNeutered(neutered[i]);
                    findPost.setFind_date(LocalDateTime.now().minusDays(i));
                    findPost.setMember_id(memberIds[i]);
                    findPost.setShelter_id(null); // 보호소 ID는 null로 설정

                    FindPost savedPost = findRepository.save(findPost);

//                    PostDocument postDoc = new PostDocument(savedPost);
//                    postDocumentRepository.save(postDoc, IndexName.POST.getIndexName());

                    // Photo 생성
                    Photo photo = new Photo();
                    photo.setPath_url(findImageUrls[i]);
                    photo.setMember_id(memberIds[i]);
                    photo.setPostId(savedPost.getId());

                    findPhotoRepository.save(photo);

                    log.info("✅ 발견 등록 완료: {} ({} - {})", savedPost.getTitle(), savedPost.getBreed(), savedPost.getLocation());
                }
            }
        };
    }
}