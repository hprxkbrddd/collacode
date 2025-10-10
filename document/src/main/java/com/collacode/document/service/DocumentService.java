package com.collacode.document.service;

import com.collacode.document.component.CCJwtDecoder;
import com.collacode.document.component.DocumentDTOMapper;
import com.collacode.document.crdt.CrdtDocument;

import com.collacode.document.dto.*;
import com.collacode.document.repository.DocumentRepository;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final CCJwtDecoder jwtDecoder;
    private final WebClient webClient;

    @Value("${collacode.user-service-url}")
    private String userServiceUrl;

    public DocumentService(DocumentRepository documentRepository,
                           CCJwtDecoder jwtDecoder){
        this.documentRepository = documentRepository;
        this.jwtDecoder = jwtDecoder;
        this.webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

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
        UserEntityDTO user = fetchParticipant(ownerId, token).block();
        return DocumentDTOMapper.toDTO(
                documentRepository.save(new CrdtDocument(title, user))
        );
    }

    public Mono<UserEntityDTO> fetchParticipant(String id, String token){
        return webClient.get()
                .header("Authorization", "Bearer "+token)
                .attribute("userId", id)
                .retrieve()
                .bodyToMono(UserEntityDTO.class);
    }

    public CrdtDocumentSmallDTO delete(String id){
        CrdtDocument toDelete = documentRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        String.format("Doc-id:%s is not found", id), 1));
        documentRepository.deleteById(id);
        return DocumentDTOMapper.toSmallDTO(toDelete);
    }
}
