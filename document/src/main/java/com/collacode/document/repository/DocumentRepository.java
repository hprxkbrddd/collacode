package com.collacode.document.repository;

import com.collacode.document.crdt.CrdtDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends MongoRepository<CrdtDocument, String> {
}
