package be.tav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import be.tav.pojo.*;

public class SkierDAO extends DAO<Skier> {
	public SkierDAO(Connection c) {
		super(c);
	}
	
	@Override
	public boolean create(Skier s) {
	    String createPerson = "INSERT INTO people (personlastname, personfirstname, personemail, personphone) VALUES (?, ?, ?, ?)";
	    String createSkier = "INSERT INTO skiers (skierid, skierbirthdate) VALUES (?, ?)";

	    try {
	        conn.setAutoCommit(false);

	        try(PreparedStatement stmtPerson = this.conn.prepareStatement(createPerson, new String[] { "personid" })) {
	            stmtPerson.setString(1, s.getLastname());
	            stmtPerson.setString(2, s.getFirstname());
	            stmtPerson.setString(3, s.getEmail());
	            stmtPerson.setString(4, s.getPhone());

	            int rowsAffectedPerson = stmtPerson.executeUpdate();

	            if(rowsAffectedPerson > 0) {
	                try(ResultSet rs = stmtPerson.getGeneratedKeys()) {
	                    if(rs.next()) {
	                        int personId = rs.getInt(1);

	                        try(PreparedStatement stmtSkier = this.conn.prepareStatement(createSkier)) {
	                            stmtSkier.setInt(1, personId);
	                            stmtSkier.setDate(2, java.sql.Date.valueOf(s.getBirthdate()));

	                            int rowsAffectedSkier = stmtSkier.executeUpdate();

	                            if(rowsAffectedSkier > 0) {
	                                conn.commit();
	                                
	                                return true;
	                            }
	                        }
	                    }
	                }
	            }

	            conn.rollback();
	            
	            return false;

	        } catch (SQLException e) {
	            conn.rollback();
	            throw e;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        
	        return false;

	    } finally {
	        try {
	            conn.setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

	@Override
	public boolean update(Skier s) {
        String updateSkier = "UPDATE skiers SET skierbirthdate = ? WHERE skierid = ?";
        String updatePerson = "UPDATE people SET personlastname = ?, personfirstname = ?, personemail = ?, personphone = ? WHERE personid = ?";
        int rowsAffectedSkier = 0;
        int rowsAffectedPerson = 0;

        try {
            conn.setAutoCommit(false);

            try(PreparedStatement stmtSkier = this.conn.prepareStatement(updateSkier);
                 PreparedStatement stmtPerson = this.conn.prepareStatement(updatePerson)) {
                stmtSkier.setDate(1, java.sql.Date.valueOf(s.getBirthdate()));
                stmtSkier.setInt(2, s.getId());
                rowsAffectedSkier = stmtSkier.executeUpdate();
                stmtPerson.setString(1, s.getLastname());
                stmtPerson.setString(2, s.getFirstname());
                stmtPerson.setString(3, s.getEmail());
                stmtPerson.setString(4, s.getPhone());
                stmtPerson.setInt(5, s.getId());
                rowsAffectedPerson = stmtPerson.executeUpdate();

                if(rowsAffectedSkier > 0 && rowsAffectedPerson > 0) {
                    conn.commit();
                    
                    return true;
                } else {
                    conn.rollback();
                    
                    return false;
                }
            } catch(SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch(SQLException e) {
        	e.printStackTrace();
        	
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch(SQLException e) {
            	e.printStackTrace();
            }
        }
    }
	
	@Override
	public boolean delete(Skier s) {
	    String query = "DELETE FROM people WHERE personid = ?";
	    
	    try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
	        stmt.setInt(1, s.getId());
	        int rowsAffected = stmt.executeUpdate();
	        
	        return rowsAffected > 0;
	    } catch(SQLException e) {
	        e.printStackTrace();
	        
	        return false;
	    }
	}
	
	@Override
	public Skier find(int id) {
		Skier skier = null;
		Map<Integer, Period> periodCache = new HashMap<>();
	    String query = "SELECT p1.personlastname AS sLastname, p1.personfirstname AS sFirstname, "
	    					+ "p1.personemail AS sEmail, p1.personphone AS sPhone, s.skierbirthdate, "
	    					+ "b.bookingid, b.bookinginsurance, "
	    					+ "p.periodid, p.periodstartdate, p.periodenddate, p.isvacation, "
	    					+ "l.lessonid, l.lessondate, l.isprivate, l.lessonnbhour, "
	    					+ "lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice, "
	    					+ "a.accreditationid, a.accreditationname, "
	    					+ "i.instructorid, p2.personlastname AS iLastname, p2.personfirstname AS iFirstname, "
	    					+ "p2.personemail AS iEmail, p2.personphone AS iPhone "
	    				+ "FROM skiers s "
	    					+ "INNER JOIN people p1 ON s.skierid = p1.personid "
	    					+ "LEFT JOIN bookings b ON s.skierid = b.skierid "
	    					+ "LEFT JOIN periods p ON b.periodid = p.periodid "
	    					+ "LEFT JOIN lessons l ON b.lessonid = l.lessonid "
	    					+ "LEFT JOIN lessontypes lt ON l.lessontypeid = lt.lessontypeid "
	    					+ "LEFT JOIN accreditations a ON lt.accreditationid = a.accreditationid "
	    					+ "LEFT JOIN instructors i ON b.instructorid = i.instructorid "
	    					+ "LEFT JOIN people p2 ON i.instructorid = p2.personid "
	    				+ "WHERE s.skierid = ?";
	    
	    try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
	    	stmt.setInt(1, id);
	    	
	    	try(ResultSet res = stmt.executeQuery()) {
	    		if(res.next()) {
	    			skier = new Skier(
	    					id,
	    					res.getString("sLastname"),
	    					res.getString("sFirstname"),
	    					res.getString("sEmail"),
	    					res.getString("sPhone"),
	    					res.getDate("skierbirthdate").toLocalDate());
	    			if(res.getInt("bookingid") != 0) {
	    				do {
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
	    					Lesson lesson = new Lesson(
	    							res.getInt("lessonid"), 
	    							res.getTimestamp("lessondate").toLocalDateTime(), 
	    							res.getInt("isprivate") == 1,
	    							res.getInt("lessonnbhour"), 
	    							accreditation.getLessonTypes().iterator().next(), 
	    							instructor);
	    					Booking booking = new Booking(
	    							res.getInt("bookingid"), 
	    							res.getInt("bookinginsurance") == 1, 
	    							period, 
	    							skier, 
	    							instructor, 
	    							lesson);
	    					
	    					skier.addBooking(booking);
	    				} while(res.next());
	    			}
	    		}
	    	}
	    } catch(SQLException e) {
	    	e.printStackTrace();
	    }
		
	    return skier;
	}
	
	@Override
	public ArrayList<Skier> findAll() {
		ArrayList<Skier> skiers = new ArrayList<>();
		String query = "SELECT s.skierid, p.personlastname, p.personfirstname, p.personemail, p.personphone, s.skierbirthdate "
						+ "FROM skiers s "
							+ "INNER JOIN people p ON s.skierid = p.personid";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query);
				ResultSet res = stmt.executeQuery()) {
			while(res.next()) {
				skiers.add(new Skier(
    					res.getInt("skierid"),
    					res.getString("personlastname"),
    					res.getString("personfirstname"),
    					res.getString("personemail"),
    					res.getString("personphone"),
    					res.getDate("skierbirthdate").toLocalDate()));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return skiers;
	}
}
