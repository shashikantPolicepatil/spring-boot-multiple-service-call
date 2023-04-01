package com.person.app.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.person.app.entity.PersonDto;

@Service
public class PersonService implements Runnable{

	private CountDownLatch latch;
	
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	@Autowired
	private RestTemplate restTemplate;

	@Value("${person.URL}")
	String personUrl;


	@Override
	public void run() {
		try {
			ResponseEntity<PersonDto> personEntity = restTemplate.getForEntity(personUrl+1212, PersonDto.class);
			System.out.println(personEntity.toString());
		} catch(ResourceAccessException ex) {
			System.out.println(ex.getMessage());
		} catch(HttpServerErrorException ex) {
			System.out.println(ex.getMessage());
		}
		latch.countDown();
	}

}
