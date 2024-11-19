package org.rockpaperscissors.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordService() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    // Metodo para encriptar la contraseña
    public String encryptPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    // Metodo para verificar la contraseña
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
