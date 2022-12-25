package by.telegram.monitoring.bot.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static by.telegram.monitoring.bot.utils.constants.PagesPathConstants.LOGIN_PAGE;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/login")
public class AuthenticationController {

  @GetMapping
  public ModelAndView openLoginPage() {
    return new ModelAndView(LOGIN_PAGE);
  }
}
