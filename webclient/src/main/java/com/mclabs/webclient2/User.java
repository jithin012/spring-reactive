package com.mclabs.webclient2;

public class User {
	/**
	 * "id": 1, "name": "Leanne Graham", "username": "Bret", "email":
	 * "Sincere@april.biz", "address": { "street": "Kulas Light", "suite": "Apt.
	 * 556", "city": "Gwenborough", "zipcode": "92998-3874", "geo": { "lat":
	 * "-37.3159", "lng": "81.1496" } }, "phone": "1-770-736-8031 x56442",
	 * "website": "hildegard.org", "company": { "name": "Romaguera-Crona",
	 * "catchPhrase": "Multi-layered client-server neural-net", "bs": "harness
	 * real-time e-markets" }
	 */
	private Integer id;
	private String name;
	private String email;

	private Object address;
	private String phone;
	private String website;
	private Object company;

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Object getAddress() {
		return address;
	}

	public void setAddress(Object address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Object getCompany() {
		return company;
	}

	public void setCompany(Object company) {
		this.company = company;
	}

	public User(Integer id, String name, String email, Object address, String phone, String website, Object company) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.address = address;
		this.phone = phone;
		this.website = website;
		this.company = company;
	}

	public User() {
	}

}
