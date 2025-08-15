package be.tav.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import be.tav.dao.DAO;

public class Period {
	private int id;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean isVacation;
	
	private Set<Booking> bookings;
	
	public Period(int id, LocalDate sD, LocalDate eD, boolean v) {
		this.id = id;
		setStartDate(sD);
		setEndDate(eD);
		setIsVacation(v);
		bookings = new HashSet<>();
	}
	
	public int getId() { return this.id; }
	
	public LocalDate getStartDate() { return this.startDate; }
	
	public void setStartDate(LocalDate sD) throws IllegalArgumentException {
		if(sD == null) {
			throw new IllegalArgumentException("setStartDate a échoué: argument null.");
		}
		
		if(getEndDate() != null && getEndDate().isBefore(sD)) {
			throw new IllegalArgumentException("setStartDate a échoué: " + sD + " après " + this.endDate + ".");
		}
		
		this.startDate = sD;
	}
	
	public LocalDate getEndDate() { return this.endDate; }
	
	public void setEndDate(LocalDate eD) throws IllegalArgumentException {
		if(eD == null) {
			throw new IllegalArgumentException("setEndDate a échoué: argument null.");
		}
		
		if(getStartDate() != null && getStartDate().isAfter(eD)) {
			throw new IllegalArgumentException("setEndDate a échoué: " + eD + " avant " + getStartDate() + ".");
		}
		
		this.endDate = eD;
	}
	
	public boolean getIsVacation() { return this.isVacation; }
	
	public void setIsVacation(boolean v) { this.isVacation = v; }
	
	public HashSet<Booking> getBookings() { return new HashSet<Booking>(this.bookings); }
	
	public void addBooking(Booking b) throws IllegalArgumentException, IllegalStateException {
		if(b == null) {
			throw new IllegalArgumentException("addBooking a échoué: argument null.");
		}
		
		this.bookings.add(b);
		
		if (!this.bookings.add(b)) {
		    throw new IllegalStateException("addBooking a échoué: " + b + " est déjà dans la liste bookings.");
		}
	}
	
	public static Period getPeriod(int id, DAO<Period> periodDAO) { return periodDAO.find(id); }
	
	public static ArrayList<Period> getPeriods(DAO<Period> periodDAO) { return periodDAO.findAll(); }
	
	public static class WeekInfo {
		public int weekNumber;
		public int year;
		public LocalDate start;
		public LocalDate end;
		public WeekInfo(int weekNumber, int year, LocalDate start, LocalDate end) {
			this.weekNumber = weekNumber;
			this.year = year;
			this.start = start;
			this.end = end;
		}
		@Override
		public String toString() {
			return "Semaine " + weekNumber + " (" + start + " au " + end + ")";
		}
	}

	public static java.util.List<WeekInfo> getValidWeeks(be.tav.dao.PeriodDAO periodDAO) {
		java.util.List<Period> periods = periodDAO.findAll();
		java.util.Set<String> seen = new java.util.HashSet<>();
		java.util.List<WeekInfo> validWeeks = new java.util.ArrayList<>();
		java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.SUNDAY_START;
		for (Period p : periods) {
			LocalDate d = p.getStartDate();
			while (!d.isAfter(p.getEndDate())) {
				int week = d.get(weekFields.weekOfWeekBasedYear());
				int year = d.getYear();
				LocalDate weekStart = d.with(java.time.DayOfWeek.SUNDAY);
				LocalDate weekEnd = weekStart.plusDays(6);
				String key = year + ":" + week;
				if (!seen.contains(key)) {
					validWeeks.add(new WeekInfo(week, year, weekStart, weekEnd));
					seen.add(key);
				}
				d = weekStart.plusWeeks(1);
			}
		}
		return validWeeks;
	}
}