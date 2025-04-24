package com.example.demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;


public class ConsumingRestService {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Quote(String Quote, Value value) { }
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Value(Long id, String quote) { }
	
	private static final Logger log = LoggerFactory.getLogger(ConsumingRestService.class);
	
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Bean
	@Profile("!test")
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			Quote quote = restTemplate.getForObject(
					"http://localhost:8080/api/random", Quote.class);
			log.info(quote.toString());
		};
	}
	
	
//	public static void main(String[] args) {
//		SpringApplication.run(ConsumingRestService.class, args);
//	}
	
}
