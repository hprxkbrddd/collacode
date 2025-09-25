package com.collacode.document.service;

import com.collacode.document.component.CCJwtDecoder;
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
    private final CCJwtDecoder jwtDecoder;

    public ManyCrdtDocsDTO getAll(){
        List<CrdtDocument> res = documentRepository.findAll();
        return DocumentDTOMapper.listToDTO(res);
    }

    public CrdtDocument getById(String id){
        return documentRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        String.format("Doc-id:%s is not found", id), 1));
    }

    public CrdtDocumentDTO create(String title, String token){
        System.out.println("creating document w title '"+title+"'");
        String ownerId = jwtDecoder
                .jwtDecoder()
                .decode(token)
                .getSubject();
        System.out.println("document entitled as '"+title+"' is now owned by user with id "+ownerId);
        return DocumentDTOMapper.toDTO(
                documentRepository.save(new CrdtDocument(title, ownerId))
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
