package com.alexsalov.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="users")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@Column(name="username")
	@NotNull
	private String username;
	
	@Column(name="password")
	@NotNull
	private String password;
	
	@OneToMany(mappedBy="author", cascade=CascadeType.ALL, orphanRemoval=false)
	private List<Entry> entries = new ArrayList<Entry>();

	public User(){}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public long getId() {
		return this.id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
}