package dravin.com.repository.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import dravin.com.repository.constant.enumConstant.Gender;
import dravin.com.repository.constant.enumConstant.Permissions;
import dravin.com.repository.constant.enumConstant.Roles;
import dravin.com.repository.constant.enumConstant.SearchFilterColumnName;
import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.PermissionEntity;
import dravin.com.repository.entity.RoleEntity;
import dravin.com.repository.entity.UserEntity;
import dravin.com.repository.entity.UserOtherInformationEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class UserEnitySearchSpecification {

    public static Specification<UserEntity> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<UserEntity> search(SearchFilterColumnName columnName, String searchItem) {
        return (root, query, criteriaBuilder) -> {
            if (columnName == null || searchItem == null || searchItem.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + searchItem.trim().toLowerCase() + "%";

            return switch (columnName) {
                case FULL_NAME -> {
                    Join<UserEntity, UserOtherInformationEntity> otherInfo = root.join("userOtherInformation", JoinType.LEFT);
                    Predicate fullName = criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.concat(
                                            criteriaBuilder.concat(
                                                    criteriaBuilder.concat(
                                                            criteriaBuilder.coalesce(otherInfo.get("firstName"), ""),
                                                            " "),
                                                    criteriaBuilder.concat(
                                                            criteriaBuilder.coalesce(otherInfo.get("middleName"), ""),
                                                            " ")),
                                            criteriaBuilder.coalesce(otherInfo.get("lastName"), ""))),
                            pattern);

                    yield criteriaBuilder.or(fullName);
                }
                case EMAIL_ADDRESS -> criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        pattern);
                case ROLE -> {
                    Roles role = parseEnum(Roles.class, searchItem);
                    if (role == null) {
                        yield criteriaBuilder.disjunction();
                    }
                    if (query != null) {
                        query.distinct(true);
                    }
                    Join<UserEntity, RoleEntity> roleJoin = root.join("role", JoinType.INNER);
                    yield criteriaBuilder.equal(roleJoin.get("name"), role);
                }
                case PERMISSION -> {
                    Permissions permission = parseEnum(Permissions.class, searchItem);
                    if (permission == null) {
                        yield criteriaBuilder.disjunction();
                    }
                    if (query != null) {
                        query.distinct(true);
                    }
                    Join<UserEntity, RoleEntity> roleJoin = root.join("role", JoinType.INNER);
                    Join<RoleEntity, PermissionEntity> permissionJoin = roleJoin.join("permission", JoinType.INNER);
                    yield criteriaBuilder.equal(permissionJoin.get("name"), permission);
                }
                case AGE -> {
                    try {
                        yield criteriaBuilder.equal(
                                root.join("userOtherInformation", JoinType.INNER).get("age"),
                                Integer.valueOf(searchItem.trim()));
                    } catch (NumberFormatException ex) {
                        yield criteriaBuilder.disjunction();
                    }
                }
                case GENDER -> {
                    Gender gender = parseEnum(Gender.class, searchItem);
                    if (gender == null) {
                        yield criteriaBuilder.disjunction();
                    }
                    yield criteriaBuilder.equal(
                            root.join("userOtherInformation", JoinType.INNER).get("gender"),
                            gender);
                }
                case COUNTRY -> criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.join("userOtherInformation", JoinType.INNER).get("country")),
                        pattern);
                case STATUS -> {
                    Status status = parseEnum(Status.class, searchItem);
                    if (status == null) {
                        yield criteriaBuilder.disjunction();
                    }
                    yield criteriaBuilder.equal(root.get("active"), status);
                }
            };
        };
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        try {
            return Enum.valueOf(type, value.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
