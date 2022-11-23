package starter.springsecurity.domain.authentication.dto;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/23
 */

public class CodeAuthVerificationResult {
    @Getter
    private UUID authId;
    private UUID userId;

    public CodeAuthVerificationResult(UUID authId,
                                      @Nullable UUID userId) {
        this.authId = authId;
        this.userId = userId;
    }

    public Optional<UUID> getUserId() {
        return Optional.ofNullable(userId);
    }
}
