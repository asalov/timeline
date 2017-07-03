package com.alexsalov.services;

import javax.servlet.http.HttpSession;

import com.alexsalov.models.User;

public interface IUserService {
	public boolean login(User user, HttpSession session);
	public void logout(HttpSession session);
	public boolean isLoggedIn(HttpSession session);
	public User getLoggedInUser(HttpSession session);
}