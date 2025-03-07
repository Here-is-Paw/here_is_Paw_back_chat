package com.ll.hereispaw.domain.search.search.eventListener;

import com.ll.hereispaw.domain.search.search.document.IndexName;
import com.ll.hereispaw.domain.search.search.document.PostDocument;
import com.ll.hereispaw.domain.search.search.dto.request.PostEventDto;
import com.ll.hereispaw.domain.search.search.service.PostDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class PostEventListener {

    private final PostDocumentService postDocumentService;

    @KafkaListener(
            topics = "meiliesearch",
            groupId = "search",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void createDocuments(PostEventDto postEventDto){
        log.debug("PostEventDto: {}", postEventDto);

        postDocumentService.add(new PostDocument(postEventDto), IndexName.POST.getIndexName());
    }
}
