package be.tav.dao;

import java.sql.Connection;
import be.tav.connection.SkiConnection;
import be.tav.pojo.*;

public class DAOFactory extends AbstractDAOFactory {
	protected static final Connection conn = SkiConnection.getInstance();
	
	@Override
	public DAO<Skier> getSkierDAO() { return new SkierDAO(conn); }

	@Override
	public DAO<Instructor> getInstructorDAO() { return new InstructorDAO(conn); }

	@Override
	public DAO<Accreditation> getAccreditationDAO() { return new AccreditationDAO(conn); }

	@Override
	public DAO<LessonType> getLessonTypeDAO() { return new LessonTypeDAO(conn); }

	@Override
	public DAO<Lesson> getLessonDAO() { return new LessonDAO(conn); }

	@Override
	public DAO<Booking> getBookingDAO() { return new BookingDAO(conn); }

	@Override
	public DAO<Period> getPeriodDAO() { return new PeriodDAO(conn); }
}
