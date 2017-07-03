package com.alexsalov.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alexsalov.models.User;

@Repository
public class UserDAOImpl implements IUserDAO {
	
	@Autowired
	private SessionFactory factory;
	
	@Override
	public User login(String username, String password) {
		Session s = this.factory.openSession();
		
		User user = (User) s.createCriteria(User.class)
					.add(Restrictions.eq("username", username))
					.uniqueResult();
		
		s.close();
		
		if(user != null && BCrypt.checkpw(password, user.getPassword())) return user;
		
		return null;
	}
}