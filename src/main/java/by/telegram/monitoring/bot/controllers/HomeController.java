package by.telegram.monitoring.bot.controllers;

import by.telegram.monitoring.bot.services.DayService;
import by.telegram.monitoring.bot.services.EventsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static by.telegram.monitoring.bot.utils.constants.PagesPathConstants.HOME_PAGE;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/home")
public class HomeController {


    @Autowired
    private DayService dayService;

    @GetMapping
    public ModelAndView openLoginPage() {
        return dayService.getAllDays();
    }
}
