package com.ll.hereispaw.domain.find.find.service;

import com.ll.hereispaw.domain.find.find.dto.DogFaceRequestDto;
import com.ll.hereispaw.domain.find.find.dto.FindDto;
import com.ll.hereispaw.domain.find.find.entity.FindPost;
import com.ll.hereispaw.domain.find.find.entity.Photo;
import com.ll.hereispaw.domain.find.find.repository.FindPhotoRepository;
import com.ll.hereispaw.domain.find.find.repository.FindRepository;
import com.ll.hereispaw.domain.missing.missing.dto.response.CreatePostEventDto;
import com.ll.hereispaw.domain.search.search.document.PostState;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FindService {
    private static String POST_TYPE = "finding";

    private final FindRepository findRepository;
    private final FindPhotoRepository findPhotoRepository;

    @Value("${custom.bucket.name}")
    private String bucketName;

    @Value("${custom.bucket.region}")
    private String region;

    @Value("${custom.bucket.finding}")
    private String dirName;

    private final S3Client s3Client;

    //카프카 발행자 템플릿
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Long saveFind(
            String title,
            String situation,
            String breed,
            Point geo,
            String location,
            String name,
            String color,
            String etc,
            int gender,
            int age,
            int state,
            int neutered,
            LocalDateTime find_date,
            Long member_id,
            Long shelter_id,
            MultipartFile file
            ) {
        FindPost findPost = new FindPost();

        findPost.setTitle(title);
        findPost.setSituation(situation);
        findPost.setBreed(breed);
        findPost.setGeo(geo);
        findPost.setLocation(location);
        findPost.setName(name);
        findPost.setColor(color);
        findPost.setEtc(etc);
        findPost.setGender(gender);
        findPost.setAge(age);
        findPost.setState(state);
        findPost.setNeutered(neutered);
        findPost.setFind_date(find_date);
        findPost.setMember_id(member_id);
        findPost.setShelter_id(shelter_id);

        FindPost savedPost = findRepository.save(findPost);

        String file_path = s3Upload(savedPost, file);

        System.out.println("response check: " + file_path);

        //카프카 메시지 발행
        DogFaceRequestDto dogFaceRequestDto = DogFaceRequestDto.builder()
            .type("save")
            .image(file_path)
            .postType(POST_TYPE)
            .postId(savedPost.getId())
            .postMemberId(savedPost.getMember_id())
            .build();
        kafkaTemplate.send("dog-face-request", dogFaceRequestDto);


        kafkaTemplate.send("meiliesearch", new CreatePostEventDto(savedPost, PostState.CREATE.getCode()));

        return savedPost.getId(); // 저장된 find_post_id 반환
    }



    public FindDto findById(Long postId) {

        FindPost findPost = findRepository.findById(postId).get();

        Photo photo = findPhotoRepository.findByPostId(findPost.getId());
        String path_url = photo.getPath_url(); // 첫 번째 사진의 URL 사용

        FindDto findDto = FindDto.builder()
                .id(findPost.getId())
                .title(findPost.getTitle())
                .situation(findPost.getSituation())
                .age(findPost.getAge())
                .breed(findPost.getBreed())
                .color(findPost.getColor())
                .etc(findPost.getEtc())
                .find_date(findPost.getFind_date())
                .gender(findPost.getGender())
                .x(findPost.getGeo().getX())
                .y(findPost.getGeo().getY())
                .location(findPost.getLocation())
                .member_id(findPost.getMember_id())
                .name(findPost.getName())
                .neutered(findPost.getNeutered())
                .path_url(findPost.getPath_url())
                .member_id(findPost.getMember_id())
                .build();

        return findDto;
    }

    public String updateFind(
            Long postId,
            String title,
            String situation,
            String breed,
            Point geo,
            String location,
            String name,
            String color,
            String etc,
            int gender,
            int age,
            int state,
            int neutered,
            LocalDateTime find_date,
            Long member_id,
            Long shelter_id,
            MultipartFile file
    ) {
        FindPost findPost = findRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));

        findPost.setTitle(title);
        findPost.setSituation(situation);
        findPost.setBreed(breed);
        findPost.setGeo(geo);
        findPost.setLocation(location);
        findPost.setName(name);
        findPost.setColor(color);
        findPost.setEtc(etc);
        findPost.setGender(gender);
        findPost.setAge(age);
        findPost.setState(state);
        findPost.setNeutered(neutered);
        findPost.setFind_date(find_date);
        findPost.setMember_id(member_id);
        findPost.setShelter_id(shelter_id);

        FindPost savedPost = findRepository.save(findPost);

        String file_path = s3Upload(findPost, file);

        return "수정 완료";
    }

    // 발견 신고 삭제
    public void deleteFind(Long postId) {
        FindPost findPost = findRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));

        s3Delete(findPost);

        findRepository.delete(findPost);

    }

    public List<FindDto> findAll() {
        // 7주일 이전에 작성된 게시글 삭제
        findExpiredPosts();

        List<FindDto> findDtos = new ArrayList<>();

        findRepository.findAll().forEach(e -> {
            FindDto dto = FindDto.builder()
                    .id(e.getId())
                    .title(e.getTitle())
                    .situation(e.getSituation())
                    .breed(e.getBreed())
                    .x(e.getGeo() != null ? e.getGeo().getX() : null)
                    .y(e.getGeo() != null ? e.getGeo().getY() : null)
                    .location(e.getLocation())
                    .name(e.getName())
                    .color(e.getColor())
                    .gender(e.getGender())
                    .etc(e.getEtc())
                    .age(e.getAge())
                    .neutered(e.getNeutered())
                    .find_date(e.getFind_date())
                    .member_id(e.getMember_id())
                    .shelter_id(e.getShelter_id())
                    .path_url(e.getPath_url())
                    .build();

            findDtos.add(dto);
        });

        return findDtos;
    }

    // 일주일 이전에 작성된 게시글 삭제
    public void findExpiredPosts() {
        // 현재 날짜에서 7일 뺀 날짜
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // 일주일 전에 작성된 게시글을 가져와서 각각의 id를 deleteFind 메소드로 넘겨서 삭제
        findRepository.findByModifiedAtBefore(sevenDaysAgo).forEach(e -> {
            deleteFind(e.getId());
        });
    }

    // s3 매서드
    public String s3Upload(
            FindPost findPost,
            MultipartFile file) {
        try {
            String filename = getUuidFilename(file);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(dirName + "/" + filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            findPost.setPath_url(getS3FileUrl(filename));

        } catch (IOException e) {
            return "error: "+e;
        }

        return findPost.getPath_url();
    }

    public GlobalResponse<String> s3Delete(FindPost findPost) {
        try {
            String fileName = getFileNameFromS3Url(findPost.getPath_url());
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(dirName + "/" + fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            findPost.setPath_url(getS3FileUrl("defaultAvatar.jpg"));
        } catch (Exception e) {
            log.warn("Failed to delete old profile image", e);
        }
        return GlobalResponse.success("삭제 성공");
    }

    public GlobalResponse<String> s3Update(
            FindPost findPost,
            MultipartFile file) {

        if (findPost.getFind_date() != null) {
            s3Delete(findPost);
            s3Upload(findPost, file);
        }

        return GlobalResponse.success("수정 성공");
    }

    private String getUuidFilename(MultipartFile file) {
        // ContentType으로부터 확장자 추출
        String contentType = file.getContentType();
        String extension = switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> "jpg";  // 기본값 설정
        };

        // UUID 파일명 생성
        return UUID.randomUUID().toString() + "." + extension;
    }

    public String getS3FileUrl(String fileName) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + dirName + "/" + fileName;
    }

    public String getFileNameFromS3Url(String s3Url) {
        return s3Url.substring(s3Url.lastIndexOf('/') + 1);
    }


}
