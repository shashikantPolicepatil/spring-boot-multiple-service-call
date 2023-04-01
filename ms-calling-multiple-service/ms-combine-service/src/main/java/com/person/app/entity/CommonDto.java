package com.person.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class CommonDto {

	private AddrDto addrDto;
	private PersonDto personDto;
	
	public CommonDto() {
		this.addrDto=new AddrDto();
		this.personDto=new PersonDto();
	}
}
