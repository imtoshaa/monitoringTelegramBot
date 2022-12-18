package by.telegram.monitoring.bot.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Entity
@Getter
@Setter
@Table(name = "events")
public class Event extends BaseEntity{

    @Column(name = "time", nullable = false)
    private String time; //просто время
    @Column(name = "car_number", nullable = true)
    private int carNumber;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "image_path", nullable = true)
    private String imagePath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "day_id", referencedColumnName = "id")
    private Day day;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
