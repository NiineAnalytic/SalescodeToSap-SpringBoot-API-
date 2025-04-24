package com.example.demo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/get")
public class GetPrimary {
	
	@Autowired
	private Authentication authenticator;
	
	@GetMapping("/GetPrimaryOrders")
    public String GetPrimaryOrders() {
		
		String url = "https://demo.salescode.ai/v2/orders";
		String token = authenticator.signInToProd();
		String bearerToken = "Bearer " + token;
		

		
		String params = url + "?" + 
		                "size=1000" + "&" + 
				         "sort=creationTime:desc"+ "&" + 
		                 "transformerOut=orderDetailSplitGet" + "&" + 
				         "source=portal" + "&" + 
		                 "filter=type%3Aprimary";
		
		HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(100))
				.build();
		
		if(token == "0") {
		     return "Failed at Authentication Itself.";
		}
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(params))
				.header("Content-Type", "application/json")
				.header("Authorization", bearerToken)
				.GET()
				.build();
		
		try {
			HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			if(response.statusCode() >= 200 && response.statusCode()<300) {
				System.out.println(response.body());
				return response.body().toString();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}
		
		return "Request Failed.";
	}
}
