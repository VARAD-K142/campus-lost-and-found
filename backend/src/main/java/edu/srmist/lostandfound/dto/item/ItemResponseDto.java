package edu.srmist.lostandfound.dto.item;

import edu.srmist.lostandfound.domain.Item;
import edu.srmist.lostandfound.domain.enums.ItemCategory;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.ReportType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ItemResponseDto {
    private Long id;
    private String title;
    private String description;
    private ItemCategory category;
    private ReportType reportType;
    private String location;
    private LocalDate incidentDate;
    private LocalDate reportDate;
    private String imageUrl;
    private String contactPreference;
    private ItemStatus status;
    private Long reporterId;
    private String reporterName;
    private String reporterDepartment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ItemResponseDto() {}

    public static ItemResponseDto fromEntity(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setCategory(item.getCategory());
        dto.setReportType(item.getReportType());
        dto.setLocation(item.getLocation());
        dto.setIncidentDate(item.getIncidentDate());
        dto.setReportDate(item.getReportDate());
        dto.setImageUrl(item.getImageUrl());
        dto.setContactPreference(item.getContactPreference());
        dto.setStatus(item.getStatus());
        if (item.getReporter() != null) {
            dto.setReporterId(item.getReporter().getId());
            dto.setReporterName(item.getReporter().getFullName());
            dto.setReporterDepartment(item.getReporter().getDepartment());
        }
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ItemCategory getCategory() { return category; }
    public void setCategory(ItemCategory category) { this.category = category; }
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getContactPreference() { return contactPreference; }
    public void setContactPreference(String contactPreference) { this.contactPreference = contactPreference; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getReporterDepartment() { return reporterDepartment; }
    public void setReporterDepartment(String reporterDepartment) { this.reporterDepartment = reporterDepartment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
