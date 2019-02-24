package club.tempvs.library.configuration;

import club.tempvs.library.holder.UserHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class AuthConfiguration {

    @Bean
    @RequestScope
    public UserHolder userHolder() {
        return new UserHolder();
    }
}
