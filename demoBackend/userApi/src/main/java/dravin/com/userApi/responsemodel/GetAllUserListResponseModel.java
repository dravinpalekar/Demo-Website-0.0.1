package dravin.com.userApi.responsemodel;


import dravin.com.repository.constant.enumConstant.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class GetAllUserListResponseModel {

    private Long id;

    private String fullName;

    private String fullAddress;

    private String photoData;
}
