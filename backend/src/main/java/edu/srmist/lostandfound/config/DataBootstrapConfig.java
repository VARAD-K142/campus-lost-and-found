package edu.srmist.lostandfound.config;

import edu.srmist.lostandfound.domain.Item;
import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.ItemCategory;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.ReportType;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.repository.ItemRepository;
import edu.srmist.lostandfound.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
public class DataBootstrapConfig implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataBootstrapConfig.class);

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email:admin@srmist.edu.in}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password}")
    private String adminPassword;

    @Value("${app.bootstrap.demo-user-password:}")
    private String demoUserPassword;

    @Value("${app.bootstrap.admin.name:SRM Lost & Found Administrator}")
    private String adminName;

    public DataBootstrapConfig(UserRepository userRepository,
                               ItemRepository itemRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        bootstrapAdminUser();
        if (demoUserPassword == null || demoUserPassword.isBlank()) {
            logger.info("Demo account and listing bootstrap skipped because DEMO_USER_PASSWORD is not configured.");
            return;
        }
        bootstrapDemoData();
    }

    private void bootstrapAdminUser() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setFullName(adminName);
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setDepartment("Campus Security & Student Affairs");
            admin.setStudentId("ADMIN-001");
            admin.setPhoneNumber("+91 94441 23456");

            userRepository.save(admin);
            logger.info("Bootstrap: Initial administrator account created for [{}]", adminEmail);
        }
    }

    private void bootstrapDemoData() {
        if (itemRepository.count() > 0) {
            return;
        }

        // Create Demo Students
        User student1 = userRepository.findByEmail("rahul.sharma@srmist.edu.in").orElseGet(() -> {
            User u = new User("Rahul Sharma", "rahul.sharma@srmist.edu.in",
                    passwordEncoder.encode(demoUserPassword), Role.USER, "RA2311003010145",
                    "Computer Science and Engineering", "+91 98765 43210");
            return userRepository.save(u);
        });

        User student2 = userRepository.findByEmail("priya.patel@srmist.edu.in").orElseGet(() -> {
            User u = new User("Priya Patel", "priya.patel@srmist.edu.in",
                    passwordEncoder.encode(demoUserPassword), Role.USER, "RA2311003020088",
                    "Electronics and Communication", "+91 97890 12345");
            return userRepository.save(u);
        });

        // Seed Sample Items
        // 1. LOST - Textbook
        Item item1 = new Item();
        item1.setTitle("Calculus and Linear Algebra Textbook");
        item1.setDescription("Hardcover textbook left on desk in Central Library 2nd floor study cubicle. Has handwritten formulas on inside cover.");
        item1.setCategory(ItemCategory.BOOKS);
        item1.setReportType(ReportType.LOST);
        item1.setLocation("Central Library - 2nd Floor Reading Room");
        item1.setIncidentDate(LocalDate.now().minusDays(2));
        item1.setReportDate(LocalDate.now().minusDays(2));
        item1.setContactPreference("IN_APP");
        item1.setStatus(ItemStatus.ACTIVE);
        item1.setReporter(student1);
        itemRepository.save(item1);

        // 2. FOUND - HP Wireless Mouse
        Item item2 = new Item();
        item2.setTitle("HP Wireless Mouse (Black)");
        item2.setDescription("Found on Terminal 14 desk in Tech Park Lab 3. Has minor scratch on scroll wheel and a blue sticker at the base.");
        item2.setCategory(ItemCategory.ELECTRONICS);
        item2.setReportType(ReportType.FOUND);
        item2.setLocation("Tech Park Lab 3");
        item2.setIncidentDate(LocalDate.now().minusDays(1));
        item2.setReportDate(LocalDate.now().minusDays(1));
        item2.setContactPreference("IN_APP");
        item2.setStatus(ItemStatus.ACTIVE);
        item2.setReporter(student2);
        itemRepository.save(item2);

        // 3. FOUND - Bluetooth Headphones
        Item item3 = new Item();
        item3.setTitle("Boat Rockerz 450 Bluetooth Headphones");
        item3.setDescription("Matte black foldable on-ear headphones found on bench near Main Auditorium entrance.");
        item3.setCategory(ItemCategory.ELECTRONICS);
        item3.setReportType(ReportType.FOUND);
        item3.setLocation("Main Auditorium - Entrance Lobby");
        item3.setIncidentDate(LocalDate.now().minusDays(3));
        item3.setReportDate(LocalDate.now().minusDays(3));
        item3.setContactPreference("IN_APP");
        item3.setStatus(ItemStatus.ACTIVE);
        item3.setReporter(student1);
        itemRepository.save(item3);

        // 4. LOST - Blue Leather Wallet
        Item item4 = new Item();
        item4.setTitle("Blue Leather Wallet with College ID");
        item4.setDescription("Navy blue leather bi-fold wallet misplaced during dinner. Contains college ID card and room key card.");
        item4.setCategory(ItemCategory.ACCESSORIES);
        item4.setReportType(ReportType.LOST);
        item4.setLocation("Hostel Block B Mess");
        item4.setIncidentDate(LocalDate.now().minusDays(1));
        item4.setReportDate(LocalDate.now().minusDays(1));
        item4.setContactPreference("IN_APP");
        item4.setStatus(ItemStatus.ACTIVE);
        item4.setReporter(student2);
        itemRepository.save(item4);

        // 5. FOUND - Student ID Card
        Item item5 = new Item();
        item5.setTitle("SRM Student ID Card - RA2311003010456");
        item5.setDescription("Found near University Cafeteria counter 2. Handed over safely to Security Office desk.");
        item5.setCategory(ItemCategory.IDENTIFICATION);
        item5.setReportType(ReportType.FOUND);
        item5.setLocation("University Cafeteria");
        item5.setIncidentDate(LocalDate.now());
        item5.setReportDate(LocalDate.now());
        item5.setContactPreference("IN_APP");
        item5.setStatus(ItemStatus.ACTIVE);
        item5.setReporter(student1);
        itemRepository.save(item5);

        // 6. FOUND - Bike Keys with Carabiner
        Item item6 = new Item();
        item6.setTitle("House and Bike Keys with Red Carabiner");
        item6.setDescription("Set of 3 keys including Honda bike key and red aluminium carabiner clip.");
        item6.setCategory(ItemCategory.KEYS);
        item6.setReportType(ReportType.FOUND);
        item6.setLocation("Campus Main Gate Parking");
        item6.setIncidentDate(LocalDate.now().minusDays(4));
        item6.setReportDate(LocalDate.now().minusDays(4));
        item6.setContactPreference("IN_APP");
        item6.setStatus(ItemStatus.ACTIVE);
        item6.setReporter(student2);
        itemRepository.save(item6);

        logger.info("Bootstrap: Demo students and 6 realistic campus lost/found listings seeded successfully.");
    }
}
