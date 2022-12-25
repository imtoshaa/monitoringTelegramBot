package by.telegram.monitoring.bot.services.impl;

import by.telegram.monitoring.bot.entities.Event;
import by.telegram.monitoring.bot.repositories.EventRepository;
import by.telegram.monitoring.bot.services.EventsService;
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
public class EventsServiceImpl implements EventsService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public ModelAndView getAllEvents() {
        ModelMap modelMap = new ModelMap();

        List<Event> allEvents = eventRepository.findAll();
        modelMap.addAttribute("events", allEvents);
        return new ModelAndView(HOME_PAGE, modelMap);
    }
}
