package by.telegram.monitoring.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    private String serialNumber;
    private String name;
    private String surname;
    private String role;
}
