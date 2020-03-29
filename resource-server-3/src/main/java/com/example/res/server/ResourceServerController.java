package com.example.res.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceServerController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/hello3", method = RequestMethod.POST, produces = { "application/JSON" })
	public String getHello(@RequestHeader Map<String, String> headers, @RequestBody String name) {

		headers.forEach((key, value) -> {
			LOGGER.debug(String.format("Header '%s' = %s", key, value));
		});

		return "Hi " + name;
	}
}