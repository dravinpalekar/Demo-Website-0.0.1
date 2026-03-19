package dravin.com.entity;


import dravin.com.constant.enumConstant.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "users_other_information")
@Getter
@Setter
@NoArgsConstructor
public class UserOtherInformationEntity extends AbstractDateTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 20)
    private Long id;

    @Column(name="first_name", nullable = false, length = 30)
    private String firstName;

    @Column(name="middle_name", nullable = true, length = 30)
    private String middleName;

    @Column(name="last_name", nullable = false, length = 30)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name="gender", nullable = false, length = 20)
    private Gender gender;

    @Positive
    @Range(min = 4, max = 100, message = "Please select 04+ positive numbers under the 100")
    @Column(name="age", nullable = false, length = 5)
    private Integer age;

    @Column(name="country", nullable = false, length = 20)
    private String country;

    @Column(name="city", nullable = false, length = 20)
    private String city;

    @Column(name="pin_code", nullable = false, length = 6)
    private Integer pinCode;

    @Column(name="address", nullable = false, length = 255)
    private String address;

    @Column(name="photo_url", nullable = true)
    private String photoUrl;
}
