package com.collacode.document.service;

import com.collacode.document.crdt.CrdtDocument;
import com.collacode.document.crdt.CrdtEngine;
import com.collacode.document.crdt.CrdtOperation;
import com.collacode.document.crdt.VectorClock;
import com.collacode.document.repository.DocumentRepository;
import com.collacode.document.rga.RGA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollaborationService {
    private final DocumentRepository repository;
    private final CrdtEngine crdtEngine;

    public synchronized void applyOperation(String docId, CrdtOperation operation) {
        CrdtDocument doc = repository.findById(docId).orElseThrow();

        // Трансформируем новую операцию относительно существующих
        List<CrdtOperation> transformed = crdtEngine.transformOperations(
                doc.getOperations(),
                operation
        );

        // Применяем все операции
        RGA<Character> newContent = doc.getContent();
        for (CrdtOperation op : transformed) {
            crdtEngine.applyOperation(newContent, op);
        }

        // Обновляем документ
        doc.setContent(newContent);
        doc.getOperations().addAll(transformed);
        doc.getVersionVector().getClock().merge(
                operation.authorId(),
                operation.sequenceNumber(),
                Math::max
        );

        repository.save(doc);
    }

    public List<CrdtOperation> getOperationsSince(String docId, VectorClock clientVector) {
        // Возвращаем только те операции, которые новее чем у клиента
        return repository.findById(docId)
                .map(doc -> doc.getOperations().stream()
                        .filter(op -> isOperationNew(op, clientVector))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private boolean isOperationNew(CrdtOperation op, VectorClock clientVector) {
        return VectorClock.clockComparator(op.vectorClock(), clientVector);
    }
}
