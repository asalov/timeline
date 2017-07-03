package com.alexsalov.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexsalov.dao.IEntryDAO;
import com.alexsalov.models.Entry;

@Service
public class EntryServiceImpl implements IEntryService {
	
	@Autowired
	private IEntryDAO entryDAO;
	
	@Override
	public List<Entry> getAllEntries() {
		return this.entryDAO.getAllEntries();
	}
	
	@Override
	public List<Entry> getEntriesYear(){
		return this.entryDAO.getEntriesYear();
	}

	@Override
	public Entry getEntry(long id) {
		return this.entryDAO.getEntry(id);
	}

	@Override
	public void addEntry(Entry entry) {
		this.entryDAO.addEntry(entry);
	}

	@Override
	public void deleteEntry(long id) {
		this.entryDAO.deleteEntry(id);
	}

	@Override
	public void updateEntry(Entry entry) {
		this.entryDAO.updateEntry(entry);
	}
	
	@Override
	public boolean isUnique(int year) {
		return this.entryDAO.isUnique(year);
	}
}