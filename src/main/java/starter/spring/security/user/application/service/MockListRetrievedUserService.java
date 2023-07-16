package starter.spring.security.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import starter.spring.security.user.application.domain.Email;
import starter.spring.security.user.application.domain.UserId;
import starter.spring.security.user.application.port.in.ListRetrievedUserQuery;
import starter.spring.security.user.application.port.in.RetrievedUser;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Service
@RequiredArgsConstructor
public class MockListRetrievedUserService implements ListRetrievedUserQuery {

    @Override
    public RetrievedUser getOne(UserId id) {
        return new RetrievedUser(id, "이름 Mock", new Email("mock@gmail.com"));
    }
}
