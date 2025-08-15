package com.collacode.document.crdt;

import com.collacode.document.enums.OperationType;

import java.time.Instant;
import java.util.Comparator;

public record CrdtOperation(
        OperationType type, // INSERT, DELETE, UPDATE
        String authorId, // Идентификатор клиента
        Long sequenceNumber, // Последовательный номер операции для этого клиента
        VectorClock vectorClock, // Вектор часов на момент создания операции
        String anchorId, // Позиция в документе
        Character value, // Текст для вставки/замены
        Instant timestamp// Временная метка
){
    public static Comparator<CrdtOperation> operationsComparator() {
        return (a, b) -> a.vectorClock.isBefore(b.vectorClock) ? -1 :
                b.vectorClock.isBefore(a.vectorClock) ? 1 : 0;
    }
    /**
     * Проверяет, являются ли две операции конкурентными
     * (были созданы без знания друг о друге)
     */
    public boolean isConcurrentWith(CrdtOperation other) {
        return this.vectorClock().isConcurrent(other.vectorClock);
    }
}
