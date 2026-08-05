package dravin.com.repository.repository;

import dravin.com.repository.entity.FriendsInformationEntity;
import dravin.com.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface FriendsInformationRepository extends JpaRepository<FriendsInformationEntity, Long> {

    Optional<FriendsInformationEntity> findByUserAAndUserB(UserEntity userA, UserEntity userB);

}
