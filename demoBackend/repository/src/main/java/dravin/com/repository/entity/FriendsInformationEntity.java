package dravin.com.repository.entity;


import dravin.com.repository.constant.enumConstant.FriendStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "friends_information")
@Getter
@Setter
@NoArgsConstructor
public class FriendsInformationEntity extends AbstractDateTimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 20)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a", nullable = false)
    private UserEntity userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b", nullable = false)
    private UserEntity userB;

    @Column(name="status", nullable = false)
    private FriendStatus status;

    public FriendsInformationEntity(UserEntity userA, UserEntity userB, FriendStatus status) {
        this.userA = userA;
        this.userB = userB;
        this.status = status;
    }
}
