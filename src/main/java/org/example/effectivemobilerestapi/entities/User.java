package org.example.effectivemobilerestapi.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.effectivemobilerestapi.enums.Role;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "public")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ToString.Exclude
    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    private List<Task> createdTasks = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "executor", fetch = FetchType.EAGER)
    private List<Task> assignedTasks = new ArrayList<>();
}
