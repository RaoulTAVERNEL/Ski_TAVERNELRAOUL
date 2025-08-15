package be.tav.controller;

import be.tav.pojo.Skier;
import be.tav.pojo.LessonType;
import be.tav.pojo.Lesson;
import be.tav.dao.DAOFactory;
import be.tav.dao.LessonDAO;
import be.tav.dao.LessonTypeDAO;
import java.util.List;

public class BookLessonController {
    private final Skier skier;
    private final DAOFactory factory;
    private final SkierController skierController;

    public BookLessonController(Skier skier, DAOFactory factory) {
        this.skier = skier;
        this.factory = factory;
        this.skierController = new SkierController(factory);
    }

    public List<LessonType> getEligibleLessonTypes() {
        LessonTypeDAO lessonTypeDAO = (LessonTypeDAO) factory.getLessonTypeDAO();
        List<LessonType> allLessonTypes = lessonTypeDAO.findAll();
        return skier.getEligibleLessonTypes(allLessonTypes);
    }

    public List<String> getValidTimeSlots() {
        return LessonType.getValidTimeSlots();
    }

    public List<Lesson> getAvailableLessons(LessonType type, java.time.LocalDate date, String timeSlot, boolean isIndividual) {
        LessonDAO lessonDAO = (LessonDAO) factory.getLessonDAO();
        List<Lesson> allLessons = lessonDAO.findAll();
        return Lesson.findAvailableLessons(allLessons, type, date, timeSlot, isIndividual);
    }

    public float computeTotalPrice(LessonType type, boolean insurance) {
        if (type == null) return 0f;
        float price = type.getPrice();
        if (insurance) price += 10.0f;
        return price;
    }

    public String validateBooking(LessonType type, String dateStr, String timeSlot, boolean insurance, boolean isCollectif) {
        if (type == null) return "Veuillez sélectionner un type de cours.";
        if (dateStr == null || dateStr.isEmpty()) return "Veuillez sélectionner une date.";
        try {
            if (isCollectif) {
            } else {
            }
        } catch (Exception ex) {
            return "Format de date invalide : " + dateStr;
        }
        boolean isChild = skier.getAge() < 18;
        String accName = type.getAccreditation() != null ? type.getAccreditation().getName().toLowerCase() : "";
        if (isChild && !accName.contains("enfant")) {
            return "Ce skieur ne peut participer qu'à des cours enfants.";
        }
        if (!isChild && !accName.contains("adulte")) {
            return "Ce skieur ne peut participer qu'à des cours adultes.";
        }
        boolean isGroup = timeSlot.equals("Matin") || timeSlot.equals("Après-midi");
        boolean isPrivate = timeSlot.contains("privé");
        if (insurance && isPrivate) {
            return "L'assurance n'est disponible que pour les cours de groupe matin/après-midi.";
        }
        if (insurance && !isGroup) {
            return "L'assurance n'est disponible que pour les cours de groupe matin/après-midi.";
        }
        return null;
    }

    public Skier getSkier() {
        return skier;
    }

    public SkierController getSkierController() {
        return skierController;
    }
}