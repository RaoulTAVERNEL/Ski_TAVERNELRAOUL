package be.tav.controller;

import be.tav.dao.DAO;
import be.tav.dao.DAOFactory;
import be.tav.pojo.Lesson;
import be.tav.pojo.LessonType;
import be.tav.pojo.Skier;
import be.tav.pojo.Booking;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import be.tav.dao.LessonDAO;
import be.tav.dao.LessonTypeDAO;

public class SkierController {
    private final DAO<Skier> skierDAO;
    private final DAOFactory factory;

    public SkierController(DAOFactory factory) {
        this.skierDAO = factory.getSkierDAO();
        this.factory = factory;
    }

    public ArrayList<Skier> getAllSkiers() {
        return Skier.findSkiers(skierDAO);
    }

    public Skier getSkierById(int id) {
        return Skier.findSkier(id, skierDAO);
    }

    public List<LessonType> getEligibleLessonTypes(Skier skier) {
        LessonTypeDAO lessonTypeDAO = (LessonTypeDAO) factory.getLessonTypeDAO();
        List<LessonType> allLessonTypes = lessonTypeDAO.findAll();
        return skier.getEligibleLessonTypes(allLessonTypes);
    }
    
    public List<Lesson> getAvailableLessons(LessonType type, LocalDate date, String timeSlot) {
        LessonDAO lessonDAO = (LessonDAO) factory.getLessonDAO();
        List<Lesson> allLessons = lessonDAO.findAll();
        return Lesson.findAvailableLessons(allLessons, type, date, timeSlot, false);
    }

    public boolean bookLesson(Skier skier, Lesson lesson, boolean isCollectif, boolean insurance) {
        return Booking.createBooking(skier, lesson, isCollectif, insurance);
    }

    public boolean createSkier(Skier skier) {
        if (skier == null) return false;
        return skierDAO.create(skier);
    }
}