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
public class GetOrderByNumber {
	
	@Autowired
	private Authentication authenticator;
	
	@GetMapping("/GetOrderByNumber")
    public String GetPrimaryOrders() {
		
		String url = "https://demo.salescode.ai/v2/orders";
		String token = authenticator.signInToProd();
		String bearerToken = token;
		
		String params = url + "?" + 
		                "filter=id:0002698-company-user-shobhit" + "&" + 
				         "type=primary"+ "&" + 
		                 "transformerOut=orderDetailSplitGet" + "&" + 
				         "source=portal";
		
		HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(100))
				.build();
		
		if(token == "0") {
		     return "Failed at Authentication Itself.";
		}
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(params))
				.header("accept", "application/json, text/plain, */*")
				.header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
				.header("authorization", bearerToken)
				.header("lob", "niinedemo")
				.header("origin", "https://cocaind.salescode.ai")
				.GET()
				.build();
		
		try {
			HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			if(response.statusCode() >= 200 && response.statusCode()<300) {
				System.out.println(response.body());
				System.out.println(url);
				System.out.println(bearerToken);
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
