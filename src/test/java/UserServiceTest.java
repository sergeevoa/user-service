import com.example.dao.UserDAO;
import com.example.domain.User;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserDAO dao;
    private UserService service;

    //Инициализируем новый mock перед каждым тестом, чтобы гарантировать изолированность тестов
    @BeforeEach
    void setUp() {
        dao = mock(UserDAO.class);
        service = new UserService(dao);
    }

    @Test
    void create() {
        service.create("Alice", "alice@example.com", 30);

        //captor позволяет перехватить аргумент, переданный в метод мок-объекта
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        //Проверяем что dao.create был вызван один раз
        //captor.capture() сохраняет объект User переданный в dao.create(...)
        verify(dao).create(captor.capture());

        User user = captor.getValue();
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(30, user.getAge());
        LocalDateTime createdAt = user.getCreatedAt();
        assertNotNull(createdAt);
        //Проверка того что поле createdAt содержит значение, отстоящее от текущего не более чем на 1 сек.
        assertTrue(Duration.between(createdAt, LocalDateTime.now()).toMillis() < 1000);
    }

    @Test
    void getById() {
        User mock = new User("Bob", "bob@example.com", 25, LocalDateTime.now());
        //Устанавливаем поведение заглушки dao
        when(dao.getById(1L)).thenReturn(mock);

        User result = service.get(1L);
        assertSame(mock, result);       //Сравниваем объекты по ссылкам
        verify(dao).getById(1L);
    }

    @Test
    void getAll() {
        List<User> list = List.of(
                new User("A", "a@a", 20, LocalDateTime.now()),
                new User("B", "b@b", 21, LocalDateTime.now())
        );
        when(dao.getAll()).thenReturn(list);

        List<User> result = service.getAll();
        assertEquals(list, result);
        verify(dao).getAll();
    }

    @Test
    void update() {
        User existing = new User("Carol", "carol@ex.com", 40, LocalDateTime.now());
        existing.setId(5L);
        when(dao.getById(5L)).thenReturn(existing);

        User updatedUser = service.update(5L, "NewName", null, 45);

        assertEquals(updatedUser.getName(), existing.getName());
        assertEquals(updatedUser.getEmail(), existing.getEmail());
        assertEquals(updatedUser.getAge(), existing.getAge());

        verify(dao).update(existing);
    }

    @Test
    void updateWhenUserNotFound() {
        when(dao.getById(99L)).thenReturn(null);
        User notExisting = service.update(99L, "newName", "newEmail", 60);

        assertNull(notExisting);

        verify(dao, never()).update(any());
    }

    @Test
    void delete() {
        User existing = new User("Carol", "carol@ex.com", 40, LocalDateTime.now());
        existing.setId(5L);
        when(dao.getById(5L)).thenReturn(existing);

        User deletedUser = service.delete(5L);

        assertSame(existing, deletedUser);

        verify(dao).delete(existing);
    }

    @Test
    void deleteWhenUserNotFound() {
        when(dao.getById(99L)).thenReturn(null);
        User deletedUser = service.delete(99L);

        assertNull(deletedUser);

        verify(dao, never()).update(any());
    }
}
