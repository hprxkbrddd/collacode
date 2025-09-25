package com.collacode.document.crdt;

import com.collacode.document.dto.ParticipantDTO;
import com.collacode.document.enums.Role;
import com.collacode.document.rga.RGA;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@Document
@NoArgsConstructor
public class CrdtDocument {
    @Id
    private String id; // Идентификатор документа
    private String title;
    private RGA<Character> content;
    private HashSet<ParticipantDTO> participants;
    private final

    @Version
    private Long version; // Оптимистичная блокировка

    private VectorClock versionVector; // Вектор часов для клиентов

    private List<CrdtOperation> operations;

    public CrdtDocument(String title, String ownerId) {

        this.title = title;
        this.versionVector = new VectorClock();
        this.content = new RGA<>(this.versionVector);

        this.operations = Collections.emptyList();
    }

    public String stringContent(){
        StringBuilder stringContent = new StringBuilder();
        for (Character symbol : content.toList()){
            stringContent.append(symbol);
        }
        return stringContent.toString();
    }
}
