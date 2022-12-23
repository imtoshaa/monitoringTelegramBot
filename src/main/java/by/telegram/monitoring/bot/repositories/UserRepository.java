package by.telegram.monitoring.bot.repositories;

import by.telegram.monitoring.bot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserBySerialNumber(String serialNumber);
    User getUserByChatId(Long chatId);

}
