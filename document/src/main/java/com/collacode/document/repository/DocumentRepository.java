package com.collacode.document.repository;

import com.collacode.document.crdt.CrdtDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<CrdtDocument, String> {

//    @Query("{ '_id': ?0, 'versionVector.?1': ?2 }")
//    Optional<CrdtDocument> findByIdAndClientVersion(String docId, String clientId, Long version);
//
//    @Query(value = "{ '_id': ?0 }", fields = "{ 'operations': { $slice: -?1 } }")
//    Optional<CrdtDocument> findLastOperations(String docId, int limit);
}
