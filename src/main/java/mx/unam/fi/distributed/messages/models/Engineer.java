package mx.unam.fi.distributed.messages.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "engineer")
@Data
public class Engineer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "engineer_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "speciality")
    private String speciality;
}
