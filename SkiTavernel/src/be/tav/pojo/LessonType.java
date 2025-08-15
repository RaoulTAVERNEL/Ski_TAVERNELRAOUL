package be.tav.pojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import be.tav.dao.DAO;

public class LessonType {
	private int id;
	private Level level;
	private float price;
	
	private Accreditation accreditation;
	private Set<Lesson> lessons;
	
	private int minStudents;
	private int maxStudents;
	
	private boolean privateType;
	
	public LessonType(int id, String l, float p, Accreditation a) {
		this.id = id;
		setLevel(l);
		setPrice(p);
		setAccreditation(a);
		lessons = new HashSet<>();
		setMinMaxStudents();
		String accName = a != null ? a.getName().toLowerCase() : "";
		String levelName = l != null ? l.toLowerCase() : "";
		this.privateType = accName.contains("privé") || accName.contains("individuel") || levelName.contains("privé") || levelName.contains("individuel");
	}

	private void setMinMaxStudents() {
		String accName = accreditation != null ? accreditation.getName().toLowerCase() : "";
		if (accName.contains("enfant") || accName.contains("snowboard")) {
			minStudents = 5;
			maxStudents = 8;
		} else if (accName.contains("adulte")) {
			minStudents = 6;
			maxStudents = 10;
		} else if (accName.contains("compétition") || accName.contains("competition") || accName.contains("hors-piste") || accName.contains("hors piste")) {
			minStudents = 5;
			maxStudents = 8;
		} else {
			minStudents = 5;
			maxStudents = 8;
		}
	}

	public int getMinStudents() { return minStudents; }
	public int getMaxStudents() { return maxStudents; }
	
	public int getId() { return this.id; }
	
	public String getLevel() { return this.level.getLevel(); }
	
	public void setLevel(String l) throws IllegalArgumentException {
	    if(l == null || l.isEmpty()) {
	        throw new IllegalArgumentException("setLevel a échoué: argument null/empty.");
	    }

	    for(Level levelEnum : Level.values()) {
	        if(levelEnum.name().equalsIgnoreCase(l)) {
	            this.level = levelEnum;
	            
	            return;
	        }
	    }
	    
	    throw new IllegalArgumentException("setLevel a échoué: " + l + " n'est pas un niveau valide.");
	}
	
	public float getPrice() { return this.price; }
	
	public void setPrice(float p) throws IllegalArgumentException {
		if(p == 0) {
			throw new IllegalArgumentException("setPrice a échoué: argument à 0.");
		}
		
		Set<Float> validPrices = Set.of(120.f, 130.f, 140.f, 150.f, 160.f, 170.f);
		
		if(!validPrices.contains(p)) {
			throw new IllegalArgumentException("setPrice a échoué: " + p + " n'est pas un prix valide.");
		}
		
		this.price = p;
	}
	
	public Accreditation getAccreditation() { return this.accreditation; }
	
	public void setAccreditation(Accreditation a) {
		if(a == null) {
			throw new IllegalArgumentException("setAccreditation a échoué: argument null.");
		}
		
		this.accreditation = a;
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
	
	public static LessonType findLessonType(int id, DAO<LessonType> lessonTypeDAO) { return lessonTypeDAO.find(id); }
	
	public static ArrayList<LessonType> findLessonTypes(DAO<LessonType> lessonTypeDAO) { return lessonTypeDAO.findAll(); }

	public boolean isPrivateType() {
        return privateType;
    }
    public void setPrivateType(boolean value) {
        this.privateType = value;
    }
	
	public boolean isCollective() {
        return this.minStudents >= 5;
    }
	
	@Override
	public String toString() {
		String acc = accreditation != null ? accreditation.getName() : "?";
		return acc + " - Niveau " + getLevel();
	}
	
	public static java.util.List<String> getValidTimeSlots() {
        java.util.List<String> slots = new java.util.ArrayList<>();
        slots.add("Matin");
        slots.add("Après-midi");
        slots.add("Déjeuner (1h, privé)");
        slots.add("Déjeuner (2h, privé)");
        return slots;
    }
}