package ek.osnb.demo.coffeeshop.account;

import ek.osnb.demo.coffeeshop.common.ConflictException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AccountService {

    private final AccountRepository accounts;
    private final PasswordEncoder passwordEncoder;

    AccountService(AccountRepository accounts, PasswordEncoder passwordEncoder) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    void register(RegisterRequest request) {
        String email = Account.normalizeEmail(request.email());

        // Hvis emailen findes, kastes exception, som bliver til 409
        if (accounts.existsByEmail(email)) {
            throw new ConflictException("This account already exists");
        }

        accounts.save(Account.create(email, passwordEncoder.encode(request.password()), Role.CUSTOMER));
    }
}
