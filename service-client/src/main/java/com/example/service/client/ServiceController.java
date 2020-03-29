package com.example.service.client;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Configuration
public class ServiceController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = "/test", method = RequestMethod.POST, produces = { "application/JSON" }, consumes = {
			"text/plain" })
	public String getDetails(@RequestParam(value = "name", defaultValue = "0") String name) {

		String accessToken = obtainAccessToken();

		String apiResponse1 = invokeAPI(accessToken, "http://localhost:9081/hello", "ABRAHAM");

		String apiResponse2 = invokeAPI(accessToken, "http://localhost:9082/hello2", "GEORGE");

		return "SUCCESS \n" + apiResponse1 + " " + apiResponse2;
	}

	private String invokeAPI(String accessToken, String apiURL, String input) {

		ResponseEntity<String> response = null;

		try {

			// Now going to invoke the API1 using this token

			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

			HttpHeaders headers = new HttpHeaders();

			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.TEXT_PLAIN);
			headers.add("Authorization", "Bearer " + accessToken);

			map.add("name", input);

			HttpEntity<String> request = new HttpEntity<>(input, headers);

			response = restTemplate.exchange(apiURL, HttpMethod.POST, request, String.class);
			
			LOGGER.debug("API Response ---------" + response.getBody());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ((null != response) ? response.getBody() : "");

	}

	private String obtainAccessToken() {

		ResponseEntity<String> response = null;
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

		// According OAuth documentation we need to send the client id and secret key in
		// the header for authentication
		String credentials = "exampleClient:exampleSecret";
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		LOGGER.debug("encodedCredentials ---------" + encodedCredentials);

		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", "Basic " + encodedCredentials);

		map.add("grant_type", "password");
		map.add("username", "abraham");
		map.add("password", "abraham");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		String access_token_url = "http://localhost:8080/oauth/token";

		response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

		LOGGER.debug("Access Token Response ---------" + response.getBody());

		JSONObject jsonObject = new JSONObject(response.getBody().toString());
		String accessToken = jsonObject.getString("access_token");
		LOGGER.debug("Access Token :: " + accessToken);

		return accessToken;
	}

}