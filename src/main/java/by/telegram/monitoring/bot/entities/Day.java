package by.telegram.monitoring.bot.entities;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Entity
@Getter
@Setter
@Table(name = "days")
public class Day extends BaseEntity {

    private String date; //просто день

    @OneToMany(mappedBy = "day", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private List<Event> events;
}
