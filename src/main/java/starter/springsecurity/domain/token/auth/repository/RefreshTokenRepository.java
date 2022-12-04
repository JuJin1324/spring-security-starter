package starter.springsecurity.domain.token.auth.repository;

import starter.springsecurity.domain.entity.repository.CommonRepository;
import starter.springsecurity.domain.token.auth.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/05
 */
//public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
public interface RefreshTokenRepository extends CommonRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(UUID userId);

    <S extends RefreshToken> S save(S entity);
}
