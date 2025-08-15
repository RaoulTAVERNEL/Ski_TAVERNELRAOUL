package be.tav.pojo;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import be.tav.dao.DAO;
import be.tav.exception.BusinessException;

public class Skier extends Person {
	private LocalDate birthdate;
	
	private Set<Booking> bookings;
	
	public Skier(int id, String ln, String fn, String e, String p, LocalDate b) {
		super(id, ln, fn, e, p);
		setBirthdate(b);
		bookings = new HashSet<>();		
	}
	
	public Skier(String ln, String fn, String e, String p, LocalDate b) { this(-1, ln, fn, e, p, b); }
	
	public LocalDate getBirthdate() { return this.birthdate; }
	
	private void setBirthdate(LocalDate b) throws IllegalArgumentException, BusinessException {
		if(b == null) {
			throw new IllegalArgumentException("setBirthdate a échoué: argument null/empty.");
		}
		
		LocalDate today = LocalDate.now();
		
		if(b.isAfter(today)) {
			throw new BusinessException("La date saisie est invalide.");
		}
		
		if(Period.between(b, today).getYears() < 4) {
			throw new BusinessException("L'âge de l'élève est inférieur à l'âge minimum requis.");
		}
		
		this.birthdate = b;
	}
	
	public int getAge() { return Period.between(getBirthdate(), LocalDate.now()).getYears(); }
	
	public HashSet<Booking> getBookings() { return new HashSet<Booking>(this.bookings); }
	
	public void addBooking(Booking b) {
	    if(b == null) {
	        return;
	    }
	    this.bookings.add(b);
	}
	
	public static Skier findSkier(int id, DAO<Skier> skierDAO) { return skierDAO.find(id); }
	
	public static ArrayList<Skier> findSkiers(DAO<Skier> skierDAO) { return skierDAO.findAll(); }
	
	public boolean createSkier(DAO<Skier> skierDAO) { return skierDAO.create(this); }
	
	public boolean updateSkier(DAO<Skier> skierDAO) {
		if(skierDAO.update(this)) {
			this.getBookings().forEach(booking -> booking.setSkier(this));
			
			return true;
		}
		
		return false;
	}
	
	public boolean deleteSkier(DAO<Skier> skierDAO) { return this.getBookings().isEmpty() && skierDAO.delete(this); }
	
	public java.util.List<LessonType> getEligibleLessonTypes(java.util.List<LessonType> allLessonTypes) {
        java.util.List<LessonType> eligible = new java.util.ArrayList<>();
        boolean isChild = this.getAge() < 18;
        for (LessonType lt : allLessonTypes) {
            String accName = lt.getAccreditation() != null ? lt.getAccreditation().getName().toLowerCase() : "";
            if (isChild && accName.contains("enfant")) {
                eligible.add(lt);
            } else if (!isChild && accName.contains("adulte")) {
                eligible.add(lt);
            }
        }
        return eligible;
    }

    public boolean hasBookingForLesson(Lesson lesson) {
        if (lesson == null) return false;
        for (Booking b : bookings) {
            if (b.getLesson() != null && b.getLesson().getId() == lesson.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBookingAtSlot(java.time.LocalDateTime date, LessonType type) {
        if (date == null || type == null) return false;
        for (Booking b : bookings) {
            Lesson l = b.getLesson();
            if (l != null && l.getLessonType() != null && l.getLessonType().getId() == type.getId()
                && l.getDate() != null && l.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }
}