package mx.unam.fi.distributed.messages.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Representa un dispositivo en el sistema.
 */
@Entity
@Table(name = "device")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    /**
     * El identificador único del dispositivo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "serial_number", length = 10)
    private String serialNumber;

    @ManyToOne
    @JoinColumn(name = "branch_id", referencedColumnName = "branch_id")
    private Branch branch;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "device")
    private List<Ticket> tickets;
}
