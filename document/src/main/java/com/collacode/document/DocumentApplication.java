package com.collacode.document;

import com.collacode.document.crdt.CrdtDocument;
import com.collacode.document.repository.DocumentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DocumentApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentApplication.class, args);
	}

	@Bean
	CommandLineRunner runner (DocumentRepository repository,
							  MongoTemplate mongoTemplate){
		return args -> {
			Map<String, Long> vectorClock = new HashMap<>();
			vectorClock.put("client1", 1L);
			String content = "some content";
			CrdtDocument document = new CrdtDocument(
					"my document",
					content,
					vectorClock,
					Collections.emptyList()
			);

//			usingMongoTemplateAndQuery(repository, mongoTemplate, content, document);
		};
	}

	private static void usingMongoTemplateAndQuery(DocumentRepository repository, MongoTemplate mongoTemplate, String content, CrdtDocument document) {
		Query query = new Query();
		query.addCriteria(Criteria.where("content").is(content));

		List<CrdtDocument> res = mongoTemplate.find(query, CrdtDocument.class);

		if (res.size() > 1)
			throw new IllegalStateException("many docs with same content found");
		if (res.isEmpty()) {
			repository.insert(document);
			System.out.println("INSERTED DOC");
		}
		else System.out.println("ALREADY EXISTS");
	}
}
