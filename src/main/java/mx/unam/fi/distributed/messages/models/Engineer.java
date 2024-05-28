package mx.unam.fi.distributed.messages.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "engineer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Engineer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "engineer_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "speciality")
    private String speciality;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "engineer")
    private List<Ticket> tickets;
}
