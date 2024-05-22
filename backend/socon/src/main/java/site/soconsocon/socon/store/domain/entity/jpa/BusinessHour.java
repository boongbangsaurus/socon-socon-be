package site.soconsocon.socon.store.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;


@Entity(name="BUSINESS_HOUR")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "store")
public class BusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_hour_id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "day", nullable = false)
    private String day;

    @Column(name = "is_working", nullable = false, columnDefinition = "boolean default false")
    private Boolean isWorking;

    @Column(name = "open_at")
    private Time openAt;

    @Column(name = "close_at")
    private Time closeAt;

    @Column(name = "is_breaktime", nullable = false, columnDefinition = "boolean default false")
    private Boolean isBreaktime;

    @Column(name = "breaktime_start")
    private Time breaktimeStart;

    @Column(name = "breaktime_end")
    private Time breaktimeEnd;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}