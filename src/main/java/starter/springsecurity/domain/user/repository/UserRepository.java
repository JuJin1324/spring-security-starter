package starter.springsecurity.domain.user.repository;

import starter.springsecurity.domain.entity.repository.CommonRepository;
import starter.springsecurity.domain.user.model.User;

import java.util.Optional;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/20
 */
//public interface UserRepository extends JpaRepository<User, Long> {
public interface UserRepository extends CommonRepository<User, Long> {

    Optional<Long> findById(User user);
}
