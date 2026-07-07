package dravin.com.repository.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "userName", columnNames = "user_name"),
                @UniqueConstraint(name = "email", columnNames = "email")
        })
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends AbstractDateTimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 20)
    private Long id;

    @Column(name="user_name", nullable = true, length = 20)
    private String userName;

    @Email
    @Column(name="email", nullable = false, length = 30)
    private String email;

    @Column(name="password", nullable = false, length = 255)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_other_information_id", referencedColumnName = "id", nullable = true)
    private UserOtherInformationEntity userOtherInformation;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> role = new HashSet<>();

    public UserEntity(String email, String username, String password, Set<RoleEntity> role) {
        this.email = email;
        this.userName = username;
        this.password = password;
        this.role = role;
    }
}
