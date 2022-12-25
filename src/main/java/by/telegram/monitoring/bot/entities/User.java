package by.telegram.monitoring.bot.entities;

import by.telegram.monitoring.bot.utils.SerialNumberConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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

    @Column(name = "serial_number")
    @SerialNumberConstraint
    private String serialNumber;
    @Column(name = "password")
//    @PasswordConstraint
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "chat_id")
    private Long chatId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id")
    private Role role;


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private List<Event> events;
}
