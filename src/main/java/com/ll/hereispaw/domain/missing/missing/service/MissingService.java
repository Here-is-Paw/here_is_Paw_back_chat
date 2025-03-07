package com.ll.hereispaw.domain.missing.missing.service;

import com.ll.hereispaw.domain.find.find.dto.DogFaceRequestDto;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.missing.Auhtor.entity.Author;
import com.ll.hereispaw.domain.missing.Auhtor.repository.AuthorRepository;
import com.ll.hereispaw.domain.missing.missing.dto.request.MissingRequestDTO;
import com.ll.hereispaw.domain.missing.missing.dto.response.CreatePostEventDto;
import com.ll.hereispaw.domain.missing.missing.dto.response.MissingResponseDto;
import com.ll.hereispaw.domain.missing.missing.entity.Missing;
import com.ll.hereispaw.domain.missing.missing.repository.MissingRepository;
import com.ll.hereispaw.domain.search.search.document.PostState;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.exception.CustomException;
import com.ll.hereispaw.standard.Ut.GeoUt;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Tag(name = " 실종 신고 API", description = "Missing")
public class MissingService {
    private static String POST_TYPE = "missing";

    @Value("${custom.bucket.name}")
    private String bucketName;

    @Value("${custom.bucket.region}")
    private String region;

    @Value("${custom.bucket.missing}")
    private String dirName;

    private final S3Client s3Client;

    private final MissingRepository missingRepository;
    private final AuthorRepository authorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public MissingResponseDto write(Author author, MissingRequestDTO missingRequestDto) {

//        log.debug("author : {}", missingRequestDto.getAuthor());

        authorRepository.save(author);

        String pathUrl = s3Upload(missingRequestDto.getFile());

        System.out.println("geo: " + missingRequestDto.getGeo());


        Missing missing = missingRepository.save(
                Missing.builder()
                        .name(missingRequestDto.getName())
                        .breed(missingRequestDto.getBreed())
                        .geo(GeoUt.wktToPoint(missingRequestDto.getGeo()))
                        .location(missingRequestDto.getLocation())
                        .color(missingRequestDto.getColor())
                        .serialNumber(missingRequestDto.getSerialNumber())
                        .gender(missingRequestDto.getGender())
                        .neutered(missingRequestDto.getNeutered())
                        .age(missingRequestDto.getAge())
                        .lostDate(missingRequestDto.getLostDate())
                        .etc(missingRequestDto.getEtc())
                        .reward(missingRequestDto.getReward())
                        .missingState(missingRequestDto.getMissingState())
                        .pathUrl(pathUrl)
                        .author(author)
                        .build()
        );

        //카프카 메시지 발행
        DogFaceRequestDto dogFaceRequestDto = DogFaceRequestDto.builder()
                .type("save")
                .image(missing.getPathUrl())
                .postType(POST_TYPE)
                .postId(missing.getId())
                .postMemberId(missing.getAuthor().getId())
                .build();
        kafkaTemplate.send("dog-face-request", dogFaceRequestDto);

        kafkaTemplate.send("meiliesearch", new CreatePostEventDto(missing, PostState.CREATE.getCode()));

//        s3Upload(missing, file);
        return new MissingResponseDto(missing);
    }

    public Author of(Member member) {
        return entityManager.getReference(Author.class, member.getId());
    }

    public List<MissingResponseDto> list() {
        List<Missing> missings = missingRepository.findAll();
        List<MissingResponseDto> missingResponseDtos = new ArrayList<>();

        for (Missing missing : missings) {

            Author author = authorRepository.findById(missing.getAuthor().getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

            missingResponseDtos.add(
                    new MissingResponseDto(missing)
            );
        }

        return missingResponseDtos;
    }

    public MissingResponseDto findById(Long missingId) {
        Missing missing = missingRepository.findById(missingId)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        return new MissingResponseDto(missing);
    }

    @Transactional
    public MissingResponseDto update(
            Author author,
            MissingRequestDTO missingRequestDTO,
            Long missingId) {

        Missing missing = missingRepository.findById(missingId).orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        if (!author.getId().equals(missing.getId())) {
            throw new CustomException(ErrorCode.METHOD_NOT_ALLOWED);
        }

        String pathUrl = s3Upload(missingRequestDTO.getFile());

        missing.setName(missingRequestDTO.getName());
        missing.setBreed(missingRequestDTO.getBreed());
        missing.setGeo(GeoUt.wktToPoint(missingRequestDTO.getGeo()));
        missing.setLocation(missingRequestDTO.getLocation());
        missing.setColor(missingRequestDTO.getColor());
        missing.setSerialNumber(missingRequestDTO.getSerialNumber());
        missing.setGender(missingRequestDTO.getGender());
        missing.setNeutered(missingRequestDTO.getNeutered());
        missing.setAge(missingRequestDTO.getAge());
        missing.setLostDate(missingRequestDTO.getLostDate());
        missing.setEtc(missingRequestDTO.getEtc());
        missing.setMissingState(missingRequestDTO.getMissingState());
        missing.setReward(missingRequestDTO.getReward());
        missing.setPathUrl(pathUrl);

        missingRepository.save(missing);

        return new MissingResponseDto(missing);
    }

    @Transactional
    public String delete(Author author, Long missingId) {
        Missing missing = missingRepository.findById(missingId).orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        if (!author.getId().equals(missing.getId())) {
            throw new CustomException(ErrorCode.METHOD_NOT_ALLOWED);
        }

        s3Delete(missing.getPathUrl());
        missingRepository.delete(missing);

        return "신고글 삭제";
    }

    // s3 매서드
    public String s3Upload(
            MultipartFile file) {

        String filename = getUuidFilename(file);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(dirName + "/" + filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            //return getS3FileUrl(filename)
//            missing.setPathUrl(getS3FileUrl(filename));

        } catch (IOException e) {
            return new CustomException(ErrorCode.S3_UPLOAD_ERROR).toString();
        }

        return getS3FileUrl(filename);
    }

    public void s3Delete(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(dirName + "/" + fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
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

