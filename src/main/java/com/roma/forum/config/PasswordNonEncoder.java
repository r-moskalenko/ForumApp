package com.roma.forum.config;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordNonEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        return charSequence.toString();
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return charSequence.toString().equals(s);
    }
}
