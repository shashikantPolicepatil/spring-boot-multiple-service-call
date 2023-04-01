package com.address.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.address.app.entity.AddressEntity;

@RestController
public class AddressController {
	
	@GetMapping("/addresses/{addrId}")
	public ResponseEntity<AddressEntity> getAddress(@PathVariable Integer addrId) throws Exception {
		//Thread.sleep(4000);
		return new ResponseEntity<AddressEntity>(new AddressEntity("dklsjg","sldkgj",65656), HttpStatus.OK);
	}
}
