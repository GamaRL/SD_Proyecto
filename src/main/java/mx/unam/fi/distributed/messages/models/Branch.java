package mx.unam.fi.distributed.messages.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Representa una sucursal en el sistema distribuido.
 */
@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Branch {

    /**
     * El identificador único de la sucursal.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "branch")
    private List<Device> devices;
}
