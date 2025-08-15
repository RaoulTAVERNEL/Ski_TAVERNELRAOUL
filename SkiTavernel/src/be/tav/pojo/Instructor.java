package be.tav.pojo;

import java.util.HashSet;
import java.util.Set;

public class Instructor extends Person {
	private Set<Accreditation> accreditations;
	private Set<Lesson> lessons;
	private Set<Booking> bookings;
	
	public Instructor(int id, String ln, String fn, String e, String p, Accreditation a) {
		super(id, ln, fn, e, p);
		accreditations = new HashSet<>();
		addAccreditation(a);
		lessons = new HashSet<>();
		bookings = new HashSet<>();
	}
	
	public HashSet<Accreditation> getAccreditations() { return new HashSet<Accreditation>(this.accreditations); }
	
	public void addAccreditation(Accreditation a) throws IllegalArgumentException, IllegalStateException {
		if(a == null) {
			throw new IllegalArgumentException("addAccreditation a échoué: argument null.");
		}
		
		if (!this.accreditations.add(a)) {
		    throw new IllegalStateException("addAccreditation a échoué: " + a + " est déjà dans la liste accreditations.");
		}
	}
	
	public HashSet<Lesson> getLessons() { return new HashSet<Lesson>(this.lessons); }
	
	public void addLesson(Lesson l) throws IllegalArgumentException, IllegalStateException {
		if(l == null) {
			throw new IllegalArgumentException("addLesson a échoué: argument null.");
		}
		
		if (!this.lessons.add(l)) {
		    throw new IllegalStateException("addLesson a échoué: " + l + " est déjà dans la liste lessons.");
		}
	}
	
	public HashSet<Booking> getBookings() { return new HashSet<Booking>(this.bookings); }
	
	public void addBooking(Booking b) throws IllegalArgumentException, IllegalStateException {
		if(b == null) {
			throw new IllegalArgumentException("addBooking a échoué: argument null.");
		}
		
		if (!this.bookings.add(b)) {
		    throw new IllegalStateException("addBooking a échoué: " + b + " est déjà dans la liste bookings.");
		}
	}
	
    public static Instructor findInstructor(int id, be.tav.dao.DAO<Instructor> instructorDAO) {
        return instructorDAO.find(id);
    }

    public static java.util.ArrayList<Instructor> findInstructors(be.tav.dao.DAO<Instructor> instructorDAO) {
        return instructorDAO.findAll();
    }

    public boolean hasAccreditationFor(LessonType lessonType) {
        if (lessonType == null || lessonType.getAccreditation() == null) return false;
        for (Accreditation acc : this.accreditations) {
            if (acc.getName().equalsIgnoreCase(lessonType.getAccreditation().getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isAvailableAt(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return false;
        for (Lesson lesson : this.lessons) {
            if (lesson.getDate() != null && lesson.getDate().equals(dateTime)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAvailable(java.time.LocalDateTime date, int duration) {
        for (Lesson lesson : lessons) {
            java.time.LocalDateTime start = lesson.getDate();
            java.time.LocalDateTime end = start.plusHours(lesson.getDuration());
            java.time.LocalDateTime newStart = date;
            java.time.LocalDateTime newEnd = date.plusHours(duration);
            if (newStart.isBefore(end) && newEnd.isAfter(start)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAvailableForCollective(java.time.LocalDate startDate, String timeSlot) {
        java.time.LocalDate endDate = startDate.plusDays(6);
        for (Lesson lesson : this.lessons) {
            java.time.LocalDate lessonDate = lesson.getDate().toLocalDate();
            if (!lesson.getIsPrivate() && (lessonDate.isBefore(startDate) || lessonDate.isAfter(endDate))) {
                continue;
            }
            
            String slot = getTimeSlotFromLesson(lesson);
            if (slot.equals(timeSlot)) {
                return false;
            }
        }
        return true;
    }

    private String getTimeSlotFromLesson(Lesson lesson) {
        java.time.LocalTime time = lesson.getDate().toLocalTime();
        if (time.isBefore(java.time.LocalTime.NOON)) return "Matin";
        else return "Après-midi";
    }
}