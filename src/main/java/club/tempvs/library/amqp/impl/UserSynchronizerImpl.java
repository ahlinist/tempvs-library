package club.tempvs.library.amqp.impl;

import club.tempvs.library.amqp.UserSynchronizer;
import club.tempvs.library.dto.UserDto;
import club.tempvs.library.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserSynchronizerImpl extends AbstractAMQPConnector implements UserSynchronizer {

    private static final String LIBRARY_USER_AMQP_QUEUE = "library.user";

    private final ObjectMapper jacksonObjectMapper;
    private final UserService userService;

    @Autowired
    public UserSynchronizerImpl(ObjectMapper jacksonObjectMapper,
                                UserService userService,
                                ConnectionFactory amqpConnectionFactory) {
        super(amqpConnectionFactory);
        this.userService = userService;
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public void execute() {
        super.execute(jsonMessage -> refreshParticipant(jsonMessage));
    }

    protected String getQueue() {
        return LIBRARY_USER_AMQP_QUEUE;
    }

    private void refreshParticipant(String json) {
        try {
            UserDto userDto = jacksonObjectMapper.readValue(json, UserDto.class);
            userService.saveUser(userDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
