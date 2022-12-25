package by.telegram.monitoring.bot.services;

import by.telegram.monitoring.bot.entities.User;
import by.telegram.monitoring.bot.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      UserDetails userDetails;
      Optional<User> user = userRepository.findUserBySerialNumber(username);
      if (user.isPresent()) {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(user.get().getRole().getRole()));
        userDetails = new org.springframework.security.core.userdetails.User(user.get().getSerialNumber(),
            user.get().getPassword(), roles);
      } else {
        throw new UsernameNotFoundException("User wasn't found");
      }

      return userDetails;
    }
}
