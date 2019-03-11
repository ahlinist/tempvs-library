package club.tempvs.library;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableCircuitBreaker
@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
public class LibraryApplication {

	@Value("${amqp.url}")
	private String amqpUrl;
	@Value("${amqp.timeout}")
	private int amqpTimeout;

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
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

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}
}
