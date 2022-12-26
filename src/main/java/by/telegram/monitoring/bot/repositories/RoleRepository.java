package by.telegram.monitoring.bot.repositories;

import by.telegram.monitoring.bot.entities.Event;
import by.telegram.monitoring.bot.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role getRoleByRole(String role);
}
