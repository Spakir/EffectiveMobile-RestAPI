package org.example.effectivemobilerestapi.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.effectivemobilerestapi.enums.Role;
import java.util.List;
import java.util.ArrayList;

@EqualsAndHashCode
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "public")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    @Setter
    private String email;

    @Column(nullable = false,length = 200)
    @Setter
    private String password;

    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;

    @OneToMany(mappedBy = "author",fetch = FetchType.EAGER)
    private List<Task> createdTasks = new ArrayList<>();

    @OneToMany(mappedBy = "executor",fetch = FetchType.EAGER)
    private List<Task> assignedTasks = new ArrayList<>();
}
