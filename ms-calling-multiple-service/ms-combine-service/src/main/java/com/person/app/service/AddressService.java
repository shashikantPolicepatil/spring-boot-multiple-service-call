package com.person.app.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.person.app.entity.AddrDto;

@Service
public class AddressService implements Runnable {

	private CountDownLatch latch;

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	@Autowired
	private RestTemplate restTemplate;

	@Value("${addr.URL}")
	String addrUrl;

	@Override
	public void run() {
		try {
			ResponseEntity<AddrDto> addrEntity = restTemplate.getForEntity(addrUrl + 1212, AddrDto.class);
			System.out.println(addrEntity);
		} catch (HttpClientErrorException ex) {
			System.out.println(ex.getMessage());
		} catch (HttpServerErrorException ex) {
			System.out.println(ex.getMessage());
		}
		latch.countDown();
	}

}
