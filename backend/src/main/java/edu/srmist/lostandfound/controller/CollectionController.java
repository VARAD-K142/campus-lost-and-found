package edu.srmist.lostandfound.controller;

import edu.srmist.lostandfound.dto.collection.CollectionRecordDto;
import edu.srmist.lostandfound.dto.collection.RecordCollectionRequest;
import edu.srmist.lostandfound.dto.common.ApiResponse;
import edu.srmist.lostandfound.dto.common.PagedResponse;
import edu.srmist.lostandfound.service.CollectionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @PostMapping("/collections")
    public ResponseEntity<ApiResponse<CollectionRecordDto>> recordCollection(
            @Valid @RequestBody RecordCollectionRequest request) {
        CollectionRecordDto record = collectionService.recordCollection(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Physical item handover and collection recorded successfully", record));
    }

    @GetMapping("/collections/item/{itemId}")
    public ResponseEntity<ApiResponse<CollectionRecordDto>> getCollectionByItemId(@PathVariable Long itemId) {
        CollectionRecordDto record = collectionService.getCollectionByItemId(itemId);
        return ResponseEntity.ok(ApiResponse.ok(record));
    }

    @GetMapping("/collections/my")
    public ResponseEntity<ApiResponse<PagedResponse<CollectionRecordDto>>> getMyCollections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CollectionRecordDto> pageResult = collectionService.getMyCollections(page, size);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.fromPage(pageResult)));
    }

    @GetMapping("/admin/collections")
    public ResponseEntity<ApiResponse<PagedResponse<CollectionRecordDto>>> getAllCollections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CollectionRecordDto> pageResult = collectionService.getAllCollections(page, size);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.fromPage(pageResult)));
    }
}
