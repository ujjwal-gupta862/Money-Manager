package Money.Manager.repository;

import Money.Manager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByProfileIdOrderByDatesDesc(Long profileId);

    List<ExpenseEntity> findByTop5ByProfileIdOrderByDatesDesc(Long profileId);

    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    List<ExpenseEntity> findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyWord,
            Sort sort
    );

    List<ExpenseEntity> findByProfileIdAndDateBetween(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate
    );
}
