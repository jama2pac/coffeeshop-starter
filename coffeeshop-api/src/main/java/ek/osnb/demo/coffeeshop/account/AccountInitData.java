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

    AccountInitData(AccountRepository accounts, PasswordEncoder passwordEncoder) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (accounts.existsByEmail("staff@coffeeshop.dk")) {
            return;
        }
        accounts.save(Account.create("staff@coffeeshop.dk", passwordEncoder.encode("staff123"), Role.STAFF));
    }
}
