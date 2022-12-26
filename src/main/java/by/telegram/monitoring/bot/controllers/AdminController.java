package by.telegram.monitoring.bot.controllers;

import by.telegram.monitoring.bot.dto.NewUserDto;
import by.telegram.monitoring.bot.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static by.telegram.monitoring.bot.utils.constants.PagesPathConstants.ADMIN_PAGE;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ModelAndView getAdminPage() {
        return new ModelAndView(ADMIN_PAGE);
    }

    @PostMapping("/newUser")
    public ModelAndView saveNewBrand(@ModelAttribute NewUserDto userDto) {
        return userService.createNewUser(userDto);
    }
}
