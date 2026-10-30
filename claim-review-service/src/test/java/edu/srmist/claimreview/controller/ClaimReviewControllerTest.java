package edu.srmist.claimreview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.srmist.claimreview.domain.ClaimStatus;
import edu.srmist.claimreview.dto.ClaimRecordDto;
import edu.srmist.claimreview.dto.SubmitClaimDto;
import edu.srmist.claimreview.service.ClaimReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClaimReviewController.class)
@TestPropertySource(properties = "service.api-key=test-only-api-key")
class ClaimReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimReviewService claimService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_KEY = "test-only-api-key";

    @Test
    @DisplayName("POST /api/v1/claims should return 401 when API key is missing")
    void shouldRejectWithoutApiKey() throws Exception {
        SubmitClaimDto dto = new SubmitClaimDto();
        dto.setItemId(1L);
        dto.setClaimantUserId(2L);
        dto.setClaimantName("Test Student");
        dto.setClaimantEmail("student@srmist.edu.in");
        dto.setClaimExplanation("Lost my blue bag");
        dto.setDistinguishingFeatures("Nike logo");

        mockMvc.perform(post("/api/v1/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/claims should return 201 Created with valid payload and API key")
    void shouldCreateClaimWithApiKey() throws Exception {
        SubmitClaimDto dto = new SubmitClaimDto();
        dto.setItemId(1L);
        dto.setClaimantUserId(2L);
        dto.setClaimantName("Test Student");
        dto.setClaimantEmail("student@srmist.edu.in");
        dto.setClaimExplanation("Lost my blue bag with books");
        dto.setDistinguishingFeatures("Nike logo on front zipper");

        ClaimRecordDto responseDto = new ClaimRecordDto();
        responseDto.setId(55L);
        responseDto.setItemId(1L);
        responseDto.setStatus(ClaimStatus.PENDING);
        responseDto.setClaimantName("Test Student");

        when(claimService.submitClaim(any(SubmitClaimDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/claims")
                        .header("X-Service-API-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/v1/claims/{id} should return 200 OK")
    void shouldGetClaimById() throws Exception {
        ClaimRecordDto responseDto = new ClaimRecordDto();
        responseDto.setId(55L);
        responseDto.setItemId(1L);
        responseDto.setStatus(ClaimStatus.PENDING);

        when(claimService.getClaimById(55L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/claims/55")
                        .header("X-Service-API-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(55L));
    }
}
