package hu.bankblaze.bankblaze.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "queue_number")
public class QueueNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int number;
    private Boolean toRetail = false;
    private Boolean toCorporate = false;
    private Boolean toTeller = false;
    private Boolean toPremium = false;
    private Boolean active = true;

    /*@OneToMany(mappedBy = "queueNumber", cascade = CascadeType.REMOVE)
    private List<Desk> desks = new ArrayList<>();*/
}
