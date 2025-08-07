package com.collacode.document.crdt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Document
@NoArgsConstructor
public class CrdtDocument {
    @Id
    private String id; // Идентификатор документа

    private String title;

    private String content; // Текущее состояние документа (опционально, можно вычислять)

    @Version
    private Long version; // Оптимистичная блокировка

    private Map<String, Long> versionVector; // Вектор часов для клиентов

    private List<CrdtOperation> operations;

    public CrdtDocument(String title) {
        this.title = title;
        this.content = "";
        this.versionVector = Collections.emptyMap();
        this.operations = Collections.emptyList();
    }

    public CrdtDocument(String title, String content) {
        this.title = title;
        this.content = content;
        this.versionVector = Collections.emptyMap();
        this.operations = Collections.emptyList();
    }

    public CrdtDocument(String title,
                        String content,
                        Map<String, Long> versionVector,
                        List<CrdtOperation> operations) {
        this.title = title;
        this.content = content;
        this.versionVector = versionVector;
        this.operations = operations;
    }
}
