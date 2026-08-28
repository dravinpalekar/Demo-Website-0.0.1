package dravin.com.repository.repository;

import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface RoleRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {

    Optional<RoleEntity> findByName(Roles name);

    Optional<RoleEntity> findByNameAndDeletedAtIsNull(Roles name);

    List<RoleEntity> findByDeletedAtIsNull();

    Page<RoleEntity> findByDeletedAtIsNull(Pageable pageable);

    boolean existsByPermissionId(Long id);

    Optional<RoleEntity> findByIdAndDeletedAtIsNull(Long id);

}