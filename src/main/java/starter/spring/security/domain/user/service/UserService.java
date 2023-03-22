package starter.spring.security.domain.user.service;

import starter.spring.security.domain.entity.vo.PhoneNumber;
import starter.spring.security.domain.user.dto.UserCreateDto;
import starter.spring.security.domain.user.dto.UserReadDto;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public interface UserService {

    /**
     * 회원 생성
     *
     * @return userId
     */
    UUID createUser(UUID authenticationToken, UserCreateDto createDto);

    /**
     * 회원 ID 조회
     */
    UUID getUserId(UUID authenticationToken);

    /**
     * 회원 단건 조회
     */
    UserReadDto getSingleUser(UUID userId);
}
