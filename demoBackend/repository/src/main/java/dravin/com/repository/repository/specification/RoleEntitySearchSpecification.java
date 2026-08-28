package dravin.com.repository.repository.specification;

import dravin.com.repository.constant.enumConstant.SearchFilterColumnName;
import dravin.com.repository.constant.enumConstant.SearchFilterRoleColumnName;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.entity.RoleEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class RoleEntitySearchSpecification {

    public static Specification<RoleEntity> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<RoleEntity> search(SearchFilterRoleColumnName columnName, String searchItem) {
        return (root, query, criteriaBuilder) -> {
            if (columnName == null || searchItem == null || searchItem.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + searchItem.trim().toLowerCase() + "%";

            return switch (columnName) {
                case ROLE_NAME -> criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name").as(String.class)),
                        pattern);
                case PERMISSION_NAME -> {
                    if (query != null) {
                        query.distinct(true);
                    }
                    Join<RoleEntity, PermissionEntity> permissionJoin = root.join("permission", JoinType.INNER);
                    yield criteriaBuilder.like(
                            criteriaBuilder.lower(permissionJoin.get("name").as(String.class)),
                            pattern);
                }
                default -> criteriaBuilder.conjunction();
            };
        };
    }
}
