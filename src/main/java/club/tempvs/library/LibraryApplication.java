package club.tempvs.library;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
public class LibraryApplication {

	private static final int AMQP_CONNECTION_TIMEOUT = 30000;
	private static final String CLOUDAMQP_URL = System.getenv("CLOUDAMQP_URL");

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

	@Bean
	public ConnectionFactory amqpConnectionFactory() throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();

		if (CLOUDAMQP_URL != null) {
			connectionFactory.setUri(CLOUDAMQP_URL);
			connectionFactory.setConnectionTimeout(AMQP_CONNECTION_TIMEOUT);
		}

		return connectionFactory;
	}
}

