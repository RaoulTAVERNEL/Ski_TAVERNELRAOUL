package be.tav.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Lesson {
	private int id;
	private LocalDateTime date;
	private boolean isPrivate;
	private int duration;
	
	private LessonType lessonType;
	private Instructor instructor;
	private List<Booking> bookings;
	
	public Lesson(int id, LocalDateTime d, boolean isP, int dur, LessonType lt, Instructor i) {
        System.out.println("[DEBUG] Lesson constructor: id=" + id + ", date=" + d + ", isPrivate=" + isP + ", duration=" + dur + ", lessonType=" + (lt != null ? lt.getId() : "null") + ", instructor=" + (i != null ? i.getId() : "null"));
        this.id = id;
        setDate(d);
        setLessonType(lt);
        setInstructor(i);
        setIsPrivate(isP);
        setDuration(dur);
        bookings = new ArrayList<>();
    }
	
	public int getId() { return this.id; }
	
    public int getMinBookings() {
        if (getIsPrivate()) return 1;
        String accName = lessonType != null && lessonType.getAccreditation() != null ? lessonType.getAccreditation().getName().toLowerCase() : "";
        if (accName.contains("enfant") || accName.contains("snowboard")) return 5;
        if (accName.contains("adulte")) return 6;
        if (accName.contains("compétition") || accName.contains("hors-piste")) return 5;
        return 0;
    }
    public int getMaxBookings() {
        if (getIsPrivate()) return 4;
        String accName = lessonType != null && lessonType.getAccreditation() != null ? lessonType.getAccreditation().getName().toLowerCase() : "";
        if (accName.contains("enfant") || accName.contains("snowboard")) return 8;
        if (accName.contains("adulte")) return 10;
        if (accName.contains("compétition") || accName.contains("hors-piste")) return 8;
        return 0;
    }
	
	public LocalDateTime getDate() { return this.date; }
	
	public void setDate(LocalDateTime d) throws IllegalArgumentException {
		if(d == null) {
			throw new IllegalArgumentException("setDate a échoué: argument null.");
		}
		
		this.date = d;
	}
	
	public boolean getIsPrivate() { return this.isPrivate; }
	
	public void setIsPrivate(boolean isP) {
        this.isPrivate = isP;
    }
	
	public int getDuration() { return this.duration; }
	
	public void setDuration(int d) throws IllegalArgumentException {
        if(d == 0) {
            throw new IllegalArgumentException("setDuration a échoué: argument à 0.");
        }
        Set<Integer> validDurations = Set.of(1, 2, 18);
        if(!validDurations.contains(d)) {
            throw new IllegalArgumentException("setDuration a échoué: " + d + " n'est pas une durée valide pour ce cours.");
        }
        this.duration = d;
    }
	
	public LessonType getLessonType() { return this.lessonType; }
	
	public void setLessonType(LessonType lt) throws IllegalArgumentException {
		if(lt == null) {
			throw new IllegalArgumentException("setLessonType a échoué: argument null.");
		}
		
		this.lessonType = lt;
	}
	
	public Instructor getInstructor() { return this.instructor; }
	
	public void setInstructor(Instructor i) throws IllegalArgumentException {
		if(i == null) {
			throw new IllegalArgumentException("setInstructor a échoué: argument null.");
		}
		
		this.instructor = i;
	}
	
	public ArrayList<Booking> getBookings() { return new ArrayList<Booking>(bookings); }
	
	public void addBooking(Booking b) throws IllegalArgumentException, IllegalStateException {
		if(b == null) {
			throw new IllegalArgumentException("addBooking a échoué: argument null.");
		}
		
		if (!this.bookings.add(b)) {
		    throw new IllegalStateException("addBooking a échoué: " + b + " est déjà dans la liste bookings.");
		}
	}

    public String getTimeSlot() {
        java.time.LocalTime time = this.getDate().toLocalTime();
        if (this.getIsPrivate() && time.getHour() == 12) {
            if (this.getDuration() == 1) return "Déjeuner (1h, privé)";
            if (this.getDuration() == 2) return "Déjeuner (2h, privé)";
        }
        if (time.isBefore(java.time.LocalTime.NOON)) return "Matin";
        if (time.isAfter(java.time.LocalTime.of(13, 0)) && time.isBefore(java.time.LocalTime.of(15, 0))) return "Après-midi";
        return "?";
    }

    public static java.util.List<Lesson> findAvailableLessons(java.util.List<Lesson> allLessons, LessonType type, java.time.LocalDate date, String timeSlot, boolean isIndividual) {
        java.util.List<Lesson> available = new java.util.ArrayList<>();
        for (Lesson lesson : allLessons) {
            if (lesson.getLessonType() == null || lesson.getLessonType().getId() != type.getId()) continue;
            if (lesson.getDate() == null || !lesson.getDate().toLocalDate().equals(date)) continue;
            String slot = lesson.getTimeSlot();
            if (!slot.equals(timeSlot)) continue;
            int max = lesson.getMaxBookings();
            int current = lesson.getBookings() != null ? lesson.getBookings().size() : 0;
            if (isIndividual) {
                if (!lesson.getIsPrivate()) continue;
                if (max != 4) continue;
                if (lesson.getDate().toLocalTime().getHour() != 12) continue;
                if (timeSlot.equals("Déjeuner (1h, privé)") && lesson.getDuration() != 1) continue;
                if (timeSlot.equals("Déjeuner (2h, privé)") && lesson.getDuration() != 2) continue;
            } else {
                if (lesson.getIsPrivate()) continue;
                String accName = lesson.getLessonType().getAccreditation().getName().toLowerCase();
                if ((accName.contains("enfant") || accName.contains("snowboard")) && max != 8) continue;
                if (accName.contains("adulte") && max != 10) continue;
                if ((accName.contains("compétition") || accName.contains("hors-piste")) && max != 8) continue;
            }
            if (current >= max) continue;
            available.add(lesson);
        }
        return available;
    }

    public static Lesson createCollectiveLesson(LocalDateTime date, LessonType lt, Instructor instructor) {
        int duration = 18;
        boolean isPrivate = false;
        if (lt == null) throw new IllegalArgumentException("Le type de cours ne peut pas être null.");
        if (instructor == null) throw new IllegalArgumentException("L'instructeur ne peut pas être null.");
        Lesson lesson = new Lesson(0, date, isPrivate, duration, lt, instructor);
        return lesson;
    }
}