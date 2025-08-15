package be.tav.view;

import be.tav.dao.DAOFactory;
import be.tav.dao.PeriodDAO;
import be.tav.pojo.Booking;
import be.tav.pojo.Lesson;
import be.tav.pojo.Period;
import be.tav.pojo.Skier;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BookingManagementView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final DAOFactory factory;
    private PeriodDAO periodDAO;
    private JComboBox<String> weekSelector;
    private JLabel revenueLabel;
    private JLabel totalBookingsLabel;
    private JPanel bookingsPanel;
    private List<Booking> allBookings;
    private JLabel weekDateLabel;
    private List<Period.WeekInfo> validWeeks;
    private int selectedWeekIdx = 0;

    public BookingManagementView(DAOFactory factory) {
        this.factory = factory;
        this.periodDAO = (PeriodDAO) factory.getPeriodDAO();
        setTitle("Gestion des réservations");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new TopBannerPanel(AccueilNavigationHelper::navigateAccueil, true), BorderLayout.NORTH);
        initData();
        initComponents();
    }

    private void initData() {
        allBookings = factory.getBookingDAO().findAll();
        validWeeks = Period.getValidWeeks(periodDAO);
        if (!validWeeks.isEmpty()) selectedWeekIdx = 0;
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel weekPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevWeekBtn = new JButton("<");
        JButton nextWeekBtn = new JButton(">");
        prevWeekBtn.addActionListener(e -> {
            if (selectedWeekIdx > 0) {
                selectedWeekIdx--;
                weekSelector.setSelectedIndex(selectedWeekIdx);
                updateWeekDateLabel();
                refreshBookingsPanel();
            }
        });
        nextWeekBtn.addActionListener(e -> {
            if (selectedWeekIdx < validWeeks.size() - 1) {
                selectedWeekIdx++;
                weekSelector.setSelectedIndex(selectedWeekIdx);
                updateWeekDateLabel();
                refreshBookingsPanel();
            }
        });
        weekPanel.add(prevWeekBtn);
        weekPanel.add(new JLabel("Semaine : "));
        weekSelector = new JComboBox<>();
        for (Period.WeekInfo w : validWeeks) {
            weekSelector.addItem(w.toString());
        }
        if (!validWeeks.isEmpty()) weekSelector.setSelectedIndex(selectedWeekIdx);
        weekSelector.addActionListener(e -> {
            selectedWeekIdx = weekSelector.getSelectedIndex();
            updateWeekDateLabel();
            refreshBookingsPanel();
        });
        weekPanel.add(weekSelector);
        nextWeekBtn.setFocusable(false);
        prevWeekBtn.setFocusable(false);
        weekPanel.add(nextWeekBtn);
        weekDateLabel = new JLabel();
        weekPanel.add(Box.createHorizontalStrut(20));
        weekPanel.add(weekDateLabel);
        mainPanel.add(weekPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        revenueLabel = new JLabel();
        totalBookingsLabel = new JLabel();
        summaryPanel.add(revenueLabel);
        summaryPanel.add(Box.createHorizontalStrut(30));
        summaryPanel.add(totalBookingsLabel);
        mainPanel.add(summaryPanel);

        bookingsPanel = new JPanel();
        bookingsPanel.setLayout(new BoxLayout(bookingsPanel, BoxLayout.Y_AXIS));
        JScrollPane bookingsScroll = new JScrollPane(bookingsPanel);
        bookingsScroll.setPreferredSize(new Dimension(1100, 550));
        mainPanel.add(bookingsScroll);

        add(mainPanel, BorderLayout.CENTER);
        updateWeekDateLabel();
        refreshBookingsPanel();
    }

    private void updateWeekDateLabel() {
        if (validWeeks.isEmpty()) {
            weekDateLabel.setText("");
            return;
        }
        Period.WeekInfo w = validWeeks.get(selectedWeekIdx);
        weekDateLabel.setText("(" + w.start + " au " + w.end + ")");
    }

    private void refreshBookingsPanel() {
        bookingsPanel.removeAll();
        if (validWeeks.isEmpty()) {
            bookingsPanel.revalidate();
            bookingsPanel.repaint();
            return;
        }
        Period.WeekInfo w = validWeeks.get(selectedWeekIdx);
        List<Booking> weekBookings = allBookings.stream().filter(b -> {
            Lesson l = b.getLesson();
            if (l == null || l.getDate() == null) return false;
            java.time.LocalDate d = l.getDate().toLocalDate();
            return !d.isBefore(w.start) && !d.isAfter(w.end);
        }).collect(Collectors.toList());
        double totalRevenue = 0;
        int totalBookings = weekBookings.size();
        java.util.Map<Lesson, List<Booking>> lessonBookings = weekBookings.stream()
                .collect(Collectors.groupingBy(Booking::getLesson));
        for (java.util.Map.Entry<Lesson, List<Booking>> entry : lessonBookings.entrySet()) {
            Lesson lesson = entry.getKey();
            List<Booking> bookings = entry.getValue();
            double lessonTotal = bookings.stream().mapToDouble(b -> getBookingPrice(b)).sum();
            totalRevenue += lessonTotal;
            JPanel lessonPanel = new JPanel();
            lessonPanel.setLayout(new BoxLayout(lessonPanel, BoxLayout.Y_AXIS));
            String typeCours = lesson.getIsPrivate() ? "cours particulier" : "cours collectif";
            lessonPanel.setBorder(BorderFactory.createTitledBorder(
                lesson.getLessonType().getAccreditation().getName() + " - " + lesson.getLessonType().getLevel()
                + " (" + typeCours + ")"
            ));
            String instructorName = lesson.getInstructor().getLastname() + " " + lesson.getInstructor().getFirstname();
            JLabel infoLabel;
            if (lesson.getIsPrivate()) {
                String lessonDate = lesson.getDate().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String lessonTime = lesson.getDate().toLocalTime().toString();
                String duration = lesson.getDuration() + "h";
                infoLabel = new JLabel("Moniteur : " + instructorName +
                    " | Date : " + lessonDate +
                    " | Heure : " + lessonTime +
                    " | Durée : " + duration +
                    " | Prix total : " + String.format("%.2f", lessonTotal) + "€");
            } else {
                infoLabel = new JLabel("Moniteur : " + instructorName +
                    " | Prix total : " + String.format("%.2f", lessonTotal) + "€");
            }
            lessonPanel.add(infoLabel);
            int quota = lesson.getLessonType().getMinStudents();
            int max;
            if (lesson.getIsPrivate()) {
                max = 4;
            } else {
                max = lesson.getLessonType().getMaxStudents();
            }
            boolean quotaReached = bookings.size() >= quota;
            JLabel quotaLabel = new JLabel("Réservations : " + bookings.size() + "/" + max);
            quotaLabel.setForeground(quotaReached ? Color.GREEN.darker() : Color.RED);
            lessonPanel.add(quotaLabel);
            String[] columns = {"Nom du skieur", "Âge", "Email", "Assurance", "Prix"};
            Object[][] data = new Object[bookings.size()][5];
            int i = 0;
            for (Booking b : bookings) {
                Skier s = b.getSkier();
                data[i][0] = s.getLastname() + " " + s.getFirstname();
                data[i][1] = s.getAge();
                data[i][2] = s.getEmail();
                data[i][3] = b.getHasInsurance() ? "Oui" : "Non";
                data[i][4] = String.format("%.2f", getBookingPrice(b));
                i++;
            }
            JTable skiersTable = new JTable(data, columns);
            skiersTable.setEnabled(false);
            skiersTable.setRowHeight(24);
            skiersTable.setFont(new Font("Arial", Font.PLAIN, 13));
            skiersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            lessonPanel.add(new JScrollPane(skiersTable));
            bookingsPanel.add(lessonPanel);
            bookingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }
        revenueLabel.setText("Revenu total semaine : " + String.format("%.2f", totalRevenue) + "€");
        totalBookingsLabel.setText("Total réservations : " + totalBookings);
        bookingsPanel.revalidate();
        bookingsPanel.repaint();
    }

    private double getBookingPrice(Booking b) {
        Lesson lesson = b.getLesson();
        if (lesson.getIsPrivate()) {
            int duration = lesson.getDuration();
            if (duration == 1) return 60.0;
            if (duration == 2) return 90.0;
            return 60.0;
        } else {
            double base = lesson.getLessonType().getPrice();
            if (b.getHasInsurance()) base += 10;
            return base;
        }
    }
}