package be.tav.dao;

import java.sql.Connection;
import java.util.ArrayList;

public abstract class DAO<T> {
	protected Connection conn = null;
	
	public DAO(Connection c) {
		this.conn = c;
	}
	
	public abstract boolean create(T obj);
	
	public abstract boolean update(T obj);
	
	public abstract boolean delete(T obj);
	
	public abstract T find(int id);
	
	public abstract ArrayList<T> findAll();
}
