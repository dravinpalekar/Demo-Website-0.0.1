package dravin.com.repository.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class RoomEntity extends AbstractDateTimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 20)
    private Long id;

    @Column(name="room_id", nullable = false, length = 70)
    private String roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a", nullable = false)
    private UserEntity userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b", nullable = false)
    private UserEntity userB;

    public RoomEntity(UserEntity userA, UserEntity userB) {
        this.roomId = System.currentTimeMillis() + "_" + UUID.randomUUID();
        this.userA = userA;
        this.userB = userB;
    }
}
