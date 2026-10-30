package edu.srmist.lostandfound.repository;

import edu.srmist.lostandfound.domain.Item;
import edu.srmist.lostandfound.domain.enums.ItemCategory;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i JOIN FETCH i.reporter WHERE i.id = :id")
    Optional<Item> findByIdWithReporter(@Param("id") Long id);

    @Query(value = "SELECT i FROM Item i JOIN FETCH i.reporter r WHERE " +
            "(:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:reportType IS NULL OR i.reportType = :reportType) AND " +
            "(:category IS NULL OR i.category = :category) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%')))",
            countQuery = "SELECT COUNT(i) FROM Item i WHERE " +
            "(:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:reportType IS NULL OR i.reportType = :reportType) AND " +
            "(:category IS NULL OR i.category = :category) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<Item> searchItems(
            @Param("keyword") String keyword,
            @Param("reportType") ReportType reportType,
            @Param("category") ItemCategory category,
            @Param("status") ItemStatus status,
            @Param("location") String location,
            Pageable pageable);

    @Query("SELECT i FROM Item i JOIN FETCH i.reporter WHERE i.reporter.id = :reporterId ORDER BY i.createdAt DESC")
    Page<Item> findByReporterId(@Param("reporterId") Long reporterId, Pageable pageable);

    List<Item> findTop5ByOrderByCreatedAtDesc();

    long countByReportType(ReportType reportType);
    long countByStatus(ItemStatus status);
    long countByReportTypeAndStatus(ReportType reportType, ItemStatus status);

    @Query("SELECT i.category, COUNT(i) FROM Item i GROUP BY i.category")
    List<Object[]> countItemsByCategory();
}
