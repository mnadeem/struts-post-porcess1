package com.test.struts;

public class Employee {
	private String name;
	private String ssNum;

	public Employee(final String name, final String ssNum) {
		this.name = name;
		this.ssNum = ssNum;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSsNum(final String ssNum) {
		this.ssNum = ssNum;
	}

	public String getSsNum() {
		return ssNum;
	}
}
