package starter.springsecurity.web.filter;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public class InvalidJsonWebTokenException extends RuntimeException {

    public InvalidJsonWebTokenException(String message) {
        super(message);
    }
}
