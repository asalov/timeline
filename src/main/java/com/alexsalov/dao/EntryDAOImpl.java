package com.alexsalov.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.alexsalov.models.Entry;
import com.alexsalov.models.Picture;

@Repository
public class EntryDAOImpl implements IEntryDAO {

	@Autowired
	private SessionFactory factory;
	
	@Value("${upload.directory}")
	private String uploadDir;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Entry> getAllEntries() {
		Session s = this.factory.openSession();
		
		List<Entry> entries = s.createCriteria(Entry.class).addOrder(Order.asc("year")).list();
		
		for(Entry entry : entries){
			Hibernate.initialize(entry.getPictures());
		}
		
		s.close();
		
		return entries;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Entry> getEntriesYear(){
		List<Entry> entries = new ArrayList<Entry>();
		
		Session s = this.factory.openSession();
		
		ProjectionList proj = Projections.projectionList();
		proj.add(Projections.property("id"));
		proj.add(Projections.property("year"));
		
		List<Object[]> results = s.createCriteria(Entry.class).setProjection(proj).addOrder(Order.asc("year")).list();
		
		for (Object[] res: results){
			Entry entry = new Entry((long)res[0], (int)res[1]);
			
			entries.add(entry);
		}
		
		s.close();
		
		return entries;	
	}

	@Override
	public Entry getEntry(long id) {
		Session s = this.factory.openSession();
		
		Entry entry = (Entry) s.createCriteria(Entry.class).add(Restrictions.eq("id", id)).uniqueResult();
		
		Hibernate.initialize(entry.getPictures());
		Hibernate.initialize(entry.getFacts());
		Hibernate.initialize(entry.getStats());
		
		s.close();
		
		return entry;
	}

	@Override
	public void addEntry(Entry entry) {
		Session s = this.factory.openSession();
		
		s.beginTransaction();
		s.persist(entry);
		s.getTransaction().commit();
		
		s.close();
	}

	@Override
	public void deleteEntry(long id) {
		Session s = this.factory.openSession();
		
		s.beginTransaction();
		Entry entry = (Entry) s.load(Entry.class, id);
		
		if(entry != null){
			List<Picture> pics = entry.getPictures();
			
			if(pics.size() > 0){
				for(Picture pic : pics){
					String fileName = pic.getUrl().substring(8);
					
					File f = new File(this.uploadDir + fileName);
					f.delete();
				}
			}
			
			s.delete(entry);
		}
		
		s.getTransaction().commit();
		s.close();
	}

	@Override
	public void updateEntry(Entry entry) {
		Session s = this.factory.openSession();
		
		s.beginTransaction();
		s.update(entry);
		s.getTransaction().commit();
		
		s.close();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean isUnique(int year) {
		Session s = this.factory.openSession();
		
		List<Entry> results  = s.createCriteria(Entry.class).add(Restrictions.eq("year", year)).list();
		
		return results.size() == 0;
	}
}