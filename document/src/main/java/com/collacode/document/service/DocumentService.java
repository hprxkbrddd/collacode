package com.collacode.document.service;

import com.collacode.document.component.DocumentDTOMapper;
import com.collacode.document.crdt.CrdtDocument;
import com.collacode.document.dto.CrdtDocumentDTO;
import com.collacode.document.dto.CrdtDocumentSmallDTO;
import com.collacode.document.dto.ManyCrdtDocsDTO;
import com.collacode.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

    public ManyCrdtDocsDTO getAll(){
        List<CrdtDocument> res = documentRepository.findAll();
        return DocumentDTOMapper.listToDTO(res);
    }

    public CrdtDocumentDTO getById(String id){
        CrdtDocument res = documentRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        String.format("Doc-id:%s is not found", id), 1));
        return DocumentDTOMapper.toDTO(res);
    }

    public CrdtDocumentDTO create(String title){
        return DocumentDTOMapper.toDTO(
                documentRepository.save(new CrdtDocument(title))
        );
    }

    public CrdtDocumentSmallDTO delete(String id){
        CrdtDocument toDelete = documentRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        String.format("Doc-id:%s is not found", id), 1));
        documentRepository.deleteById(id);
        return DocumentDTOMapper.toSmallDTO(toDelete);
    }
}
