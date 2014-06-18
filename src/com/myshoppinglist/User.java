package com.myshoppinglist;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	private String emailHash;
	private String passwordHash;

	public Key getKey() {
		return key;
	}

	public String getEmail() {
		return emailHash;
	}

	public void setEmail(String emailHash) {
		this.emailHash = emailHash;
	}

	public String getPassword() {
		return passwordHash;
	}

	public void setPassword(String passwordHash) {
		this.passwordHash = passwordHash;
	}

}
