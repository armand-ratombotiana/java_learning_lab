package com.learning.backend03;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Classic Spring MVC controller using @Controller (not @RestController).
 *
 * Methods return view names (Thymeleaf templates) rather than JSON bodies.
 * @ModelAttribute can be used at method level to add attributes to the model
 * for every request, or at parameter level to bind form data.
 */
@Controller
@RequestMapping("/register")
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    /**
     * Populates common model attributes for all request mappings in this class.
     */
    @ModelAttribute("appName")
    public String appName() {
        return "Spring MVC Registration Demo";
    }

    /**
     * GET /register — displays the registration form.
     * A new RegistrationForm is added to the model as the form backing object.
     */
    @GetMapping
    public String showForm(Model model) {
        log.info("Showing registration form");
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration-form";
    }

    /**
     * POST /register — processes the form submission.
     *
     * @Valid triggers validation on the RegistrationForm.
     * BindingResult must immediately follow the validated parameter to capture errors.
     * If errors exist, the form view is re-displayed with error messages.
     */
    @PostMapping
    public String submitForm(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                             BindingResult bindingResult, Model model) {
        log.info("Form submitted: {}", form);

        if (bindingResult.hasErrors()) {
            log.warn("Validation failed with {} errors", bindingResult.getErrorCount());
            return "registration-form";
        }

        model.addAttribute("message", "Registration successful for " + form.getUsername());
        return "registration-success";
    }
}
