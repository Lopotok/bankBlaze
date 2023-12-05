package hu.bankblaze.bankblaze.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Desk {
    @Id
    private Long id;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "employee_id")
    @JsonManagedReference
    private Employee employee;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "queue_number_id")
    @JsonManagedReference
    private QueueNumber queueNumber;
}
