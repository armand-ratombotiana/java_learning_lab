package com.learning.backend16.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class GitEncryptionConfig {

    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.noOpText();
    }
}
