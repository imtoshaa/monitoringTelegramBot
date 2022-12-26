package by.telegram.monitoring.bot.services;

import by.telegram.monitoring.bot.dto.NewUserDto;
import org.springframework.web.servlet.ModelAndView;

public interface UserService {

    ModelAndView createNewUser(NewUserDto userDto);
}
