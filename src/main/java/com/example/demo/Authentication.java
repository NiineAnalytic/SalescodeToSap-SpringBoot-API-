package com.example.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Authentication {
	
	static private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvEbxPHVMhvoI4JVU0twKmV6+D0glCpxrAiN7+sp88xUvhA+IIrirRCGiq+v5rpG3VMJv3N5+Nxm/2JZwwMlw04tdCOoLdsp4iLc+UNq0iTZ5P2W/U7QhsQNDsA+qzPtZC28AUm1mfkNYu+FEkec5vkRxHk4Co7gd5RjGGlzSLmQIDAQAB";
	static private String url = "https://api.sellinademo.io";

	
	private static String initializePassword() {
	    try {
	        return encrypt(publicKey);
	    } catch (IllegalBlockSizeException | BadPaddingException e) {
	        e.printStackTrace();
	        return "ENCRYPTION_FAILED"; // Fallback value
	    }
	}
	
    private static String encrypt(String publicKey) throws IllegalBlockSizeException, BadPaddingException {
		
        long currentTimestamp = System.currentTimeMillis();
        String data = currentTimestamp + ":" + "@1234";
        
		
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			try {

				cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
				return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			throw new RuntimeException("Encryption failed", e);
		} 
		return "Error Occured";
	}

    
	private static PublicKey getPublicKey(String base64PublicKey) {
		PublicKey publicKey = null;
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return publicKey;
	}

	
	public String signInToProd() {
		String url = "https://api.sellinademo.io";
		String lob = "niinedemo";
		String password = initializePassword();
		
		String jsonBody = new ObjectMapper()
			    .createObjectNode()
			    .put("loginId", "c11226")
			    .put("password", password)
			    .toString();
		
		System.out.println(url);
		
		try {
			HttpClient client = HttpClient.newBuilder()
					.connectTimeout(Duration.ofSeconds(100))
					.build();
			
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url + "/signin?lob=niinedemo&nostore=true"))  //add "&nostore=true" for 'ckt' token (required for PostOrderStatusUpdate API)
					.header("Content-Type", "application/json")
					.header("lob", lob)
					.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
					.build();
			
			HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
			 
			
			if(response.statusCode() >= 200 && response.statusCode()<300) {
	            String body = response.body().toString();
				
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(body);
				
				String bearerToken = jsonNode.get("token").asText();
				
				System.out.println(response);
				System.out.println((String) response.body());
//				(String) response.body()
				return bearerToken;
			}else {
				System.out.println("Error Http: " + response.statusCode() + " - " + response.body());
				return "0";
			}
		} catch(Exception e) {
			e.printStackTrace();
			return "0";
		}
	}
}
