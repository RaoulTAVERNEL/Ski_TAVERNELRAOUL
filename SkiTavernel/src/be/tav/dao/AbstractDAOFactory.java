package be.tav.dao;

import be.tav.pojo.*;

public abstract class AbstractDAOFactory {
	public static final int DAO_FACTORY = 0;
	public static final int XML_DAO_FACTORY = 1;
	
	public abstract DAO<Skier> getSkierDAO();
	
	public abstract DAO<Instructor> getInstructorDAO();
	
	public abstract DAO<Accreditation> getAccreditationDAO();
	
	public abstract DAO<LessonType> getLessonTypeDAO();
	
	public abstract DAO<Lesson> getLessonDAO();
	
	public abstract DAO<Booking> getBookingDAO();
	
	public abstract DAO<Period> getPeriodDAO();
	
	public static AbstractDAOFactory getFactory(int type) {
		switch(type) {
			case DAO_FACTORY:
				return new DAOFactory();
			default:
				return null;
		}
	}
}
