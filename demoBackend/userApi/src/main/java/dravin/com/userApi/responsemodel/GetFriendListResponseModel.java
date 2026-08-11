package dravin.com.userApi.responsemodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GetFriendListResponseModel {

    private String fullName;

    private String email;

    private String photoData;

}
