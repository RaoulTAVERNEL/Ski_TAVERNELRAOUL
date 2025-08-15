package be.tav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import be.tav.pojo.*;

public class BookingDAO extends DAO<Booking> {
	public BookingDAO(Connection c) {
		super(c);
	}

	@Override
	public boolean create(Booking b) {
		String query = "INSERT INTO bookings (bookinginsurance, lessonid, periodid, skierid, instructorid) VALUES (?, ?, ?, ?, ?)";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setInt(1, b.getHasInsurance() ? 1 : 0);
			stmt.setInt(2, b.getLesson().getId());
			stmt.setInt(3, b.getPeriod().getId());
			stmt.setInt(4, b.getSkier().getId());
			stmt.setInt(5, b.getInstructor().getId());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean update(Booking b) {
		String query = "UPDATE bookings SET bookinginsurance = ? WHERE bookingid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setInt(1, b.getHasInsurance() ? 1 : 0);
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean delete(Booking b) {
		String query = "DELETE FROM bookings WHERE bookingid = ?";
	    
	    try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
	        stmt.setInt(1, b.getId());
	        int rowsAffected = stmt.executeUpdate();
	        
	        return rowsAffected > 0;
	    } catch(SQLException e) {
	        e.printStackTrace();
	        
	        return false;
	    }
	}

	@Override
	public Booking find(int id) {
		Booking booking = null;
		String query = "SELECT b.bookingid, b.bookinginsurance, "
							+ "l.lessonid, l.lessondate, l.isprivate, l.lessonnbhour, "
							+ "lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice, "
							+ "a.accreditationid, a.accreditationname, "
							+ "i.instructorid, p1.personlastname AS iLastname, p1.personfirstname AS iFirstname, "
							+ "p1.personemail AS iEmail, p1.personphone AS iPhone, "
							+ "s.skierid, p2.personlastname AS sLastname, p2.personfirstname AS sFirstname, "
							+ "p2.personemail AS sEmail, p2.personphone AS sPhone, s.skierbirthdate, "
							+ "p.periodid, p.periodstartdate, p.periodenddate, p.isvacation "
						+ "FROM bookings b "
							+ "INNER JOIN lessons l ON b.lessonid = l.lessonid "
							+ "INNER JOIN lessontypes lt ON l.lessontypeid = lt.lessontypeid "
							+ "INNER JOIN accreditations a ON lt.accreditationid = a.accreditationid "
							+ "INNER JOIN instructors i ON l.instructorid = i.instructorid "
							+ "INNER JOIN people p1 ON i.instructorid = p1.personid "
							+ "INNER JOIN skiers s ON b.skierid = s.skierid "
							+ "INNER JOIN people p2 ON s.skierid = p2.personid "
							+ "INNER JOIN periods p ON b.periodid = p.periodid "
						+ "WHERE b.bookingid = ?";
		
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
							res.getString("iLastname"),
							res.getString("iFirstname"),
							res.getString("iEmail"),
							res.getString("iPhone"),
							accreditation);
					LessonType lessonType = accreditation.getLessonTypes().iterator().next();
					
					Lesson lesson = new Lesson(
							res.getInt("lessonid"),
							res.getTimestamp("lessondate").toLocalDateTime(),
							res.getInt("isprivate") == 1,
							res.getInt("lessonnbhour"),
							lessonType,
							instructor);
					Skier skier = new Skier(
							res.getInt("skierid"),
							res.getString("sLastname"),
							res.getString("sFirstname"),
							res.getString("sEmail"),
							res.getString("sPhone"),
							res.getDate("skierbirthdate").toLocalDate());
					Period period = new Period(
							res.getInt("periodid"),
							res.getDate("periodstartdate").toLocalDate(),
							res.getDate("periodenddate").toLocalDate(),
							res.getInt("isvacation") == 1);
					
					booking = new Booking(
							res.getInt("bookingid"),
							res.getInt("bookinginsurance") == 1,
							period,
							skier,
							instructor,
							lesson);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return booking;
	}

