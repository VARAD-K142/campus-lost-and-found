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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CollectionRecordRepository collectionRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ItemService itemService;

    private User owner;
    private User otherUser;
    private Item testItem;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setFullName("Rahul Sharma");
        owner.setEmail("rahul@srmist.edu.in");
        owner.setRole(Role.USER);

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setFullName("Unauthorized Person");
        otherUser.setEmail("other@srmist.edu.in");
        otherUser.setRole(Role.USER);

        testItem = new Item();
        testItem.setId(10L);
        testItem.setTitle("Casio Calculator");
        testItem.setDescription("Scientific calculator left in TP Lab");
        testItem.setCategory(ItemCategory.ELECTRONICS);
        testItem.setReportType(ReportType.LOST);
        testItem.setLocation("Tech Park Lab 3");
        testItem.setIncidentDate(LocalDate.now());
        testItem.setStatus(ItemStatus.ACTIVE);
        testItem.setReporter(owner);
    }

    @Test
    @DisplayName("Should create item associated with authenticated reporter")
    void shouldCreateItem() {
        CreateItemRequest request = new CreateItemRequest();
        request.setTitle("Casio Calculator");
        request.setDescription("Scientific calculator left in TP Lab");
        request.setCategory(ItemCategory.ELECTRONICS);
        request.setReportType(ReportType.LOST);
        request.setLocation("Tech Park Lab 3");
        request.setIncidentDate(LocalDate.now());

        when(userService.getCurrentAuthenticatedUser()).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ItemResponseDto response = itemService.createItem(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Casio Calculator");
        assertThat(response.getReporterId()).isEqualTo(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("Should prevent non-owner user from editing another user's item report")
    void shouldPreventNonOwnerFromUpdatingItem() {
        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setTitle("Hacked Title");
        updateRequest.setDescription("Malicious edit attempt");
        updateRequest.setCategory(ItemCategory.OTHER);
        updateRequest.setLocation("Random");
        updateRequest.setIncidentDate(LocalDate.now());

        when(userService.getCurrentAuthenticatedUser()).thenReturn(otherUser);
        when(itemRepository.findByIdWithReporter(10L)).thenReturn(Optional.of(testItem));

        assertThatThrownBy(() -> itemService.updateItem(10L, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You are not authorized to edit this item report");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Should prevent editing resolved or collected items")
    void shouldPreventEditingResolvedItems() {
        testItem.setStatus(ItemStatus.RESOLVED);

        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setTitle("New Title");
        updateRequest.setDescription("Description");
        updateRequest.setCategory(ItemCategory.BOOKS);
        updateRequest.setLocation("Library");
        updateRequest.setIncidentDate(LocalDate.now());

        when(userService.getCurrentAuthenticatedUser()).thenReturn(owner);
        when(itemRepository.findByIdWithReporter(10L)).thenReturn(Optional.of(testItem));

        assertThatThrownBy(() -> itemService.updateItem(10L, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Resolved or closed items cannot be modified");
    }
}
