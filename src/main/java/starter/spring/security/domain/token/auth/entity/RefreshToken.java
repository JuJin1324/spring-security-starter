package starter.spring.security.domain.token.auth.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;
import starter.spring.security.domain.entity.BaseTimeEntity;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/05
 */

@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "user_uuid", columnDefinition = "BINARY(16)", updatable = false)
    private UUID userId;

    @Column(name = "token")
    @Getter(AccessLevel.PROTECTED)
    private String token;

    public RefreshToken(UUID userId) {
        this.userId = userId;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public void expire() {
        this.token = null;
    }

    public boolean isExpired() {
        return ObjectUtils.isEmpty(this.token);
    }
}
