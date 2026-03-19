package dravin.com.entity;


import dravin.com.constant.enumConstant.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "roleName", columnNames = "name")
        })
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 20)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="name", nullable = false, length = 20)
    private Roles name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionEntity> permission = new HashSet<>();

    public RoleEntity(Roles name,Set<PermissionEntity> permission){
        this.name = name;
        this.permission = permission;
    }
}
