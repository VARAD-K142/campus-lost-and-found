package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.domain.Item;
import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.ItemCategory;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.ReportType;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.dto.item.CreateItemRequest;
import edu.srmist.lostandfound.dto.item.ItemResponseDto;
import edu.srmist.lostandfound.dto.item.UpdateItemRequest;
import edu.srmist.lostandfound.repository.CollectionRecordRepository;
import edu.srmist.lostandfound.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final CollectionRecordRepository collectionRepository;
    private final UserService userService;
    private final AuditService auditService;

    public ItemService(ItemRepository itemRepository,
                       CollectionRecordRepository collectionRepository,
                       UserService userService,
                       AuditService auditService) {
        this.itemRepository = itemRepository;
        this.collectionRepository = collectionRepository;
        this.userService = userService;
        this.auditService = auditService;
    }

    public ItemResponseDto createItem(CreateItemRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        Item item = new Item();
        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription().trim());
        item.setCategory(request.getCategory());
        item.setReportType(request.getReportType());
        item.setLocation(request.getLocation().trim());
        item.setIncidentDate(request.getIncidentDate());
        item.setImageUrl(request.getImageUrl());
        item.setContactPreference(request.getContactPreference() != null ? request.getContactPreference() : "IN_APP");
        item.setStatus(ItemStatus.ACTIVE);
        item.setReporter(currentUser);

        Item saved = itemRepository.save(item);

        auditService.logAction(currentUser, "ITEM_CREATED", "Item", saved.getId(),
                "Created " + saved.getReportType() + " report: '" + saved.getTitle() + "' in " + saved.getCategory(), null);

        return ItemResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ItemResponseDto getItemById(Long id) {
        Item item = itemRepository.findByIdWithReporter(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with ID: " + id));
        return ItemResponseDto.fromEntity(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemResponseDto> searchItems(String keyword,
                                            ReportType reportType,
                                            ItemCategory category,
                                            ItemStatus status,
                                            String location,
                                            int page,
                                            int size,
                                            String sortBy) {
        Sort sort;
        if ("oldest".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");
        } else if ("title".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "title");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, Math.min(size, 50), sort);
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanLocation = (location != null && !location.trim().isEmpty()) ? location.trim() : null;

        return itemRepository.searchItems(cleanKeyword, reportType, category, status, cleanLocation, pageable)
                .map(ItemResponseDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ItemResponseDto> getMyItems(int page, int size) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), Sort.by(Sort.Direction.DESC, "createdAt"));
        return itemRepository.findByReporterId(currentUser.getId(), pageable)
                .map(ItemResponseDto::fromEntity);
    }

    public ItemResponseDto updateItem(Long id, UpdateItemRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Item item = itemRepository.findByIdWithReporter(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with ID: " + id));

        // Enforce backend ownership check
        boolean isOwner = item.getReporter().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this item report");
        }

        // Prevent modification to locked/resolved records
        if (item.getStatus() == ItemStatus.RESOLVED || item.getStatus() == ItemStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resolved or closed items cannot be modified");
        }

        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription().trim());
        item.setCategory(request.getCategory());
        item.setLocation(request.getLocation().trim());
        item.setIncidentDate(request.getIncidentDate());
        if (request.getImageUrl() != null) item.setImageUrl(request.getImageUrl());
        if (request.getContactPreference() != null) item.setContactPreference(request.getContactPreference());

        Item updated = itemRepository.save(item);

        auditService.logAction(currentUser, "ITEM_UPDATED", "Item", updated.getId(),
                "Updated details for report: '" + updated.getTitle() + "'", null);

        return ItemResponseDto.fromEntity(updated);
    }

    public void deleteItem(Long id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Item item = itemRepository.findByIdWithReporter(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with ID: " + id));

        boolean isOwner = item.getReporter().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this item report");
        }

        // Check if collection record exists or resolved
        if (collectionRepository.existsByItemId(id) || item.getStatus() == ItemStatus.RESOLVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete item because a verified collection or resolution record is associated with it");
        }

        itemRepository.delete(item);

        auditService.logAction(currentUser, "ITEM_DELETED", "Item", id,
                "Deleted item report: '" + item.getTitle() + "'", null);
    }

    public ItemResponseDto adminUpdateStatus(Long id, ItemStatus newStatus, String adminNotes) {
        User admin = userService.getCurrentAuthenticatedUser();
        if (admin.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can update item status directly");
        }

        Item item = itemRepository.findByIdWithReporter(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with ID: " + id));

        ItemStatus oldStatus = item.getStatus();
        item.setStatus(newStatus);
        Item updated = itemRepository.save(item);

        auditService.logAction(admin, "ITEM_STATUS_CHANGED", "Item", updated.getId(),
                "Admin changed status from " + oldStatus + " to " + newStatus + ". Notes: " + adminNotes, null);

        return ItemResponseDto.fromEntity(updated);
    }

    public ItemResponseDto adminResolveItem(Long id, String resolutionNotes) {
        return adminUpdateStatus(id, ItemStatus.RESOLVED, resolutionNotes);
    }

    @Transactional(readOnly = true)
    public Item getItemEntity(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with ID: " + id));
    }
}
