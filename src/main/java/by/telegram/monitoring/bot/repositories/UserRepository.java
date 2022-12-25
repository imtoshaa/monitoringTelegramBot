package by.telegram.monitoring.bot.repositories;

import by.telegram.monitoring.bot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserBySerialNumber(String serialNumber);
    User getUserByChatId(Long chatId);
    Optional<User> findUserBySerialNumber(String serialNumber);
    boolean existsUserByChatId(Long chatId);
}
