package Money.Manager.repository;

import Money.Manager.entity.ExpenseEntity;
import Money.Manager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {
    List<IncomeEntity> findByProfileIdOrderByDatesDesc(Long profileId);

    List<IncomeEntity> findByTop5ByProfileIdOrderByDatesDesc(Long profileId);

    @Query("SELECT e FROM IncomeEntity e WHERE e.profile.id = :profileId")
    List<IncomeEntity> findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyWord,
            Sort sort
    );

    List<IncomeEntity> findByProfileIdAndDateBetween(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate
    );
}
