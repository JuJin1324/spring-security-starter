package starter.springsecurity.domain.user.service;

import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.user.dto.UserCreateDto;
import starter.springsecurity.domain.user.dto.UserReadDto;

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
    UUID createUser(PhoneNumber phoneNumber, UserCreateDto createDto);

    /**
     * 회원 ID 조회
     */
    UUID getUserId(UUID authId);

    /**
     * 회원 단건 조회
     */
    UserReadDto getSingleUser(UUID userId);
}
