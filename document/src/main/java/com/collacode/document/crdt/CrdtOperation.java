package com.collacode.document.crdt;

import com.collacode.document.enums.OperationType;

import java.time.Instant;
import java.util.Comparator;

public record CrdtOperation(
        OperationType type, // INSERT, DELETE, UPDATE
        String authorId, // Идентификатор клиента
        Long sequenceNumber, // Последовательный номер операции для этого клиента
        VectorClock dependencies, // Вектор часов на момент создания операции
        int position, // Позиция в документе
        String text, // Текст для вставки/замены
        Instant timestamp// Временная метка
){
    public static Comparator<CrdtOperation> operationsComparator() {
        return (a, b) -> a.dependencies.isBefore(b.dependencies) ? -1 :
                b.dependencies.isBefore(a.dependencies) ? 1 : 0;
    }
}
