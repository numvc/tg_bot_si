package ccproject.tgbot.sigame.entities;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = "cur_match")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "uzers")
public class User {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    private String username;

    @DecimalMin(value = "0")
    private double points;

    @NotNull
    @Column(columnDefinition = "integer default 0")
    private int c_points = 0;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "cur_match_id", referencedColumnName = "match_id")
    private Match cur_match;

    @Column(nullable = false)
    private int state = 0;

    @Transient
    private LocalDateTime localDateTimeSend;
}