	@Override
	public ArrayList<Booking> findAll() {
		ArrayList<Booking> bookings = new ArrayList<>();
	    Map<Integer, Lesson> lessonCache = new HashMap<>();
	    Map<Integer, Skier> skierCache = new HashMap<>();
	    Map<Integer,Period> periodCache = new HashMap<>();
	    String query = "SELECT b.bookingid, b.bookinginsurance, "
	    					+ "l.lessonid, l.lessondate, l.isprivate, l.lessonnbhour, "
	    					+ "lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice, "
	    					+ "a.accreditationid, a.accreditationname, "
	    					+ "i.instructorid, p1.personlastname AS iLastname, p1.personfirstname AS iFirstname, "
	    					+ "p1.personemail AS iEmail, p1.personphone AS iPhone, "
	    					+ "s.skierid, p2.personlastname AS sLastname, p2.personfirstname AS sFirstname, "
	    					+ "p2.personemail AS sEmail, p2.personphone AS sPhone, s.skierbirthdate, "
	    					+ "p.periodid, p.periodstartdate, p.periodenddate, p.isvacation "
	    				+ "FROM bookings b "
	    					+ "INNER JOIN lessons l ON b.lessonid = l.lessonid "
	    					+ "INNER JOIN lessontypes lt ON l.lessontypeid = lt.lessontypeid "
	    					+ "INNER JOIN accreditations a ON lt.accreditationid = a.accreditationid "
	    					+ "INNER JOIN instructors i ON l.instructorid = i.instructorid "
	    					+ "INNER JOIN people p1 ON i.instructorid = p1.personid "
	    					+ "INNER JOIN skiers s ON b.skierid = s.skierid "
	    					+ "INNER JOIN people p2 ON s.skierid = p2.personid "
	    					+ "INNER JOIN periods p ON b.periodid = p.periodid";

	    try(PreparedStatement stmt = this.conn.prepareStatement(query);
	         ResultSet res = stmt.executeQuery()) {
	    	while(res.next()) {
	    	    int lessonId = res.getInt("lessonid");
	    	    Lesson lesson = lessonCache.computeIfAbsent(lessonId, lessonKey -> {
	    	    	try {
	    	    		Accreditation accreditation = new Accreditation(
	    	    				res.getInt("accreditationid"),
	    	    	            res.getString("accreditationname"),
	    	    	            res.getInt("lessontypeid"),
	    	    	            res.getString("lessontypelevel"),
	    	    	            res.getFloat("lessontypeprice"));
	    	    		Instructor instructor = new Instructor(
	    	    	            res.getInt("instructorid"),
	    	    	            res.getString("iLastname"),
	    	    	            res.getString("iFirstname"),
	    	    	            res.getString("iEmail"),
	    	    	            res.getString("iPhone"),
	    	    	            accreditation);
	    	    		
	    	    		return new Lesson(
	    	    				lessonId,
	    	    	            res.getTimestamp("lessondate").toLocalDateTime(),
	    	    	            res.getInt("isprivate") == 1,
	    	    	            res.getInt("lessonnbhour"),
	    	    	            accreditation.getLessonTypes().iterator().next(),
	    	    	            instructor);
	    	    	} catch(SQLException e) {
	    	    		throw new RuntimeException(e);
	    	    	}});

	    	    int skierId = res.getInt("skierid");
	    	    Skier skier = skierCache.computeIfAbsent(skierId, skierKey -> {
					try {
						return new Skier(
								skierId,
								res.getString("sLastname"),
								res.getString("sFirstname"),
								res.getString("sEmail"),
								res.getString("sPhone"),
								res.getDate("skierbirthdate").toLocalDate());
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}});
	    	    
	    	    int periodId = res.getInt("periodid");
	    	    Period period = periodCache.computeIfAbsent(periodId, periodKey -> {
	    	    	try {
	    	    		return new Period(
	    	    				periodId,
	    	    				res.getDate("periodstartdate").toLocalDate(),
	    		    	        res.getDate("periodenddate").toLocalDate(),
	    		    	        res.getInt("isvacation") == 1);
	    	    	} catch(SQLException e) {
	    	    		throw new RuntimeException(e);
	    	    	}});
	    	    
	    	    bookings.add(new Booking(
	    	        res.getInt("bookingid"),
	    	        res.getInt("bookinginsurance") == 1,
	    	        period,
	    	        skier,
	    	        lesson.getInstructor(),
	    	        lesson
	    	    ));
	    	}
	    } catch (SQLException e) {
	        e.printStackTrace();
        }

	    return bookings;
	}
}