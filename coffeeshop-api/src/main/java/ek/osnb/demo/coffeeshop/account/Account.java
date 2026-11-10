package ek.osnb.demo.coffeeshop.account;

import jakarta.persistence.*;
import java.util.Locale;

// klassen bliver til tabellen accounts
@Entity
@Table(name = "accounts")
class Account {

    // Id = primary key, MySQL tildeler numrene (bliver altså til AUTO_INCREMENT)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // email skal altid udfyldes og to konti kan ikke have samme email
    @Column(nullable = false, unique = true)
    private String email;

    // Gemmer kun BCrypt-version af password
    @Column(nullable = false)
    private String passwordHash;

    // Rollen gemmes som CUSTOMER eller STAFF som tekst - fra enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Bruges af Hibernate, når den henter en konto fra databasen
    protected Account() {
       // Tom constructor - JPA-regel
    }

    // Koden kan ikke oprette konto, uden alle felter udfyldt
    private Account(String email, String passwordHash, Role role){
        this.email = email.trim().toLowerCase(Locale.ROOT);  // gemmer mail med små bogstaver
        this.passwordHash = passwordHash;
        this.role = role;
    }

    static Account create(String email, String passwordHash, Role role) {
        return new Account(email, passwordHash, role);
    }

    // Andre klasser i account-package kan læse felterne uden at ændre dem
    Long getId() { return id; }
    String getEmail() { return email; }
    String getPasswordHash() { return passwordHash; }
    Role getRole() { return role; }


}
