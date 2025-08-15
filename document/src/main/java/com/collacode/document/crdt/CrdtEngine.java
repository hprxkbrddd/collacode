package com.collacode.document.crdt;

import java.util.List;

public class CrdtEngine {

    public String applyOperation(String current, CrdtOperation op) {
        int pos = Math.min(op.position(), current.length());
        int end = Math.min(pos + op.text().length(), current.length());

        return switch (op.type()) {
            case INSERT -> current.substring(0, pos) +
                    op.text() +
                    current.substring(pos);
            case DELETE -> current.substring(0, pos) +
                    current.substring(end);
            case UPDATE -> current.substring(0, pos) +
                    op.text() +
                    current.substring(end);
        };
    }

    public List<CrdtOperation> transformOperations(List<CrdtOperation> existingOps, CrdtOperation newOp) {
        existingOps.add(newOp);
        existingOps.sort(CrdtOperation.operationsComparator());
        return existingOps;
    }
}
