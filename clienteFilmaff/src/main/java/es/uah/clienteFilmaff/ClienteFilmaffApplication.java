package es.uah.clienteFilmaff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ClienteFilmaffApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClienteFilmaffApplication.class, args);
	}

	@Bean
	public RestTemplate template()
	{
		RestTemplate template = new RestTemplate();
		return template;
	}

}
