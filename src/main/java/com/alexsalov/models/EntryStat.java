package com.alexsalov.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.gson.annotations.Expose;

@Entity
@Table(name="entry_stats", uniqueConstraints=@UniqueConstraint(columnNames={"entry_id", "stat_id"}))
public class EntryStat{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@ManyToOne
	@JoinColumn(name="entry_id")
	private Entry entry;

	@ManyToOne
	@JoinColumn(name="stat_id")
	@Expose
	private Stat stat;
	
	@Column(name="val")
	@Expose
	private String value;
	
	public EntryStat(){}
	
	public EntryStat(Entry entry, Stat stat, String value){
		this.entry = entry;
		this.stat = stat;
		this.value = value;
	}
	
	public long getId(){
		return this.id;
	}
	
	public Entry getEntry() {
		return this.entry;
	}

	public void setEntry(Entry entry) {
		this.entry = entry;
	}

	public Stat getStat() {
		return this.stat;
	}

	public void setStat(Stat stat) {
		this.stat = stat;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}