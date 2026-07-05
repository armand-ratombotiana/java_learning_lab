package com.learning.backend03;

import jakarta.validation.constraints.*;

/**
 * Form backing object used with @ModelAttribute and @Valid.
 *
 * Bean Validation annotations define constraints that are automatically
 * checked when @Valid is present on the controller parameter.
 */
public class RegistrationForm {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Min(value = 18, message = "You must be at least 18 years old")
    @Max(value = 120, message = "Age must be realistic")
    private int age;

    @AssertTrue(message = "You must accept the terms")
    private boolean acceptedTerms;

    // Getters and setters

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public boolean isAcceptedTerms() { return acceptedTerms; }
    public void setAcceptedTerms(boolean acceptedTerms) { this.acceptedTerms = acceptedTerms; }

    @Override
    public String toString() {
        return "RegistrationForm{username='" + username + "', email='" + email +
               "', age=" + age + ", acceptedTerms=" + acceptedTerms + "}";
    }
}
