package by.telegram.monitoring.bot.dto.converters;

import by.telegram.monitoring.bot.dto.NewUserDto;
import by.telegram.monitoring.bot.entities.Role;
import by.telegram.monitoring.bot.entities.User;
import by.telegram.monitoring.bot.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NewUserConverter {

    @Autowired
    private RoleRepository roleRepository;

    public User fromDto(NewUserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .serialNumber(userDto.getSerialNumber())
                .role(roleRepository.getRoleByRole(userDto.getRole()))
                .build();
    }
}
