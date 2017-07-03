package com.alexsalov.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;

@Entity
@Table(name="pictures")
public class Picture {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@Column(name="url")
	@NotNull
	@Expose
	private String url;
	
	@ManyToOne
	private Entry entry;
	
	public Picture(){}
	
	public Picture(String url) {
		this.url = url;
	}

	public long getId() {
		return this.id;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Entry getEntry() {
		return this.entry;
	}
	
	public void setEntry(Entry entry) {
		this.entry = entry;
	}
}