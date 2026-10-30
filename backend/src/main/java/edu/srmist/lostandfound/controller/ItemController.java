package edu.srmist.lostandfound.controller;

import edu.srmist.lostandfound.domain.enums.ItemCategory;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.ReportType;
import edu.srmist.lostandfound.dto.common.ApiResponse;
import edu.srmist.lostandfound.dto.common.PagedResponse;
import edu.srmist.lostandfound.dto.item.CreateItemRequest;
import edu.srmist.lostandfound.dto.item.ItemResponseDto;
import edu.srmist.lostandfound.dto.item.UpdateItemRequest;
import edu.srmist.lostandfound.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<PagedResponse<ItemResponseDto>>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ReportType reportType,
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "newest") String sortBy) {

        Page<ItemResponseDto> pageResult = itemService.searchItems(keyword, reportType, category, status, location, page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.fromPage(pageResult)));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> getItemById(@PathVariable Long id) {
        ItemResponseDto item = itemService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<ItemResponseDto>> createItem(@Valid @RequestBody CreateItemRequest request) {
        ItemResponseDto created = itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Item report submitted successfully", created));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateItemRequest request) {
        ItemResponseDto updated = itemService.updateItem(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Item report updated successfully", updated));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Item report deleted successfully", "Item #" + id + " removed"));
    }

    @GetMapping("/items/my")
    public ResponseEntity<ApiResponse<PagedResponse<ItemResponseDto>>> getMyItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ItemResponseDto> pageResult = itemService.getMyItems(page, size);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.fromPage(pageResult)));
    }

    @PatchMapping("/admin/items/{id}/status")
    public ResponseEntity<ApiResponse<ItemResponseDto>> adminUpdateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        ItemStatus newStatus = ItemStatus.valueOf(payload.get("status"));
        String adminNotes = payload.getOrDefault("adminNotes", "Status updated by administrator");
        ItemResponseDto updated = itemService.adminUpdateStatus(id, newStatus, adminNotes);
        return ResponseEntity.ok(ApiResponse.ok("Item status updated", updated));
    }

    @PostMapping("/admin/items/{id}/resolve")
    public ResponseEntity<ApiResponse<ItemResponseDto>> adminResolveItem(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload) {
        String notes = (payload != null) ? payload.getOrDefault("notes", "Resolved by administrator") : "Resolved by administrator";
        ItemResponseDto updated = itemService.adminResolveItem(id, notes);
        return ResponseEntity.ok(ApiResponse.ok("Item marked as resolved", updated));
    }
}
