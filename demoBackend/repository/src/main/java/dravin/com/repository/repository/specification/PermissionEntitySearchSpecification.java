package dravin.com.repository.repository.specification;

import dravin.com.repository.entity.PermissionEntity;
import org.springframework.data.jpa.domain.Specification;

public class PermissionEntitySearchSpecification {

    public static Specification<PermissionEntity> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<PermissionEntity> search(String searchItem) {
        return (root, query, criteriaBuilder) -> {
            if (searchItem == null || searchItem.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + searchItem.trim().toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name").as(String.class)),
                    pattern
            );
        };
    }
}
