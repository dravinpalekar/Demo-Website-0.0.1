package dravin.com.repository.repository;

import dravin.com.repository.entity.RoomEntity;
import dravin.com.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    Optional<RoomEntity> findByUserAAndUserBAndDeletedAtIsNullOrUserBAndUserAAndDeletedAtIsNull(UserEntity userA, UserEntity userB, UserEntity userC, UserEntity userD);
}
