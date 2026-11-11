package ek.osnb.demo.coffeeshop.account;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Staff kan ikke oprette sig selv, så staff account er created ved opstart
@Component
class AccountInitData implements CommandLineRunner {

    private final AccountRepository accounts;
    private final PasswordEncoder passwordEncoder;

    // Spring giver repository og encoder med (dependency injection)
    AccountInitData(AccountRepository accounts, PasswordEncoder passwordEncoder) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
    }

    // Kører én gang efter opstart. *Public fordi den kommer fra et interface*
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Findes kontoen allerede, sker der intet (ingen duplicates ved reload)
        if (accounts.existsByEmail("staff@coffeeshop.dk")) {
            return;
        }
        // Password hashes, før det gemmes
        accounts.save(Account.create("staff@coffeeshop.dk", passwordEncoder.encode("staff123"), Role.STAFF));
    }
}
