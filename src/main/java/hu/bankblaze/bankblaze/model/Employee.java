package hu.bankblaze.bankblaze.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"permissions", "desks"})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String role;
    private String password;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<Desk> desks = new ArrayList<>();

}

