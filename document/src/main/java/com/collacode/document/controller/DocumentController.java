package com.collacode.document.controller;

import com.collacode.document.component.DocumentDTOMapper;
import com.collacode.document.dto.CrdtDocumentDTO;
import com.collacode.document.dto.CrdtDocumentSmallDTO;
import com.collacode.document.dto.ManyCrdtDocsDTO;
import com.collacode.document.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collacode/v1/doc")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<ManyCrdtDocsDTO> getAll() {
        return ResponseEntity.ok(
                documentService.getAll()
        );
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<CrdtDocumentDTO> getById(@PathVariable String id){
        return ResponseEntity.ok(
                DocumentDTOMapper.toDTO(documentService.getById(id))
        );
    }

    @PostMapping("/create")
    public ResponseEntity<CrdtDocumentDTO> create(
            @RequestBody String title,
            @RequestHeader("Authorization")String authHeader) {
        return ResponseEntity.ok(
                documentService.create(title, authHeader.substring(7))
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CrdtDocumentSmallDTO> delete(@PathVariable String id){
        return ResponseEntity.ok(
                documentService.delete(id)
        );
    }
}
