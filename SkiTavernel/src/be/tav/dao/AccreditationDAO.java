package be.tav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import be.tav.pojo.Accreditation;
import be.tav.pojo.LessonType;

public class AccreditationDAO extends DAO<Accreditation> {
	public AccreditationDAO(Connection c) {
		super(c);
	}

	@Override
	public boolean create(Accreditation a) {
		String query = "INSERT INTO accreditations (accreditationname) VALUES (?)";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setString(1, a.getName());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean update(Accreditation a) {
		String query = "UPDATE accreditations SET accreditationname = ? WHERE accreditationid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setString(1, a.getName());
			stmt.setInt(2, a.getId());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean delete(Accreditation a) {
		String query = "DELETE FROM accreditations WHERE accreditationid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setInt(1, a.getId());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public Accreditation find(int id) {
		Accreditation accreditation = null;
		String query = "SELECT a.accreditationname, "
							+ "lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice "
						+ "FROM accreditations a "
							+ "INNER JOIN lessontypes lt ON a.accreditationid = lt.accreditationid "
						+ "WHERE a.accreditationid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			
			try(ResultSet res = stmt.executeQuery()) {
			    if(res.next()) {
			        accreditation = new Accreditation(
			        		id,
			        		res.getString("accreditationname"),
			        		res.getInt("lessontypeid"),
			        		res.getString("lessontypelevel"),
			        		res.getFloat("lessontypeprice"));
			        
			        while(res.next()) {
			            accreditation.addLessonType(new LessonType(
			            		res.getInt("lessontypeid"),
			            		res.getString("lessontypelevel"),
			            		res.getFloat("lessontypeprice"),
			            		accreditation));
			        }
			    }
			}

		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return accreditation;
	}

	@Override
	public ArrayList<Accreditation> findAll() {
		ArrayList<Accreditation> accreditations = new ArrayList<>();
		String query = "SELECT a.accreditationid, a.accreditationname, "
							+ "LISTAGG(lt.lessontypeid || '|' || lt.lessontypelevel || '|' || lt.lessontypeprice, ',') "
								+ "WITHIN GROUP(ORDER BY lt.lessontypeid) AS lessontypes_info "
						+ "FROM accreditations a "
							+ "INNER JOIN lessontypes lt ON a.accreditationid = lt.accreditationid "
						+ "GROUP BY(a.accreditationid, a.accreditationname)";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query);
				ResultSet res = stmt.executeQuery()) {
			while(res.next()) {
		    	String[] lessonTypesInfo = res.getString("lessontypes_info").split(",");
		        String[] firstLessonType = lessonTypesInfo[0].split("\\|");
		        Accreditation a = new Accreditation(
		        		res.getInt("accreditationid"),
		        		res.getString("accreditationname"),
		        		Integer.parseInt(firstLessonType[0]),
		        		firstLessonType[1],
		        		Float.parseFloat(firstLessonType[2]));
		        
		        for(int i = 1; i < lessonTypesInfo.length; i++) {
		            String[] lessonType = lessonTypesInfo[i].split("\\|");
		            
		            a.addLessonType(new LessonType(
		            		Integer.parseInt(lessonType[0]),
		            		lessonType[1],
		            		Float.parseFloat(lessonType[2]),
		            		a));
		        }

		        accreditations.add(a);
		    }
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return accreditations;
	}
}
