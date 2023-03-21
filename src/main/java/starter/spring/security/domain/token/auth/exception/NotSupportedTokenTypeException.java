package starter.spring.security.domain.token.auth.exception;

import starter.spring.security.domain.token.auth.entity.TokenType;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/09
 */
public class NotSupportedTokenTypeException extends RuntimeException {
    public NotSupportedTokenTypeException(TokenType tokenType) {
        super("Not supported token type: " + tokenType.name());
    }
}
