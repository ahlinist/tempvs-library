package club.tempvs.library;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
public class LibraryApplication {

	@Value("${amqp.url}")
	private String amqpUrl;
	@Value("${amqp.timeout}")
	private int amqpTimeout;

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

	@Bean
	public ConnectionFactory amqpConnectionFactory() throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();

		if (amqpUrl != null) {
			connectionFactory.setUri(amqpUrl);
			connectionFactory.setConnectionTimeout(amqpTimeout);
		}

		return connectionFactory;
	}
}
