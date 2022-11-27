package starter.springsecurity.domain.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import starter.springsecurity.domain.authentication.model.PhoneAuth;
import starter.springsecurity.domain.entity.repository.CommonRepository;
import starter.springsecurity.domain.entity.vo.PhoneNumber;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
//public interface PhoneAuthRepository extends CommonRepository<PhoneAuth, Long> {
public interface PhoneAuthRepository extends JpaRepository<PhoneAuth, Long> {

    @Query("select pa from PhoneAuth pa where pa.phoneNumber = :phoneNumber")
    Optional<PhoneAuth> findByPhoneNumber(@Param("phoneNumber") PhoneNumber phoneNumber);

    Optional<PhoneAuth> findByUuid(UUID uuid);

    <S extends PhoneAuth> S save(S entity);
}
