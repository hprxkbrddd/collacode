package com.collacode.document.crdt;

import com.collacode.document.enums.OperationType;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class CrdtOperation {
    private OperationType type; // INSERT, DELETE, UPDATE

    private String authorId; // Идентификатор клиента

    private Long sequenceNumber; // Последовательный номер операции для этого клиента

    private Map<String, Long> dependencies; // Вектор часов на момент создания операции

    private int position; // Позиция в документе

    private String text; // Текст для вставки/замены

    private Instant timestamp; // Временная метка

    // Конструкторы, геттеры и сеттеры
}
