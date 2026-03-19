package dravin.com.entity;


import dravin.com.constant.enumConstant.Permissions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permission",
        uniqueConstraints = {
                @UniqueConstraint(name = "permissionName", columnNames = "name")
        })
@Getter
@Setter
@NoArgsConstructor
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 20)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="name", nullable = false, length = 20)
    private Permissions name;

    public PermissionEntity(Permissions name) {
        this.name = name;
    }
}
