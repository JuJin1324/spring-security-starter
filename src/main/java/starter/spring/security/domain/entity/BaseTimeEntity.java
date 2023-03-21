package starter.spring.security.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/22
 */
@MappedSuperclass
@Getter
@Setter(AccessLevel.PROTECTED)

public abstract class BaseTimeEntity {
    @Column(name = "updated_time")
    private LocalDateTime updatedTimeUTC;

    @PrePersist
    public void prePersist() {
        updatedTimeUTC = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedTimeUTC = LocalDateTime.now();
    }
}
