package Money.Manager.repository;

import Money.Manager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository
        extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByEmail(String email);

    List<ProfileEntity> findByIsActiveTrue();
}