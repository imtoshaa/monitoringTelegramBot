package by.telegram.monitoring.bot.services.impl;

import by.telegram.monitoring.bot.dto.NewUserDto;
import by.telegram.monitoring.bot.dto.converters.NewUserConverter;
import by.telegram.monitoring.bot.entities.User;
import by.telegram.monitoring.bot.repositories.UserRepository;
import by.telegram.monitoring.bot.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import static by.telegram.monitoring.bot.utils.constants.PagesPathConstants.ADMIN_PAGE;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NewUserConverter userConverter;

    @Override
    public ModelAndView createNewUser(NewUserDto userDto) {
        if (userDto.getName() != null
                && userDto.getSurname() != null
                && userDto.getRole() != null
                && userDto.getSerialNumber() != null) {
            User user = userConverter.fromDto(userDto);
            userRepository.save(userConverter.fromDto(userDto));
            return new ModelAndView(ADMIN_PAGE);
        }
        return new ModelAndView(ADMIN_PAGE);
    }
}
