package by.telegram.monitoring.bot;

import by.telegram.monitoring.bot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByChatId(String chatId);
}
