package org.example.effectivemobilerestapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.effectivemobilerestapi.dto.Role;

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

    @Column(nullable = false, unique = true)
    @Setter
    private String email;

    @Column(nullable = false)
    @Setter
    private String password;

    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;
}
