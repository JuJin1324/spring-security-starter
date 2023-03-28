package starter.spring.security.domain.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starter.spring.security.domain.user.exception.UserAlreadyExistException;
import starter.spring.security.domain.user.exception.UserNotFoundException;
import starter.spring.security.domain.user.repository.UserRepository;
import starter.spring.security.domain.user.dto.UserCreateDto;
import starter.spring.security.domain.user.dto.UserReadDto;
import starter.spring.security.domain.user.entity.User;
import starter.spring.security.domain.user.service.UserService;

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
    public UUID createUser(UUID authenticationToken, UserCreateDto createDto) {
        boolean alreadyExist = userRepository.findByPhoneNumber(null).isPresent();
        if (alreadyExist) {
            throw new UserAlreadyExistException();
        }
        User user = userRepository.save(new User(null, createDto.getNickname()));
        return user.getUuid();
    }

    @Override
    public UUID getUserId(UUID authenticationToken) {
        User user = userRepository.findByAuthId(authenticationToken)
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
