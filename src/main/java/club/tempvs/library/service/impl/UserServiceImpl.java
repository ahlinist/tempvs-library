package club.tempvs.library.service.impl;

import club.tempvs.library.dao.UserRepository;
import club.tempvs.library.domain.User;
import club.tempvs.library.dto.UserInfoDto;
import club.tempvs.library.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public User saveUser(UserInfoDto userInfoDto) {
        User user = new User(userInfoDto);
        return userRepository.save(user);
    }
}
