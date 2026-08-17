package dravin.com.repository.repository;

import dravin.com.repository.constant.enumConstant.FriendStatus;
import dravin.com.repository.constant.enumConstant.Status;
import dravin.com.repository.entity.FriendsInformationEntity;
import dravin.com.repository.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface FriendsInformationRepository extends JpaRepository<FriendsInformationEntity, Long> {

    Optional<FriendsInformationEntity> findByUserAAndUserBAndDeletedAtIsNullOrUserBAndUserAAndDeletedAtIsNull(UserEntity userA, UserEntity userB, UserEntity userD, UserEntity userC);

    Page<FriendsInformationEntity> findByUserAAndStatusAndDeletedAtIsNullOrUserBAndStatusAndDeletedAtIsNull(UserEntity userA,FriendStatus statusA, UserEntity userB, FriendStatus statusB, Pageable pageable);

    Page<FriendsInformationEntity> findByUserBAndStatusAndDeletedAtIsNull(UserEntity userA, FriendStatus status, Pageable pageable);

    Optional<FriendsInformationEntity> findByUserAAndUserBAndStatusAndDeletedAtIsNull(UserEntity userA, UserEntity userB, FriendStatus status);


}
