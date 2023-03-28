package starter.spring.security.domain.token.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import starter.spring.security.global.entity.repository.CommonRepository;
import starter.spring.security.domain.token.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/05
 */
//public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
public interface RefreshTokenRepository extends CommonRepository<RefreshToken, Long> {

    @Query("select rt from RefreshToken rt " +
            "inner join fetch rt.user " +
            "where rt.token = :token")
    Optional<RefreshToken> findWithUserByToken(@Param("token") UUID token);

    @Query("select rt from RefreshToken rt " +
            "inner join rt.user u " +
            "where u.uuid = :userId")
    Optional<RefreshToken> findByUserId(@Param("userId") UUID userId);

    <S extends RefreshToken> S save(S entity);
}
