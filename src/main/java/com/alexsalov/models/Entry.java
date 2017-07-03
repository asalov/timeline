package com.alexsalov.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.google.gson.annotations.Expose;

@Entity
@Table(name="entries")
public class Entry {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	@Expose
	private long id;
	
	@ManyToOne
	private User author;
	
	@Column(name="year")
	@NotNull
	@Expose
	private int year;
	
	@Column(name="summary")
	@NotNull
	@Expose
	private String summary;
	
	@Column(name="tournament_recap")
	@Expose
	private String tournamentRecap;
	
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name="created_at", updatable=false)
	private Date createdAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@Column(name="last_modified")
	private Date lastModified;
	
	@OneToMany(mappedBy="entry", cascade=CascadeType.ALL, orphanRemoval=true)
	@Expose
	private List<Fact> facts = new ArrayList<Fact>();
	
	@OneToMany(mappedBy="entry", cascade=CascadeType.ALL, orphanRemoval=true)
	@Expose
	private List<EntryStat> stats = new ArrayList<EntryStat>();
	
	@OneToMany(mappedBy="entry", cascade=CascadeType.ALL, orphanRemoval=true)
	@Expose
	private List<Picture> pictures = new ArrayList<Picture>();
	
	public Entry(){}
	
	public Entry(long id, int year){
		this.id = id;
		this.year = year;
	}
	
	public Entry(long id, User author, int year, String summary, 
			String tournamentRecap, Date createdAt, Date lastModified) {
		this.id = id;
		this.author = author;
		this.year = year;
		this.summary = summary;
		this.tournamentRecap = tournamentRecap;
		this.createdAt = createdAt;
		this.lastModified = lastModified;
	}

	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public User getAuthor() {
		return this.author;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}

	public int getYear() {
		return this.year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTournamentRecap() {
		return this.tournamentRecap;
	}

	public void setTournamentRecap(String tournamentRecap) {
		this.tournamentRecap = tournamentRecap;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getLastModified() {
		return this.lastModified;
	}
	
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public List<EntryStat> getStats() {
		return this.stats;
	}
	
	public List<Fact> getFacts() {
		return this.facts;
	}
	
	public void setFacts(List<Fact> facts) {
		this.facts = facts;
	}
	
	public List<Picture> getPictures() {
		return this.pictures;
	}
	
	public List<Long> getStatIds() {
		List<Long> ids = new ArrayList<Long>();
		
		for(EntryStat entryStat : this.stats){
			ids.add(entryStat.getStat().getId());
		}
		
		return ids;
	}
}