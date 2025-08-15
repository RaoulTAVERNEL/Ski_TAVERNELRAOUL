package be.tav.pojo;

public class Booking {
	private int id;
	private boolean hasInsurance;
	
	private Period period;
	private Skier skier;
	private Instructor instructor;
	private Lesson lesson;
	
	public Booking(int id, boolean hasI, Period p, Skier s, Instructor i, Lesson l) {
		this.id = id;
		setHasInsurance(hasI);
		setPeriod(p);
		setSkier(s);
		setInstructor(i);
		setLesson(l);
	}
	
	public int getId() { return this.id; }
	
	public boolean getHasInsurance() { return this.hasInsurance; }
	
	public void setHasInsurance(boolean hasI) { this.hasInsurance = hasI; }
	
	public Period getPeriod() { return this.period; }
	
	public void setPeriod(Period p) throws IllegalArgumentException {
		if(p == null) {
			throw new IllegalArgumentException("setPeriod a échoué: argument null.");
		}
		
		this.period = p;
	}
	
	public Skier getSkier() { return this.skier; }
	
	public void setSkier(Skier s) throws IllegalArgumentException {
		if(s == null) {
			throw new IllegalArgumentException("setSkier a échoué: argument null.");
		}
		
		this.skier = s;
	}
	
	public Instructor getInstructor() { return this.instructor; }
	
	public void setInstructor(Instructor i) throws IllegalArgumentException {
		if(i == null) {
			throw new IllegalArgumentException("setInstructor a échoué: argument null.");
		}
		
		this.instructor = i;
	}
	
	public Lesson getLesson() { return this.lesson; }
	
	public void setLesson(Lesson l) throws IllegalArgumentException {
		if(l == null) {
			throw new IllegalArgumentException("setLesson a échoué: argument null.");
		}
		
		this.lesson = l;
	}
	
	public static boolean createBooking(Skier skier, Lesson lesson, boolean isCollectif, boolean insurance) {
        if (skier == null || lesson == null) return false;
        be.tav.dao.DAOFactory factory = new be.tav.dao.DAOFactory();
        be.tav.dao.BookingDAO bookingDAO = (be.tav.dao.BookingDAO) factory.getBookingDAO();
        be.tav.dao.PeriodDAO periodDAO = (be.tav.dao.PeriodDAO) factory.getPeriodDAO();
        java.time.LocalDate lessonDate = lesson.getDate().toLocalDate();
        Period periodForLesson = null;
        for (Period p : periodDAO.findAll()) {
            if ((lessonDate.isEqual(p.getStartDate()) || lessonDate.isAfter(p.getStartDate())) &&
                (lessonDate.isEqual(p.getEndDate()) || lessonDate.isBefore(p.getEndDate()))) {
                periodForLesson = p;
                break;
            }
        }
        if (periodForLesson == null) return false;
        Booking booking = new Booking(0, insurance, periodForLesson, skier, lesson.getInstructor(), lesson);
        return bookingDAO.create(booking);
    }
}