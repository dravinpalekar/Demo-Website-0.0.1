package dravin.com.repository.repository;


import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserName(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u JOIN u.role r WHERE r.name = :role")
    Optional<UserEntity> findUsersByRole(@Param("role") Roles role);
}
