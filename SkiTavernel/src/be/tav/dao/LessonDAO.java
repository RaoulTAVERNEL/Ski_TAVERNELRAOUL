package be.tav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import be.tav.pojo.Accreditation;
import be.tav.pojo.Instructor;
import be.tav.pojo.Lesson;
import be.tav.pojo.LessonType;

public class LessonDAO extends DAO<Lesson> {
	public LessonDAO(Connection c) {
		super(c);
	}

	@Override
	public boolean create(Lesson l) {
		String query = "INSERT INTO lessons (lessondate, isprivate, lessonnbhour, lessontypeid, instructorid) VALUES (?, ?, ?, ?, ?)";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setTimestamp(1, Timestamp.valueOf(l.getDate()));
			stmt.setInt(2, l.getIsPrivate() ? 1 : 0);
			stmt.setInt(3, l.getDuration());
			stmt.setInt(4, l.getLessonType().getId());
			stmt.setInt(5, l.getInstructor().getId());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean update(Lesson l) {
		String query = "UPDATE lessons SET lessondate = ?, isprivate = ?, lessonnbhour = ? WHERE lessonid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setTimestamp(1, Timestamp.valueOf(l.getDate()));
			stmt.setInt(2, l.getIsPrivate() ? 1 : 0);
			stmt.setInt(3, l.getDuration());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean delete(Lesson l) {
		String query = "DELETE FROM lessons WHERE lessonid = ?";
	    
	    try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
	        stmt.setInt(1, l.getId());
	        int rowsAffected = stmt.executeUpdate();
	        
	        return rowsAffected > 0;
	    } catch(SQLException e) {
	        e.printStackTrace();
	        
	        return false;
	    }
	}

	@Override
	public Lesson find(int id) {
		Lesson lesson = null;
		String query = "SELECT l.lessonid, l.lessondate, l.isprivate, l.lessonnbhour, "
				+ "lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice, "
				+ "a.accreditationid, a.accreditationname, "
				+ "i.instructorid, p.personlastname, p.personfirstname, p.personemail, p.personphone "
				+ "FROM lessons l "
				+ "INNER JOIN lessontypes lt ON l.lessontypeid = lt.lessontypeid "
				+ "INNER JOIN accreditations a ON lt.accreditationid = a.accreditationid "
				+ "INNER JOIN instructors i ON l.instructorid = i.instructorid "
				+ "INNER JOIN people p ON i.instructorid = p.personid "
				+ "WHERE l.lessonid = ?";
		
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
					Instructor instructor = new Instructor(
							res.getInt("instructorid"),
							res.getString("personlastname"),
							res.getString("personfirstname"),
							res.getString("personemail"),
							res.getString("personphone"),
							accreditation);
					LessonType lessonType = accreditation.getLessonTypes().iterator().next();
					
					lesson = new Lesson(
							res.getInt("lessonid"),
							res.getTimestamp("lessondate").toLocalDateTime(),
							res.getInt("isprivate") == 1,
							res.getInt("lessonnbhour"),
							lessonType,
							instructor);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return lesson;
	}

	@Override
	public ArrayList<Lesson> findAll() {
		ArrayList<Lesson> lessons = new ArrayList<>();
		String query = "SELECT l.lessonid, l.lessondate, l.isprivate, l.lessonnbhour, "
				+ "lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice, "
				+ "a.accreditationid, a.accreditationname, "
				+ "i.instructorid, p.personlastname, p.personfirstname, p.personemail, p.personphone "
				+ "FROM lessons l "
				+ "INNER JOIN lessontypes lt ON l.lessontypeid = lt.lessontypeid "
				+ "INNER JOIN accreditations a ON lt.accreditationid = a.accreditationid "
				+ "INNER JOIN instructors i ON l.instructorid = i.instructorid "
				+ "INNER JOIN people p ON i.instructorid = p.personid";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query);
				ResultSet res = stmt.executeQuery()) {
			while(res.next()) {
				Accreditation accreditation = new Accreditation(
						res.getInt("accreditationid"),
						res.getString("accreditationname"),
						res.getInt("lessontypeid"),
						res.getString("lessontypelevel"),
						res.getFloat("lessontypeprice"));
				Instructor instructor = new Instructor(
						res.getInt("instructorid"),
						res.getString("personlastname"),
						res.getString("personfirstname"),
						res.getString("personemail"),
						res.getString("personphone"),
						accreditation);
				LessonType lessonType = accreditation.getLessonTypes().iterator().next();
				Lesson lesson = new Lesson(
						res.getInt("lessonid"),
						res.getTimestamp("lessondate").toLocalDateTime(),
						res.getInt("isprivate") == 1,
						res.getInt("lessonnbhour"),
						lessonType,
						instructor);
				lessons.add(lesson);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return lessons;
	}
}