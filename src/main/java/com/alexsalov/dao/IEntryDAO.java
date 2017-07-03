package com.alexsalov.dao;

import java.util.List;

import com.alexsalov.models.Entry;

public interface IEntryDAO {
	public List<Entry> getAllEntries();
	public List<Entry> getEntriesYear();
	public Entry getEntry(long id);
	public void addEntry(Entry entry);
	public void deleteEntry(long id);
	public void updateEntry(Entry entry);
	public boolean isUnique(int year);
}