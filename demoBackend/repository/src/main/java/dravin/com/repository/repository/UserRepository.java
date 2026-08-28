package dravin.com.repository.repository;

import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUserNameAndDeletedAtIsNullAndActive(String username, Status status);

    Optional<UserEntity> findByIdAndDeletedAtIsNull(Long id);

    Boolean existsByEmail(String email);

    Page<UserEntity> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT u FROM UserEntity u JOIN u.role r WHERE r.name = :role AND r.deletedAt IS NULL")
    Optional<UserEntity> findUsersByRoleAndDeletedAtIsNull(@Param("role") Roles role);

    // @Query("SELECT u FROM UserEntity u JOIN u.role r WHERE r.name = :role AND
    // u.deletedAt IS NULL AND u.active = :active")
    // List<UserEntity> findUsersByRoleAndActiveAndDeletedAtIsNull(@Param("role")
    // Roles role, @Param("active") Status active);

    // @Query("SELECT u, f FROM UserEntity u JOIN u.role r " +
    // "LEFT JOIN FriendsInformationEntity f ON ((f.userA = u OR f.userB = u) AND
    // f.deletedAt IS NULL) " +
    // "WHERE r.name = :role AND u.active = :active AND u.deletedAt IS NULL")
    // List<Object[]> findUsersByRoleAndActiveAndDeletedAtIsNull( @Param("role")
    // Roles role, @Param("active") Status active );

    @Query("""
                SELECT u FROM UserEntity u JOIN u.role r
                WHERE u.id != :userId AND r.name = :role AND u.deletedAt IS NULL AND u.active = :active
                  AND NOT EXISTS (
                      SELECT fi FROM FriendsInformationEntity fi
                      WHERE (
                          (fi.userA.id = :userId AND fi.userB.id = u.id)
                          OR
                          (fi.userB.id = :userId AND fi.userA.id = u.id)
                      )  AND fi.deletedAt IS NULL
                  )
            """)
    Page<UserEntity> findUsersByRoleAndActiveAndDeletedAtIsNull(@Param("role") Roles role, @Param("userId") Long userId,
            @Param("active") Status active, Pageable pageable);

    List<UserEntity> findByIdInAndDeletedAtIsNullAndActive(List<Long> userA, Status status);

}
