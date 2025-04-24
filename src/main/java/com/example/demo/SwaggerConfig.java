package com.example.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Order Details From/To Salescode ")
                                .description("Below are three API Methods, two GET methods to fetch order details "
                                		+ "and one to update status of order. ")
                                .version("v1.0.0"));
    }
}
