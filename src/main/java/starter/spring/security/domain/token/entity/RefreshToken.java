package starter.spring.security.domain.token.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.spring.security.global.entity.BaseTimeEntity;
import starter.spring.security.global.entity.converter.BooleanConverter;
import starter.spring.security.domain.user.entity.User;

import javax.persistence.*;
import java.util.UUID;

import static javax.persistence.FetchType.LAZY;

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

    @Column(name = "token", columnDefinition = "BINARY(16)")
    private UUID token;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Convert(converter = BooleanConverter.class)
    @Column(name = "expired")
    private Boolean expired;

    protected RefreshToken(UUID token, User user, Boolean expired) {
        this.token = token;
        this.user = user;
        this.expired = expired;
    }

    public static RefreshToken from(User user) {
        return new RefreshToken(UUID.randomUUID(), user, false);
    }

    public void update() {
        this.token = UUID.randomUUID();
        this.expired = false;
    }

    public void expire() {
        this.expired = true;
    }

    public boolean hasExpired() {
        return this.expired;
    }

    public UUID getUserId() {
        return this.getUser().getUuid();
    }
}
