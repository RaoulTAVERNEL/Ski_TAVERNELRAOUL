package be.tav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import be.tav.pojo.Accreditation;
import be.tav.pojo.Instructor;
import be.tav.pojo.Lesson;
import be.tav.pojo.LessonType;

public class InstructorDAO extends DAO<Instructor> {
	public InstructorDAO(Connection c) {
		super(c);
	}

	@Override
	public boolean create(Instructor i) {
		String createPerson = "INSERT INTO people (personlastname, personfirstname, personemail, personphone) VALUES (?, ?, ?, ?)";
	    String createInstructor = "INSERT INTO instructors (instructorid) VALUES (?)";
	    String createAccreditation = "INSERT INTO masters (instructorid, accreditationid) VALUES (?, ?)";
	    int rowsAffectedInstructor = 0;
        int rowsAffectedPerson = 0;
        int rowsAffectedAccreditation = 0;
	    
	    try {
	        conn.setAutoCommit(false);
	        
	        try(PreparedStatement stmtPerson = conn.prepareStatement(createPerson, PreparedStatement.RETURN_GENERATED_KEYS)) {
	            stmtPerson.setString(1, i.getLastname());
	            stmtPerson.setString(2, i.getFirstname());
	            stmtPerson.setString(3, i.getEmail());
	            stmtPerson.setString(4, i.getPhone());
	            rowsAffectedPerson = stmtPerson.executeUpdate();
	            
	            try(ResultSet rs = stmtPerson.getGeneratedKeys()) {
	                if(rs.next()) {
	                    int personId = rs.getInt(1);
	                
	                    try(PreparedStatement stmtInstructor = conn.prepareStatement(createInstructor);
	                    		PreparedStatement stmtAccreditation = conn.prepareStatement(createAccreditation)) {
	                    	stmtInstructor.setInt(1, personId);
	                        rowsAffectedInstructor = stmtInstructor.executeUpdate();
	                        
	                        stmtAccreditation.setInt(1, personId);
	                        stmtAccreditation.setInt(2, i.getAccreditations().iterator().next().getId());
	                        rowsAffectedAccreditation = stmtAccreditation.executeUpdate();
	                    }
	                    
	                    if(rowsAffectedPerson > 0 && rowsAffectedInstructor > 0 && rowsAffectedAccreditation > 0) {
	                        conn.commit();
	                        
	                        return true;
	                    } else {
	                        conn.rollback();
	                        
	                        return false;
	                    }
	                }
	            }
	        } catch(SQLException e) {
	            conn.rollback();
	            e.printStackTrace();
	            
	            return false;
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
	    
	    return false;
	}

	@Override
	public boolean update(Instructor i) {
		String query = "UPDATE people SET personlastname = ?, personfirstname = ?, personemail = ?, personphone = ? WHERE personid = ?";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setString(1, i.getLastname());
			stmt.setString(2, i.getFirstname());
			stmt.setString(3, i.getEmail());
			stmt.setString(4, i.getPhone());
			stmt.setInt(5, i.getId());
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	@Override
	public boolean delete(Instructor i) {
		String query = "DELETE FROM people WHERE personid = ?";
	    
	    try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
	        stmt.setInt(1, i.getId());
	        int rowsAffected = stmt.executeUpdate();
	        
	        return rowsAffected > 0;
	    } catch(SQLException e) {
	        e.printStackTrace();
	        
	        return false;
	    }
	}

	@Override
	public Instructor find(int id) {
		Instructor instructor = null;
		String query = "SELECT p.personlastname, p.personfirstname, p.personemail, p.personphone, "
							+ "a.accreditationid, a.accreditationname, "
							+ "LISTAGG(lt.lessontypeid || '|' || lt.lessontypelevel || '|' || lt.lessontypeprice, ',') "
								+ "WITHIN GROUP(ORDER BY lt.lessontypeid) AS lessontypes_info, "
							+ "l.lessonid, l.lessondate, l.lessonnbhour, l.isprivate "
						+ "FROM instructors i "
							+ "INNER JOIN people p ON i.instructorid = p.personid "
							+ "INNER JOIN masters m ON i.instructorid = m.instructorid "
							+ "INNER JOIN accreditations a ON m.accreditationid = a.accreditationid "
							+ "INNER JOIN lessontypes lt ON a.accreditationid = lt.accreditationid "
							+ "LEFT JOIN lessons l ON i.instructorid = l.instructorid AND lt.lessontypeid = l.lessontypeid "
						+ "WHERE i.instructorid = ? "
						+ "GROUP BY(p.personlastname, p.personfirstname, p.personemail, p.personphone, i.instructorid, "
							+ "a.accreditationid, a.accreditationname, l.lessonid, l.lessondate, l.lessonnbhour, l.isprivate)";
		
		try(PreparedStatement stmt = this.conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			
			try(ResultSet res = stmt.executeQuery()) {
				while(res.next()) {
					if(res.getInt("lessonid") != 0) {
						String[] lessonType_info = res.getString("lessontypes_info").split("\\|");
						
						if(instructor != null) {
							LessonType lessonType = instructor.getAccreditations().stream()
									.flatMap(accreditation -> accreditation.getLessonTypes().stream())
			                	    .filter(lt -> lt.getId() == Integer.parseInt(lessonType_info[0]))
			                	    .findFirst()
			                	    .orElse(null);
							
							if(lessonType == null) {
								int aId = res.getInt("accreditationid");
								Accreditation accreditation = instructor.getAccreditations().stream()
								        .filter(a -> a.getId() == aId)
								        .findFirst()
								        .orElse(null);
								
								if(accreditation != null) {
									lessonType = new LessonType(
											Integer.parseInt(lessonType_info[0]),
											lessonType_info[1],
											Float.parseFloat(lessonType_info[2]),
											accreditation);
									accreditation.addLessonType(lessonType);
								} else {
									accreditation = new Accreditation(
											res.getInt("accreditationid"),
											res.getString("accreditationname"),
											Integer.parseInt(lessonType_info[0]),
											lessonType_info[1],
											Float.parseFloat(lessonType_info[2]));
									lessonType = accreditation.getLessonTypes().iterator().next();
								}
							}
							
							instructor.addLesson(new Lesson(
									res.getInt("lessonid"),
									res.getTimestamp("lessondate").toLocalDateTime(),
									res.getInt("isprivate") == 1,
									res.getInt("lessonnbhour"),
									lessonType,
									instructor));
						} else {
							Accreditation accreditation = new Accreditation(
									res.getInt("accreditationid"),
									res.getString("accreditationname"),
									Integer.parseInt(lessonType_info[0]),
									lessonType_info[1],
									Float.parseFloat(lessonType_info[2]));
							LessonType lessonType = accreditation.getLessonTypes().iterator().next();
							instructor = new Instructor(
									id,
									res.getString("personlastname"),
									res.getString("personfirstname"),
									res.getString("personemail"),
									res.getString("personphone"),
									accreditation);
							Lesson lesson = new Lesson(
									res.getInt("lessonid"),
									res.getTimestamp("lessondate").toLocalDateTime(),
									res.getInt("isprivate") == 1,
									res.getInt("lessonnbhour"),
									lessonType,
									instructor);
							
							instructor.addLesson(lesson);
						}
					} else {
						String[] lessonTypes_info = res.getString("lessontypes_info").split(",");
						String[] firstLessonType = lessonTypes_info[0].split("\\|");
						
						Accreditation accreditation = new Accreditation(
								res.getInt("accreditationid"),
								res.getString("accreditationname"),
								Integer.parseInt(firstLessonType[0]),
								firstLessonType[1],
								Float.parseFloat(firstLessonType[2]));
						
						for(int i = 1; i < lessonTypes_info.length; i++) {
							String[] lessonType = lessonTypes_info[i].split("\\|");
							accreditation.addLessonType(new LessonType(
									Integer.parseInt(lessonType[0]),
									lessonType[1],
									Float.parseFloat(lessonType[2]),
									accreditation));
						}
						
						if(instructor == null) {
							instructor = new Instructor(
									id,
									res.getString("personlastname"),
									res.getString("personfirstname"),
									res.getString("personemail"),
									res.getString("personphone"),
									accreditation);
						} else {
							instructor.addAccreditation(accreditation);
						}
					}
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return instructor;
	}

	@Override
	public ArrayList<Instructor> findAll() {
        ArrayList<Instructor> instructors = new ArrayList<>();
        String query = "SELECT i.instructorid, p.personlastname, p.personfirstname, p.personemail, p.personphone, "
                + "a.accreditationid, a.accreditationname, "
                + "lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice "
                + "FROM instructors i "
                + "INNER JOIN people p ON i.instructorid = p.personid "
                + "INNER JOIN masters m ON i.instructorid = m.instructorid "
                + "INNER JOIN accreditations a ON m.accreditationid = a.accreditationid "
                + "INNER JOIN lessontypes lt ON a.accreditationid = lt.accreditationid ";

        try (PreparedStatement stmt = this.conn.prepareStatement(query);
             ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                int instructorId = res.getInt("instructorid");
                Instructor instructor = instructors.stream()
                        .filter(i -> i.getId() == instructorId)
                        .findFirst()
                        .orElse(null);
                Accreditation accreditation = new Accreditation(
                        res.getInt("accreditationid"),
                        res.getString("accreditationname"),
                        res.getInt("lessontypeid"),
                        res.getString("lessontypelevel"),
                        res.getFloat("lessontypeprice"));
                if (instructor == null) {
                    instructor = new Instructor(
                            instructorId,
                            res.getString("personlastname"),
                            res.getString("personfirstname"),
                            res.getString("personemail"),
                            res.getString("personphone"),
                            accreditation);
                    instructors.add(instructor);
                } else {
                    boolean exists = instructor.getAccreditations().stream()
                            .anyMatch(a -> a.getId() == accreditation.getId());
                    if (!exists) {
                        instructor.addAccreditation(accreditation);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }
	
	public ArrayList<Instructor> searchForInstructors(String timeslot, int aId, String iLastname) {
	    ArrayList<Instructor> instructors = new ArrayList<>();
	    StringBuilder query = new StringBuilder(
	        "WITH partitionedInstructors AS ("
	        		+ "SELECT i.instructorid, p.personlastname, p.personfirstname, p.personemail, p.personphone, "
	        			+ "a.accreditationid, a.accreditationname, "
	        			+ "lt.lessontypeid, lt.lessontypelevel, lt.lessontypeprice, "
	        		+ "ROW_NUMBER() OVER (PARTITION BY i.instructorid ORDER BY i.instructorid) AS rn "
	        		+ "FROM instructors i "
	        			+ "INNER JOIN people p ON i.instructorid = p.personid "
	        			+ "INNER JOIN lessons l ON i.instructorid = l.instructorid "
	        			+ "INNER JOIN lessontypes lt ON l.lessontypeid = lt.lessontypeid "
	        			+ "INNER JOIN accreditations a ON lt.accreditationid = a.accreditationid "
	        		+ "WHERE 1 = 1 ");
	    ArrayList<Object> parameters = new ArrayList<>();
	    
	    if(timeslot != null && !timeslot.isEmpty()) {
	        query.append("AND l.lessondate NOT LIKE ? ");
	        parameters.add(timeslot + "%");
	    }
	    
	    if(aId != 0) {
	        query.append("AND a.accreditationid = ? ");
	        parameters.add(aId);
	    }
	    
	    if(iLastname != null && !iLastname.isEmpty()) {
	        query.append("AND p.personlastname LIKE ? ");
	        parameters.add(iLastname + "%");
	    }

	    query.append(") SELECT instructorid, personlastname, personfirstname, personemail, personphone, "
	    					+ "accreditationid, accreditationname, "
	    					+ "lessontypeid, lessontypelevel, lessontypeprice "
	    				+ "FROM partitionedInstructors "
	    				+ "WHERE rn = ?");

	    parameters.add(1);

	    try(PreparedStatement stmt = this.conn.prepareStatement(query.toString())) {
	        for(int i = 0; i < parameters.size(); i++) {
	            stmt.setObject(i + 1, parameters.get(i));
	        }

	        try(ResultSet res = stmt.executeQuery()) {
	            while(res.next()) {
	            	instructors.add(new Instructor(
							res.getInt("instructorid"),
							res.getString("personlastname"),
							res.getString("personfirstname"),
							res.getString("personemail"),
							res.getString("personphone"),
							new Accreditation(
									res.getInt("accreditationid"),
									res.getString("accreditationname"),
									res.getInt("lessontypeid"),
									res.getString("lessontypelevel"),
									res.getFloat("lessontypeprice"))));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return instructors;
	}
}