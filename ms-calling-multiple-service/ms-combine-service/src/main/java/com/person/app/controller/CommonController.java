package com.person.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.person.app.entity.AddrDto;
import com.person.app.entity.CommonDto;
import com.person.app.entity.PersonDto;
import com.person.app.service.AddressService;
import com.person.app.service.PersonService;

@RestController
public class CommonController {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private AddressService addrService;

	@Value("${addr.URL}")
	String addrUrl;

	@Value("${person.URL}")
	String personUrl;

	@GetMapping("/load/person/{personId}")
	public CommonDto getPerson(@PathVariable Integer personId) {
		StopWatch watch = new StopWatch();
		watch.start();
		ResponseEntity<AddrDto> addrEntity;
		ResponseEntity<PersonDto> personEntity;
		try {
			addrEntity = restTemplate.getForEntity(addrUrl + personId, AddrDto.class);
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		try {
			personEntity = restTemplate.getForEntity(personUrl + personId, PersonDto.class);
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		watch.stop();
		System.out.println("Total time:sequencial:" + watch.getTotalTimeMillis());
		return new CommonDto();
	}

	@GetMapping("/load/person/parallel/{personId}")
	public CommonDto getPersonParallelCall(@PathVariable Integer personId) {
		StopWatch watch = new StopWatch();
		watch.start();
		CommonDto response = new CommonDto();

		
		/*CompletableFuture.allOf(CompletableFuture
				.supplyAsync(() -> restTemplate.getForEntity(addrUrl + personId, AddrDto.class)).thenAccept(res -> {
					response.getAddrDto().setAddr1(res.getBody().getAddr1());
					response.getAddrDto().setCityName(res.getBody().getCityName());
					response.getAddrDto().setZipCode(res.getBody().getZipCode());
				}).exceptionally(ex->{
					System.out.println(ex.getMessage());
					return null;
				}),
				CompletableFuture.supplyAsync(() -> restTemplate.getForEntity(personUrl + personId, PersonDto.class))
						.thenAccept(res -> {
							response.getPersonDto().setFName(res.getBody().getFName());
							response.getPersonDto().setLName(res.getBody().getLName());
						}).exceptionally(ex->{
							System.out.println(ex.getMessage());
							return null;
						}))
				.join();*/

		List<CompletableFuture<ResponseEntity<Object>>> completableFutures = new ArrayList<>();
		CompletableFuture<ResponseEntity<Object>> addrFeature = CompletableFuture.supplyAsync(() -> 
			
				restTemplate.getForEntity(addrUrl + personId, Object.class)
			
		).exceptionally(exception->{System.out.println(exception.getMessage());
		return null;});

		CompletableFuture<ResponseEntity<Object>> personFeature = CompletableFuture.supplyAsync(() -> 
			
				restTemplate.getForEntity(personUrl + personId, Object.class)
			
		).exceptionally(exception->{System.out.println(exception.getMessage());
		return null;});

		completableFutures.add(addrFeature);
		completableFutures.add(personFeature);
		CompletableFuture.allOf(addrFeature, personFeature).exceptionally(ex -> null).join();

		Map<Boolean, List<CompletableFuture<ResponseEntity<Object>>>> collect = completableFutures.stream()
				.collect(Collectors.partitioningBy(CompletableFuture::isCompletedExceptionally));

		List<CompletableFuture<ResponseEntity<Object>>> list = collect.get(false);
		list.forEach(obj -> {
			try {
				if(obj.get()!=null) {
				Object body = obj.get().getBody();
				if (body instanceof PersonDto) {
					response.getPersonDto().setFName(((PersonDto) body).getFName());
					response.getPersonDto().setLName(((PersonDto) body).getLName());
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		watch.stop();
		System.out.println("Total time:parallel:" + watch.getTotalTimeMillis());

		return response;
	}
	@GetMapping("/executor/person/{personId}")
	public ResponseEntity<CommonDto> getPersonByTaskExecutor(@PathVariable Integer personId) throws InterruptedException {
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		CountDownLatch countDownLatch = new CountDownLatch(2);
		addrService.setLatch(countDownLatch);
		personService.setLatch(countDownLatch);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		threadPool.execute(addrService);
		threadPool.execute(personService);
		threadPool.shutdown();
		countDownLatch.await();
		stopWatch.stop();
		System.out.println("Total time:executor:"+stopWatch.getTotalTimeMillis());
		return new ResponseEntity<CommonDto>(new CommonDto(),HttpStatus.OK);
	}

}
