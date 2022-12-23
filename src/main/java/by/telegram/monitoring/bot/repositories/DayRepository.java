package by.telegram.monitoring.bot.repositories;

import by.telegram.monitoring.bot.entities.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayRepository extends JpaRepository<Day, Long> {

    Day getDayByDate(String date);
}
