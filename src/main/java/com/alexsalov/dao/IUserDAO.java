package com.alexsalov.dao;

import com.alexsalov.models.User;

public interface IUserDAO {
	public User login(String username, String password);
}