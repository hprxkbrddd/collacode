package com.collacode.document.crdt;

import com.collacode.document.enums.OperationType;
import com.collacode.document.rga.RGA;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class CrdtEngine {

    public void applyOperation(RGA<Character> document, CrdtOperation op) {
        switch (op.type()) {
            case INSERT -> document.insert(op.value(), op.anchorId(), op.vectorClock());
            case DELETE -> document.remove(op.anchorId(), op.vectorClock());
        }
        ;
    }

    public List<CrdtOperation> transformOperations(List<CrdtOperation> ops, CrdtOperation newOp) {
        List<CrdtOperation> transformed = new ArrayList<>();
        for (CrdtOperation op : ops) {
            if (op.isConcurrentWith(newOp))
                transformed.add(transformConcurrent(op, newOp));
            else
                transformed.add(newOp);
        }
        return transformed;
    }

    public CrdtOperation transformConcurrent(CrdtOperation op, CrdtOperation newOp) {
        boolean sameAnchor = op.anchorId().equals(newOp.anchorId());
        if (op.type() == OperationType.INSERT && newOp.type() == OperationType.INSERT
                && sameAnchor) {
            if (op.vectorClock().isBefore(newOp.vectorClock())) {
                return op;
            } else {
                return new CrdtOperation(
                        op.type(),
                        op.authorId(),
                        op.sequenceNumber(),
                        op.vectorClock(),
                        newOp.anchorId(),
                        op.value(),
                        op.timestamp()
                );
            }
        }

        if ((op.type() == OperationType.DELETE && newOp.type() == OperationType.INSERT &&
                sameAnchor) ||
                (op.type() == OperationType.INSERT && newOp.type() == OperationType.DELETE &&
                        sameAnchor)) {

            boolean deleteIsFirst = (op.type() == OperationType.DELETE) ?
                    op.vectorClock().isBefore(newOp.vectorClock()) :
                    newOp.vectorClock().isBefore(op.vectorClock());

            if (deleteIsFirst) {
                // Если удаление было раньше - вставка отменяется (возвращаем удаление)
                return op.type() == OperationType.DELETE ? op : newOp;
            } else {
                // Если вставка была раньше - удаление должно быть применено к новому элементу
                // Возвращаем модифицированное удаление с новым anchorId
                String newAnchor = op.type() == OperationType.INSERT ?
                        op.anchorId() : newOp.anchorId();
                String authorId;
                long sequenceNumber;
                VectorClock vectorClock;
                Instant timestamp;

                if (op.type() == OperationType.DELETE) {
                    authorId = op.authorId();
                    sequenceNumber = op.sequenceNumber();
                    vectorClock = op.vectorClock();
                    timestamp = op.timestamp();
                } else {
                    authorId = newOp.authorId();
                    sequenceNumber = newOp.sequenceNumber();
                    vectorClock = newOp.vectorClock();
                    timestamp = newOp.timestamp();
                }

                return new CrdtOperation(
                        OperationType.DELETE,
                        authorId,
                        sequenceNumber,
                        vectorClock,
                        newAnchor,
                        null,
                        timestamp
                );
            }
        }

        // 5. Для остальных случаев возвращаем существующую операцию без изменений
        return op;
    }
}
