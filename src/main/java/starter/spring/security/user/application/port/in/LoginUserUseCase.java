package starter.spring.security.user.application.port.in;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
public interface LoginUserUseCase {
    IssuedAccessToken login(LoginUserCommand command);
}
