package club.tempvs.library.amqp.impl;

import club.tempvs.library.amqp.UserRoleChannel;
import club.tempvs.library.dto.UserRolesDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserRoleChannelmpl extends AbstractAMQPConnector implements UserRoleChannel {

    private static final String USER_ROLES_AMQP_QUEUE = "user.roles";

    @Value("${amqp.enabled}")
    private boolean amqpEnabled;

    @Autowired
    public UserRoleChannelmpl(ObjectMapper jacksonObjectMapper, ConnectionFactory amqpConnectionFactory) {
        super(amqpConnectionFactory, jacksonObjectMapper, USER_ROLES_AMQP_QUEUE);
    }

    @Async
    public void updateRoles(UserRolesDto userRolesDto) {
        try {
            if (amqpEnabled) {
                String jsonString = jacksonObjectMapper.writeValueAsString(userRolesDto);
                super.send(jsonString);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
