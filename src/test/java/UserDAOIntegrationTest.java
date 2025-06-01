import com.example.dao.UserDAO;
import com.example.domain.User;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDAOIntegrationTest {
    //Создание и запуск временной PostgreSQL БД внутри Docker-контейнера
    //Testcontainers сам управляет жизненным циклом контейнера, поэтому соединение с БД закроется автоматически
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private static SessionFactory sessionFactory;
    private static UserDAO userDAO;

    @BeforeAll
    static void setUpAll() {
        sessionFactory = new org.hibernate.cfg.Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true")
                //При запуске приложения будет создаваться схема БД, при завершении его работы - удаляться
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        userDAO = new UserDAO(sessionFactory);
    }

    @AfterAll
    static void shutdown() {
        if (sessionFactory != null) sessionFactory.close();
    }

    @BeforeEach
    void cleanDb() {
        userDAO.getTxUtil().withTransaction(s -> {
            s.createMutationQuery("delete from User").executeUpdate();
            return null;
        });
    }

    @Test
    void createAndGetById() {
        //Округляем значение времени до микросекунд чтобы не было false в сравнении из-за округления now в БД
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        User user = new User("Alice", "alice@example.com", 30, now);
        userDAO.create(user);
        assertNotNull(user.getId(), "Auto-generated id must be set");

        User fromDb = userDAO.getById(user.getId());
        assertEquals("Alice", fromDb.getName());
        assertEquals("alice@example.com", fromDb.getEmail());
        assertEquals(30, fromDb.getAge());
        assertEquals(now, fromDb.getCreatedAt());
    }

    @Test
    void getAllUsers() {
        userDAO.create(new User("Diana","diana@example.com",22, LocalDateTime.now()));
        userDAO.create(new User("Dale","dale@example.com",28, LocalDateTime.now()));

        List<User> list = userDAO.getAll();
        assertEquals(2, list.size());
    }

    @Test
    void updateUser() {
        User user = new User("Bob", "bob@example.com", 25, LocalDateTime.now());
        userDAO.create(user);

        user.setAge(26);
        userDAO.update(user);

        User updated = userDAO.getById(user.getId());
        assertEquals(26, updated.getAge());
    }

    @Test
    void deleteUser() {
        User user = new User("Carl", "carl@example.com", 40, LocalDateTime.now());
        userDAO.create(user);

        userDAO.delete(user);
        assertNull(userDAO.getById(user.getId()));
    }
}
