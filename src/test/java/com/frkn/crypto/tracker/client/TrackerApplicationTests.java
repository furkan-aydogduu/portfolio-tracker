package com.frkn.crypto.tracker.client;

import com.frkn.crypto.tracker.configuration.ServerProperties;
import com.frkn.crypto.tracker.model.ResponseMessage;
import com.frkn.crypto.tracker.model.ResponseMessageType;
import com.frkn.crypto.tracker.service.PortfolioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrackerApplicationTests {

	@Autowired
	protected TestRestTemplate restTemplate;

	@Autowired
	protected ServerProperties serverProperties;

	@Autowired
	protected PortfolioService portfolioService;

	@LocalServerPort
	protected Integer serverPort;

	protected String controllerPath;

	protected String buildURL(String methodURL){
		return "http://localhost:" + serverPort + serverProperties.getContextPath() + "/" + this.controllerPath + methodURL;
	}

	protected <R> ResponseEntity<R> makeRequest(String url, String requestBody, Class<R> returnType) throws URISyntaxException {
		MultiValueMap<String, String> headers = new HttpHeaders();
		List contentType = new ArrayList();
		contentType.add("application/json");
		headers.put("Content-Type",contentType);

		RequestEntity entity = new RequestEntity(requestBody, headers, HttpMethod.POST, new URI(buildURL(url)));
		ResponseEntity<R> response = restTemplate.exchange(
				entity,
				returnType);

		assert response.getStatusCode().is2xxSuccessful();
		assert response.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON);

		return response;
	}

	protected <R> ResponseEntity<R> makeRequest(String url, Class<R> returnType) throws URISyntaxException {
		MultiValueMap<String, String> headers = new HttpHeaders();
		List contentType = new ArrayList();
		contentType.add("application/json");
		headers.put("Content-Type",contentType);

		RequestEntity entity = new RequestEntity(headers, HttpMethod.POST, new URI(buildURL(url)));
		ResponseEntity<R> response = restTemplate.exchange(
				entity,
				returnType);

		assert response.getStatusCode().is2xxSuccessful();
		assert response.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON);

		return response;
	}

	protected void assertResponseMessageForTypes(ResponseMessage responseMessage, ResponseMessageType... responseMessageTypes){
		assert responseMessage != null;
		assert responseMessage.getMessage() != null;
		assert !responseMessage.getMessage().isEmpty();
		assert responseMessageTypes.length == 0 || Arrays
				.stream(responseMessageTypes)
				.anyMatch(messageType -> messageType.getMessage().equals(responseMessage.getMessage()));
	}

	@Test
	public void contextLoads() {
	}

}
