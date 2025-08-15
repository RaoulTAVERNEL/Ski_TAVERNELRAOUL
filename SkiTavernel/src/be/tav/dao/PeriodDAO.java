package be.tav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import be.tav.pojo.Period;

public class PeriodDAO extends DAO<Period> {
	public PeriodDAO(Connection c) {
		super(c);
	}

	@Override
	public boolean create(Period p) {
		String query = "INSERT INTO periods (periodstartdate, periodenddate, isvacation) VALUES (?, ?, ?)";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setDate(1, java.sql.Date.valueOf(p.getStartDate()));
	        stmt.setDate(2, java.sql.Date.valueOf(p.getEndDate()));
	        stmt.setInt(3, p.getIsVacation() ? 1 : 0);
	        int rowsAffected = stmt.executeUpdate();
	        
	        return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean update(Period p) {
		String query = "UPDATE periods SET periodstartdate = ?, periodenddate = ?, isvacation = ? WHERE periodid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setDate(1, java.sql.Date.valueOf(p.getStartDate()));
	        stmt.setDate(2, java.sql.Date.valueOf(p.getEndDate()));
	        stmt.setInt(3, p.getIsVacation() ? 1 : 0);
	        stmt.setInt(4, p.getId());
	        int rowsAffected = stmt.executeUpdate();
	        
	        return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean delete(Period p) {
	    String query = "DELETE FROM periods WHERE periodid = ?";
	    
	    try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
	        stmt.setInt(1, p.getId());
	        int rowsAffected = stmt.executeUpdate();
	        
	        return rowsAffected > 0;
	    } catch(SQLException e) {
	        e.printStackTrace();
	        
	        return false;
	    }
	}

	@Override
	public Period find(int id) {
		Period period = null;
		String query = "SELECT periodstartdate, periodenddate, isvacation FROM periods WHERE periodid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			
			try(ResultSet res = stmt.executeQuery()) {
				if(res.next()) {
					period = new Period(
							id, 
							res.getDate("periodstartdate").toLocalDate(), 
							res.getDate("periodenddate").toLocalDate(), 
							res.getInt("isvacation") == 1);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return period;
	}

	@Override
	public ArrayList<Period> findAll() {
		ArrayList<Period> periods = new ArrayList<>();
		String query = "SELECT periodid, periodstartdate, periodenddate, isvacation FROM periods";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query);
				ResultSet res = stmt.executeQuery()) {
			while(res.next()) {
				periods.add(new Period(
						res.getInt("periodid"), 
						res.getDate("periodstartdate").toLocalDate(), 
						res.getDate("periodenddate").toLocalDate(), 
						res.getInt("isvacation") == 1));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return periods;
	}
}
