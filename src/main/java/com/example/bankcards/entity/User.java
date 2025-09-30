package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "name", length = 100)
    String name;

    @Column(name = "surname", length = 100)
    String surname;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    String email;

    @Column(name = "password", length = 255, nullable = false)
    String password;

    @ColumnDefault("true")
    @Column(name = "enabled", nullable = false)
    Boolean enabled = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "authorities_id"
    )
    Authority authority;

    @OneToMany(mappedBy = "owner")
    List<Card> cards;
}
