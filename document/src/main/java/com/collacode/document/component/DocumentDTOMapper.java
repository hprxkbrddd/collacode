package com.collacode.document.component;

import com.collacode.document.crdt.CrdtDocument;
import com.collacode.document.dto.CrdtDocumentDTO;
import com.collacode.document.dto.CrdtDocumentSmallDTO;
import com.collacode.document.dto.ManyCrdtDocsDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentDTOMapper {
    public static CrdtDocumentDTO toDTO(CrdtDocument doc) {
        return new CrdtDocumentDTO(
                doc.getId(),
                doc.getTitle(),
                doc.getContent(),
                doc.getVersionVector(),
                doc.getOperations()
        );
    }

    public static CrdtDocumentSmallDTO toSmallDTO(CrdtDocument doc) {
        return new CrdtDocumentSmallDTO(
                doc.getId(), doc.getTitle()
        );
    }

    public static ManyCrdtDocsDTO listToDTO(List<CrdtDocument> docs) {
        List<CrdtDocumentSmallDTO> res = docs.stream().map(DocumentDTOMapper::toSmallDTO).toList();
        return new ManyCrdtDocsDTO(res);
    }
}
