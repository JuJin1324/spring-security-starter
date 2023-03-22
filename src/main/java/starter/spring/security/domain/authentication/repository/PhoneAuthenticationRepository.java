package starter.spring.security.domain.authentication.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import starter.spring.security.domain.authentication.entity.PhoneAuthentication;
import starter.spring.security.domain.entity.repository.CommonRepository;
import starter.spring.security.domain.entity.vo.PhoneNumber;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public interface PhoneAuthenticationRepository extends CommonRepository<PhoneAuthentication, Long> {
//public interface PhoneAuthenticationRepository extends JpaRepository<PhoneAuthentication, Long> {

    @Query("select pa from PhoneAuthentication pa " +
            "where pa.phoneNumber = :phoneNumber " +
            "and pa.createdTimeUTC between :startTimeUTC and :endTimeUTC " +
            "and pa.verified = false " +
            "and pa.expired = false")
    List<PhoneAuthentication> findByPhoneNumberAndCreatedTimeUTCRanged(
            @Param("phoneNumber") PhoneNumber phoneNumber,
            @Param("startTimeUTC")LocalDateTime startTimeUTC,
            @Param("endTimeUTC")LocalDateTime endTimeUTC);

    <S extends PhoneAuthentication> S save(S entity);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deleteAll();
}
