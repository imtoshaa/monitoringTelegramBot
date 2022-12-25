package by.telegram.monitoring.bot.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(name = "role", nullable = false)
    private String role;
    @ToString.Exclude
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<User> user;
}