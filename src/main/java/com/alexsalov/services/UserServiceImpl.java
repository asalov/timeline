package com.alexsalov.services;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexsalov.dao.IUserDAO;
import com.alexsalov.models.User;

@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired
	private IUserDAO userDAO;
	
	@Override
	public boolean login(User user, HttpSession session) {
		User loggedUser = this.userDAO.login(user.getUsername(), user.getPassword());
		
		if(loggedUser != null){
			session.setAttribute("loggedUser", loggedUser);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void logout(HttpSession session) {
		session.removeAttribute("loggedUser");
	}

	@Override
	public boolean isLoggedIn(HttpSession session) {
		return session.getAttribute("loggedUser") != null;
	}
	
	@Override
	public User getLoggedInUser(HttpSession session) {
		return (User) session.getAttribute("loggedUser");
	}

}