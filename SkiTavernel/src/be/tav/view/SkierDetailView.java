package be.tav.view;

import be.tav.pojo.Skier;
import be.tav.controller.SkierController;
import javax.swing.*;
import java.awt.*;
import be.tav.pojo.Booking;
import be.tav.pojo.Lesson;
import be.tav.controller.BookLessonController;
import be.tav.dao.DAOFactory;

public class SkierDetailView extends AbstractDetailView {
    private static final long serialVersionUID = 1L;
    private final Skier skier;
    public SkierDetailView(int skierId, SkierController controller) {
        super("Détails du skieur", 1200, 800);
        this.skier = controller.getSkierById(skierId);
        initComponents();
    }

    @Override
    protected void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel nameLabel = new JLabel(skier.getLastname() + " " + skier.getFirstname());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);

        java.time.format.DateTimeFormatter frFormatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String birthdateStr = skier.getBirthdate() != null ? skier.getBirthdate().format(frFormatter) : "?";
        JLabel birthdateLabel = new JLabel("Date de naissance : " + birthdateStr + " (" + skier.getAge() + " ans)");
        birthdateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        birthdateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(birthdateLabel);

        JLabel statusLabel = new JLabel("Statut : " + (skier.getAge() < 18 ? "Enfant" : "Adulte"));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        JLabel emailLabel = new JLabel("Email : " + skier.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(emailLabel);

        JLabel phoneLabel = new JLabel("Téléphone : " + skier.getPhone());
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(phoneLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel bookingsLabel = new JLabel("Réservations :");
        bookingsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bookingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(bookingsLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton bookLessonButton = new JButton("Réserver un cours");
        bookLessonButton.setFont(new Font("Arial", Font.BOLD, 16));
        bookLessonButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookLessonButton.addActionListener(e -> {
            BookLessonController bookLessonController = new BookLessonController(skier, new DAOFactory());
            BookLessonView bookLessonView = new BookLessonView(bookLessonController);
            bookLessonView.setVisible(true);
            this.dispose();
        });
        mainPanel.add(bookLessonButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        if (skier.getBookings().isEmpty()) {
            JLabel noBookingLabel = new JLabel("Aucune réservation trouvée pour ce skieur.");
            noBookingLabel.setFont(new Font("Arial", Font.ITALIC, 15));
            noBookingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(noBookingLabel);
        } else {
            java.util.List<Booking> sortedBookings = new java.util.ArrayList<>(skier.getBookings());
            sortedBookings.sort((b1, b2) -> {
                if (b1.getLesson() == null || b1.getLesson().getDate() == null) return 1;
                if (b2.getLesson() == null || b2.getLesson().getDate() == null) return -1;
                return b1.getLesson().getDate().compareTo(b2.getLesson().getDate());
            });
            String[] columns = {"Cours réservé", "Instructeur", "Date", "Type de cours", "Assurance", "Prix"};
            Object[][] tableData = new Object[sortedBookings.size()][6];
            int i = 0;
            for (Booking booking : sortedBookings) {
                Lesson lesson = booking.getLesson();
                String course = "?";
                if (lesson != null && lesson.getLessonType() != null) {
                    var lt = lesson.getLessonType();
                    String acc = lt.getAccreditation() != null ? lt.getAccreditation().getName() : "";
                    String niveau = lt.getLevel();
                    course = acc + " - Niveau " + niveau;
                }
                String instructor = lesson != null && lesson.getInstructor() != null
                        ? lesson.getInstructor().getLastname() + " " + lesson.getInstructor().getFirstname()
                        : "?";
                String date;
                if (lesson != null && lesson.getDate() != null) {
                    if (!lesson.getIsPrivate()) {
                        java.time.LocalDateTime start = lesson.getDate();
                        java.time.LocalDateTime end = start.plusHours(lesson.getDuration());
                        date = start.format(frFormatter) + " " + String.format("%02d:%02d", start.getHour(), start.getMinute()) +
                               " - " + end.format(frFormatter);
                    } else {
                        date = lesson.getDate().format(frFormatter) + " " + String.format("%02d:%02d", lesson.getDate().getHour(), lesson.getDate().getMinute());
                    }
                } else {
                    date = "?";
                }
                String type = lesson != null ? (lesson.getIsPrivate() ? "Cours particulier" : "Cours collectif") : "?";
                String assurance = (!lesson.getIsPrivate() && booking.getHasInsurance()) ? "Oui" : (!lesson.getIsPrivate() ? "Non" : "-");
                float prix;
                if (lesson != null && lesson.getIsPrivate()) {
                    int duration = lesson.getDuration();
                    if (duration == 1) prix = 60f;
                    else if (duration == 2) prix = 90f;
                    else prix = 60f;
                } else if (lesson != null && lesson.getLessonType() != null) {
                    prix = lesson.getLessonType().getPrice();
                    if (booking.getHasInsurance()) prix += 10f;
                } else {
                    prix = 0f;
                }
                String prixStr = prix > 0 ? String.format("%.2f €", prix) : "?";
                tableData[i][0] = course;
                tableData[i][1] = instructor;
                tableData[i][2] = date;
                tableData[i][3] = type;
                tableData[i][4] = assurance;
                tableData[i][5] = prixStr;
                i++;
            }
            JTable bookingsTable = new JTable(tableData, columns);
            bookingsTable.setFont(new Font("Arial", Font.PLAIN, 15));
            bookingsTable.setRowHeight(28);
            bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
            bookingsTable.setEnabled(false);
            bookingsTable.setShowGrid(true);
            bookingsTable.setFillsViewportHeight(true);
            JScrollPane bookingsScroll = new JScrollPane(bookingsTable);
            bookingsScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
            bookingsScroll.setPreferredSize(new Dimension(500, Math.min(200, 28 * (sortedBookings.size() + 1))));
            mainPanel.add(bookingsScroll);
        }

        add(mainPanel, BorderLayout.CENTER);
    }
}