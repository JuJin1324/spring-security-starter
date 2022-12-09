package starter.springsecurity.domain.token.auth.exception;

import starter.springsecurity.domain.token.auth.model.TokenType;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/09
 */
public class NotSupportedTokenTypeException extends RuntimeException {
    public NotSupportedTokenTypeException(TokenType tokenType) {
        super("Not supported token type: " + tokenType.name());
    }
}
