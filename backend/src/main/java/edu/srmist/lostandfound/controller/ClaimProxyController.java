package edu.srmist.lostandfound.controller;

import edu.srmist.lostandfound.dto.claim.ClaimResponseDto;
import edu.srmist.lostandfound.dto.claim.ClaimReviewRequest;
import edu.srmist.lostandfound.dto.claim.SubmitClaimRequest;
import edu.srmist.lostandfound.dto.common.ApiResponse;
import edu.srmist.lostandfound.service.ClaimClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ClaimProxyController {

    private final ClaimClientService claimClientService;

    public ClaimProxyController(ClaimClientService claimClientService) {
        this.claimClientService = claimClientService;
    }

    @PostMapping("/items/{itemId}/claims")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> submitClaim(
            @PathVariable Long itemId,
            @Valid @RequestBody SubmitClaimRequest request) {
        ClaimResponseDto claim = claimClientService.submitClaim(itemId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Ownership claim submitted for review", claim));
    }

    @GetMapping("/claims/{id}")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> getClaimById(@PathVariable Long id) {
        ClaimResponseDto claim = claimClientService.getClaimById(id);
        return ResponseEntity.ok(ApiResponse.ok(claim));
    }

    @GetMapping("/claims/item/{itemId}")
    public ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getClaimsByItem(@PathVariable Long itemId) {
        List<ClaimResponseDto> claims = claimClientService.getClaimsByItem(itemId);
        return ResponseEntity.ok(ApiResponse.ok(claims));
    }

    @GetMapping("/users/me/claims")
    public ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getMyClaims() {
        List<ClaimResponseDto> claims = claimClientService.getMyClaims();
        return ResponseEntity.ok(ApiResponse.ok(claims));
    }

    @GetMapping("/admin/claims")
    public ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getAllClaimsForAdmin(
            @RequestParam(required = false) String status) {
        List<ClaimResponseDto> claims = claimClientService.getAllClaimsForAdmin(status);
        return ResponseEntity.ok(ApiResponse.ok(claims));
    }

    @PostMapping("/admin/claims/{id}/review")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> reviewClaim(
            @PathVariable Long id,
            @Valid @RequestBody ClaimReviewRequest request) {
        ClaimResponseDto reviewed = claimClientService.reviewClaim(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Claim review verdict recorded", reviewed));
    }
}
