package com.collacode.document.controller;

import com.collacode.document.crdt.CrdtDocument;
import com.collacode.document.crdt.CrdtOperation;
import com.collacode.document.service.CollaborationService;
import com.collacode.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DocumentUpdateController {

    private final CollaborationService collaborationService;
    private final DocumentService documentService;

    // Обработка подписки на документ
    @MessageMapping("/subscribe/{docId}")
    @SendTo("/topic/doc")
    public CrdtDocument subscribeToDocument(
            @DestinationVariable String docId
    ) {
        return documentService.getById(docId);
    }

    // Обработка изменений документа
    @MessageMapping("/documents/{docId}/edit")
    public void handleDocumentEdit(
            @DestinationVariable String docId,
            @Payload CrdtOperation operation
//            ,@Header("Authorization") String token
    ) {
        collaborationService.applyOperation(docId, operation);
    }
}
