package be.tav.pojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import be.tav.dao.DAO;

public class Accreditation {
	private int id;
	private Category name;
	
	private Set<Instructor> instructors;
	private Set<LessonType> lessonTypes;
	
	public Accreditation(int id, String n, int ltId, String ltL, float ltP) {
		this.id = id;
		setName(n);
		instructors = new HashSet<>();
		lessonTypes = new HashSet<>();
		addLessonType(ltId, ltL, ltP);
	}
	
	public Accreditation(String n, int ltId, String ltL, float ltP) { this(-1, n, ltId, ltL, ltP); }
	
	public int getId() { return this.id; }
	
	public String getName() { return this.name.getCategory(); }
	
	public void setName(String n) throws IllegalArgumentException {
	    if(n == null || n.isEmpty()) {
	        throw new IllegalArgumentException("setName a échoué: argument null/empty.");
	    }

	    for(Category categoryEnum : Category.values()) {
	        if(categoryEnum.name().equalsIgnoreCase(n)) {
	            this.name = categoryEnum;
	            
	            return;
	        }
	    }
	    
	    throw new IllegalArgumentException("setName a échoué: " + n + " n'est pas une accréditation valide.");
	}
	
	public HashSet<Instructor> getInstructors() { return new HashSet<Instructor>(this.instructors); }
	
	public void addInstructor(Instructor i) throws IllegalArgumentException, IllegalStateException {
		if(i == null) {
			throw new IllegalArgumentException("addInstructor a échoué: argument null.");
		}
		
		if (!this.instructors.add(i)) {
		    throw new IllegalStateException("addInstructor a échoué: " + i + " est déjà dans la liste instructors.");
		}
	}
	
	public Set<LessonType> getLessonTypes() {
        return new HashSet<>(lessonTypes);
    }
	
	private void addLessonType(int ltId, String ltL, float ltP) { this.lessonTypes.add(new LessonType(ltId, ltL, ltP, this)); }
	
	public void addLessonType(LessonType lt) {
        if (lt == null) throw new IllegalArgumentException("addLessonType a échoué: argument null.");
        lessonTypes.add(lt);
    }
	
	public static Accreditation findAccreditation(int id, DAO<Accreditation> accreditationDAO) { return accreditationDAO.find(id); }
	
	public static ArrayList<Accreditation> findAccreditations(DAO<Accreditation> accreditationDAO) { return accreditationDAO.findAll(); }
}