package by.telegram.monitoring.bot.services.impl;

import by.telegram.monitoring.bot.entities.Day;
import by.telegram.monitoring.bot.repositories.DayRepository;
import by.telegram.monitoring.bot.services.DayService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static by.telegram.monitoring.bot.utils.constants.PagesPathConstants.HOME_PAGE;

@Slf4j
@AllArgsConstructor
@Service
public class DaysServiceImpl implements DayService {
    @Autowired
    private DayRepository dayRepository;

    @Override
    public ModelAndView getAllDays() {
        ModelMap modelMap = new ModelMap();
        List<Day> allDays = dayRepository.findAll();
        modelMap.addAttribute("days", allDays);
        return new ModelAndView(HOME_PAGE, modelMap);
    }
}
