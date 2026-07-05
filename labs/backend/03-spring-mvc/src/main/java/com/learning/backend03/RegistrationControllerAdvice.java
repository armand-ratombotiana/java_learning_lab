package com.learning.backend03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @ControllerAdvice provides global @InitBinder, @ModelAttribute,
 * and @ExceptionHandler methods across all controllers.
 *
 * The @InitBinder method below trims whitespace from all String inputs
 * and converts empty strings to null before validation runs.
 */
@ControllerAdvice
public class RegistrationControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(RegistrationControllerAdvice.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        log.debug("Initializing binder: {}", binder);
        // StringTrimmerEditor: if true, empty strings are converted to null
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
