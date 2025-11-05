package br.com.ss.blog.domain.service;

import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPasswordService {

    private final PasswordEncoder passwordEncoder;

    public UserPasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encodePassword(String rawPassword, String pepper) {
        // If using a server-side pepper, append it before encoding:
        // rawToStore = rawPassword + pepper
        // pepper should come from secure storage (KMS/Secret Manager), not hard-coded.
        String toEncode = (pepper == null) ? rawPassword : rawPassword + pepper;
        return passwordEncoder.encode(toEncode);
    }

    public boolean matches(String rawPassword, String storedHash, String pepper) {
        String toCheck = (pepper == null) ? rawPassword : rawPassword + pepper;
        return passwordEncoder.matches(toCheck, storedHash);
    }

    @Transactional
    public String upgradeHashIfNeeded(String rawPassword, String storedHash, String pepper) {
        // If DelegatingPasswordEncoder indicates upgrade is needed, re-encode and return new hash.
        if (passwordEncoder instanceof DelegatingPasswordEncoder) {
            DelegatingPasswordEncoder delegating = (DelegatingPasswordEncoder) passwordEncoder;
            if (delegating.upgradeEncoding(storedHash)) {
                String newHash = encodePassword(rawPassword, pepper);
                // persist newHash to DB for the user (update repository) — omitted here
                return newHash;
            }
        }
        return storedHash;
    }
}