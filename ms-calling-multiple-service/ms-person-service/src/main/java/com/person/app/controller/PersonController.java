package com.person.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.person.app.entity.PersonEntity;

@RestController
public class PersonController {
	
	
	@GetMapping("/persons/{personId}")
	public ResponseEntity<PersonEntity> getPerson(@PathVariable Integer personId) {
		
		return new ResponseEntity<PersonEntity>(new PersonEntity("klfdgj","dkgj"),HttpStatus.OK);
	}
	
	

}
