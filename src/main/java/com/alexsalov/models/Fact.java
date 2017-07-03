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
@Table(name="facts")
public class Fact {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@Column(name="description")
	@NotNull
	@Expose
	private String desc;

	@ManyToOne
	private Entry entry;
	
	public Fact(){}
	
	public Fact(String desc) {
		this.desc = desc;
	}
	
	public long getId() {
		return this.id;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String description) {
		this.desc = description;
	}
	
	public Entry getEntry() {
		return this.entry;
	}
	
	public void setEntry(Entry entry) {
		this.entry = entry;
	}
}