package com.example.res.server;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ResourceServerController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/hello2", method = RequestMethod.POST, produces = { "application/JSON" })
	public String getHello(@RequestHeader Map<String, String> headers, @RequestBody String name) {

		headers.forEach((key, value) -> {
			LOGGER.debug(String.format("Header '%s' = %s", key, value));
		});

		String accessToken = headers.entrySet().stream().filter(e -> StringUtils.equalsIgnoreCase(e.getKey(), "authorization")).findFirst()
				.get().getValue();
		
		LOGGER.debug("accessToken :: ["+accessToken+"]");

		accessToken = accessToken.replaceAll("Bearer ","");
		
		LOGGER.debug("accessToken now :: ["+accessToken+"]");
		
		String apiResponse3 = invokeAPI(accessToken, "http://localhost:9084/hello3", "TEST");

		return "Hi " + name + " "+apiResponse3;
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
}