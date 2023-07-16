package starter.spring.security.accesstoken.adapter.out.jwt;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
public class InvalidJsonWebTokenException extends RuntimeException {
    public InvalidJsonWebTokenException(String message) {
        super(message);
    }
}
