package com.test;

import com.internal.field.ApiField;

public class Perlet {
	@ApiField("name")
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
