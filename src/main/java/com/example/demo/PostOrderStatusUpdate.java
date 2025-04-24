package com.example.demo;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/post")
@Tag(name = "Order API", description = "Update Operations related to orders")
public class PostOrderStatusUpdate {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private OrderDetails order;
	
	@Autowired
	private Authentication authenticator;
	
	
	@Operation(summary = "Update order status", 
            description = "Endpoint to update the status of an order. No parameters allowed, only json objects "
            		+ "with order details enclosed in square brackets  ' [ ] ' ")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "500", description = "Internal server error")
	@PostMapping("/OrderStatusUpdate")
//    public String PostOrderStatusUpdate() {
//		
//		String url = "https://integ-pub-prod.sellina.io/data";
//		String token = authenticator.signInToProd();
//		String bearerToken = "Bearer " + token;
//		
//		order.setOrderNumber("1245");
//		order.setCustomerName("CONFIRMED");
//		
//		ObjectMapper objectMapper = new ObjectMapper();
//	    String jsonBody = "";
//	    try {
//	        jsonBody = objectMapper.writeValueAsString(order);
//	    } catch (JsonProcessingException e) {
//	        e.printStackTrace();
//	        return "Failed to convert order to JSON.";
//	    }
//	    
//		
//		String params = url + "?" + 
//		                "entityName=Order" + "&" + 
//				         "skipPreprocessing=true";
//		
//		HttpClient client = HttpClient.newBuilder()
//				.connectTimeout(Duration.ofSeconds(100))
//				.build();
//		
//		if(token == "0") {
//		     return "Failed at Authentication Itself.";
//		}
//		
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create(params))
//				.header("Content-Type", "application/json")
//				.header("Authorization", bearerToken)
//				.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
//				.build();
//		
//		try {
//			HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
//			
//			if(response.statusCode() >= 200 && response.statusCode()<300) {
//				System.out.println(response.body());
//				return response.body().toString();
//			}
//			
//		} catch (IOException | InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return e.toString();
//		}
//		
//		return "Request Failed.";
//	}
	
	public ResponseEntity<String> postOrderStatusUpdate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Array of orders whose statuses are to be updated",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = "[{\"orderNumber\": \"123\", \"status\": \"CONFIRMED\"}, " +
                                "{\"orderNumber\": \"1234\", \"status\": \"CONFIRMED\"}]"
                    )
                )
            )
            @RequestBody List<OrderDetails> orderUpdates) {
        
        if (orderUpdates == null || orderUpdates.isEmpty()) {
            return ResponseEntity.badRequest().body("No order updates provided");
        }

        String token = authenticator.signInToProd();
        if ("0".equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed at Authentication");
        }

        String bearerToken = "Bearer " + token;
        String url = "https://integ-pub-prod.sellina.io/data?entityName=Order&skipPreprocessing=true";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(100))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder responseBuilder = new StringBuilder();

        for (OrderDetails update : orderUpdates) {
            try {
                String jsonBody = objectMapper.writeValueAsString(update);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", bearerToken)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    responseBuilder.append("Order ")
                                 .append(update.getOrderNumber())
                                 .append(" updated successfully. Response: ")
                                 .append(response.body())
                                 .append("\n");
                } else {
                    responseBuilder.append("Failed to update order ")
                                 .append(update.getOrderNumber())
                                 .append(". Status code: ")
                                 .append(response.statusCode())
                                 .append("\n");
                }
            } catch (Exception e) {
                responseBuilder.append("Error processing order ")
                             .append(update.getOrderNumber())
                             .append(": ")
                             .append(e.getMessage())
                             .append("\n");
            }
        }

        return ResponseEntity.ok(responseBuilder.toString());
    }
}
