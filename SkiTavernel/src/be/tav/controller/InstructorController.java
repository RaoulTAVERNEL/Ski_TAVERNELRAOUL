package be.tav.controller;

import be.tav.dao.DAO;
import be.tav.dao.DAOFactory;
import be.tav.pojo.Instructor;
import java.util.ArrayList;

public class InstructorController {
    private final DAO<Instructor> instructorDAO;

    public InstructorController(DAOFactory factory) {
        this.instructorDAO = factory.getInstructorDAO();
    }

    public ArrayList<Instructor> getAllInstructors() {
        return Instructor.findInstructors(instructorDAO);
    }

    public Instructor getInstructorById(int id) {
        return Instructor.findInstructor(id, instructorDAO);
    }
}
