package mx.unam.fi.distributed.messages.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "device")
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "device_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "serial_number", length = 10)
    private String serialNumber;
}
