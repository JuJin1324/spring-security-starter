package starter.springsecurity.domain.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.user.dto.UserCreateDto;
import starter.springsecurity.domain.user.dto.UserReadDto;
import starter.springsecurity.domain.user.exception.UserAlreadyExistException;
import starter.springsecurity.domain.user.exception.UserNotFoundException;
import starter.springsecurity.domain.user.model.User;
import starter.springsecurity.domain.user.repository.UserRepository;
import starter.springsecurity.domain.user.service.UserService;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public UUID createUser(PhoneNumber phoneNumber, UserCreateDto createDto) {
        boolean alreadyExist = userRepository.findByPhoneNumber(phoneNumber).isPresent();
        if (alreadyExist) {
            throw new UserAlreadyExistException();
        }
        User user = userRepository.save(new User(phoneNumber, createDto.getNickname()));
        return user.getUuid();
    }

    @Override
    public UUID getUserId(UUID authId) {
        User user = userRepository.findByAuthId(authId)
                .orElseThrow(UserNotFoundException::new);
        return user.getUuid();
    }

    @Override
    @Transactional(readOnly = true)
    public UserReadDto getSingleUser(UUID userId) {
        return userRepository.findByUuid(userId)
                .map(UserReadDto::new)
                .orElseThrow(UserNotFoundException::new);
    }
}
