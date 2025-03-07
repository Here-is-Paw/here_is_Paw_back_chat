package com.ll.hereispaw.domain.search.search.repository;

import com.ll.hereispaw.domain.search.search.document.PostDocument;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.Searchable;


public interface PostDocumentRepository {

    void save(PostDocument postDoc, String indexName);

    void clear(String indexName);

    Searchable search(String indexName, SearchRequest searchRequest);
}
