package mx.unam.fi.distributed.messages.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ticket_id")
    private Integer id;

    @Column(name = "open_date")
    private LocalDateTime openDate;

    @Column(name = "close_date")
    private LocalDateTime closeDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "app_user_id", referencedColumnName = "app_user_id")
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "device_id")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "engineer_id", referencedColumnName = "engineer_id")
    private Engineer engineer;

}
