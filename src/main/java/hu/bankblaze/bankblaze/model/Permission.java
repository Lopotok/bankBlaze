package hu.bankblaze.bankblaze.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permission")
@JsonIgnoreProperties("employee")
public class Permission {

    @Id
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "employee_id")
    @JsonManagedReference
    private Employee employee;


    private Boolean forRetail=false;

    private Boolean forCorporate=false;

    private Boolean forTeller=false;

    private Boolean forPremium=false;
}
