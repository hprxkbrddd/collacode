package com.collacode.document.dto;

import com.collacode.document.crdt.CrdtOperation;

import java.util.List;
import java.util.Map;

public record CrdtDocumentDTO(
        String id,
        String title,
        String content,
        Map<String, Long> versionVector,
        List<CrdtOperation> operations
) {
}
