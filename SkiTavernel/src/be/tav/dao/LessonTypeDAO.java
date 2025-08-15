package be.tav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import be.tav.pojo.Accreditation;
import be.tav.pojo.LessonType;

public class LessonTypeDAO extends DAO<LessonType>{
	public LessonTypeDAO(Connection c) {
		super(c);
	}

	@Override
	public boolean create(LessonType lt) {
		String query = "INSERT INTO lessontypes (lessontypelevel, lessontypeprice, accreditationid) VALUES (?, ?, ?)";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setString(1, lt.getLevel());
			stmt.setFloat(2, lt.getPrice());
			stmt.setInt(3, lt.getAccreditation().getId());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean update(LessonType lt) {
		String query = "UPDATE lessontypes SET lessontypelevel = ?, lessontypeprice = ? WHERE lessontypeid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setString(1, lt.getLevel());
			stmt.setFloat(2, lt.getPrice());
			stmt.setInt(3, lt.getId());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean delete(LessonType lt) {
		String query = "DELETE FROM lessontypes WHERE lessontypeid = ?";
	    
	    try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
	        stmt.setInt(1, lt.getId());
	        int rowsAffected = stmt.executeUpdate();
	        
	        return rowsAffected > 0;
	    } catch(SQLException e) {
	        e.printStackTrace();
	        
	        return false;
	    }
	}

	@Override
	public LessonType find(int id) {
		LessonType lessonType = null;
		String query = "SELECT lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice, "
				+ "a.accreditationid, a.accreditationname "
				+ "FROM lessontypes lt "
				+ "INNER JOIN accreditations a ON lt.accreditationid = a.accreditationid "
				+ "WHERE lt.lessontypeid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			
			try(ResultSet res = stmt.executeQuery()) {
				if(res.next()) {
					Accreditation accreditation = new Accreditation(
							res.getInt("accreditationid"),
							res.getString("accreditationname"),
							res.getInt("lessontypeid"),
							res.getString("lessontypelevel"),
							res.getFloat("lessontypeprice"));
					lessonType = accreditation.getLessonTypes().iterator().next();
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return lessonType;
	}

	@Override
	public ArrayList<LessonType> findAll() {
		ArrayList<LessonType> lessonTypes = new ArrayList<>();
		String query = "SELECT LISTAGG(lt.lessontypeid || '|' || lt.lessontypelevel || '|' || lt.lessontypeprice, ',') "
				+ "WITHIN GROUP(ORDER BY lt.lessontypeid) AS lessontypes_info, "
				+ "a.accreditationid, a.accreditationname "
				+ "FROM lessontypes lt "
				+ "INNER JOIN accreditations a ON lt.accreditationid = a.accreditationid "
				+ "GROUP BY(a.accreditationid, a.accreditationname)";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query);
				ResultSet res = stmt.executeQuery()) {
			while(res.next()) {
				String[] lessonTypesInfo = res.getString("lessontypes_info").split(",");
		        String[] firstLessonType = lessonTypesInfo[0].split("\\|");
		        Accreditation accreditation = new Accreditation(
		        		res.getInt("accreditationid"),
		        		res.getString("accreditationname"),
		        		Integer.parseInt(firstLessonType[0]),
		        		firstLessonType[1],
		        		Float.parseFloat(firstLessonType[2]));
		        
		        lessonTypes.add(accreditation.getLessonTypes().iterator().next());
		        
		        for(int i = 1; i < lessonTypesInfo.length; i++) {
		            String[] lt = lessonTypesInfo[i].split("\\|");
		            
		            lessonTypes.add(new LessonType(
		            		Integer.parseInt(lt[0]),
		            		lt[1],
		            		Float.parseFloat(lt[2]),
		            		accreditation));
		        }
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return lessonTypes;
	}

}
