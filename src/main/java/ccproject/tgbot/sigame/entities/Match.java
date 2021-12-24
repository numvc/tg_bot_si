package ccproject.tgbot.sigame.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.List;


@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"player_1", "player_2"})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matches")
public class Match {
    @Id
    @NotNull
    @Column(name = "match_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "player_1", referencedColumnName = "user_id")
    private User player_1;

    @NotNull
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "player_2", referencedColumnName = "user_id")
    private User player_2;

    @NotNull
//    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, orphanRemoval = true)
//    @OneToMany(mappedBy = "match")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "match_questions", joinColumns=@JoinColumn(name="match_id"),
            inverseJoinColumns=@JoinColumn(name="id"))
    private List<Question> questions;
}
