package club.tempvs.library.scheduling;

import club.tempvs.library.amqp.UserSynchronizer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "amqp.enabled", havingValue = "true")
public class SchedulingTasks {

    private final UserSynchronizer userSynchronizer;

    //runs every hour
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void refreshUsers() {
        userSynchronizer.execute();
    }
}
