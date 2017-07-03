package com.alexsalov.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexsalov.dao.IStatDAO;
import com.alexsalov.models.Stat;

@Service
public class StatServiceImpl implements IStatService {
	
	@Autowired
	private IStatDAO statDAO;
	
	@Override
	public List<Stat> getAllStats(){
		return this.statDAO.getAllStats();
	}
}