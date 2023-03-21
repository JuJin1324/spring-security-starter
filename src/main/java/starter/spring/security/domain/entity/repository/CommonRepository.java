package starter.spring.security.domain.entity.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/20
 */
@NoRepositoryBean
public interface CommonRepository<T, ID extends Serializable> extends Repository<T, ID> {
}
