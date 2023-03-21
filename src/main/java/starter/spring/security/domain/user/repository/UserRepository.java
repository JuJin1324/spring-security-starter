package starter.spring.security.domain.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import starter.spring.security.domain.entity.repository.CommonRepository;
import starter.spring.security.domain.entity.vo.PhoneNumber;
import starter.spring.security.domain.user.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/20
 */
//public interface UserRepository extends JpaRepository<User, Long> {
public interface UserRepository extends CommonRepository<User, Long> {

    Optional<User> findByPhoneNumber(PhoneNumber phoneNumber);

    Optional<User> findByUuid(UUID userId);

    @Query("select u from User u " +
            "inner join PhoneAuth pa on pa.phoneNumber = u.phoneNumber " +
            "where pa.uuid = :authId")
    Optional<User> findByAuthId(@Param("authId") UUID authId);

    <S extends User> S save(S entity);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deleteAll();
}
