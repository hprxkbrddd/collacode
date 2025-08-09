package com.collacode.document.controller;

import com.collacode.document.crdt.CrdtDocument;
import com.collacode.document.dto.CrdtDocumentDTO;
import com.collacode.document.dto.CrdtDocumentSmallDTO;
import com.collacode.document.dto.ManyCrdtDocsDTO;
import com.collacode.document.service.DocumentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collacode/v1/doc")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/get-all")
    public ResponseEntity<ManyCrdtDocsDTO> getAll() {
        return ResponseEntity.ok(
                documentService.getAll()
        );
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<CrdtDocumentDTO> getById(@PathVariable String id){
        return ResponseEntity.ok(
                documentService.getById(id)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<CrdtDocumentDTO> create(@RequestBody String title) {
        return ResponseEntity.ok(
                documentService.create(title)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CrdtDocumentSmallDTO> delete(@PathVariable String id){
        return ResponseEntity.ok(
                documentService.delete(id)
        );
    }
}
