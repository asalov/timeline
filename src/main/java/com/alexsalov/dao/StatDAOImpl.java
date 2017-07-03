package com.alexsalov.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alexsalov.models.Stat;

@Repository
public class StatDAOImpl implements IStatDAO {
	
	@Autowired
	private SessionFactory factory;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Stat> getAllStats(){
		Session s = this.factory.openSession();
		List<Stat> stats = s.createCriteria(Stat.class).list(); 
		
		s.close();
		
		return stats;
	}
}