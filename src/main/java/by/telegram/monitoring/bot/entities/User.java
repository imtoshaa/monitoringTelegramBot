package by.telegram.monitoring.bot.entities;

import by.telegram.monitoring.bot.utils.PasswordConstraint;
import by.telegram.monitoring.bot.utils.SerialNumberConstraint;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "serial_number", nullable = false)
    @SerialNumberConstraint
    private String sNumber;
    @Column(name = "password", nullable = false)
    @PasswordConstraint
    private String password;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "surname", nullable = false)
    private String surname;
    @Column(name = "chatId", nullable = false)
    private int chatId;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private List<Event> events;
}
