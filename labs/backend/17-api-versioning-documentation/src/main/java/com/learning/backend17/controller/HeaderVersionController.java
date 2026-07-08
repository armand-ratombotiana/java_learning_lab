package com.learning.backend17.controller;

import com.learning.backend17.config.ApiVersionInterceptor;
import com.learning.backend17.model.UserV1;
import com.learning.backend17.model.UserV2;
import com.learning.backend17.service.UserServiceV1;
import com.learning.backend17.service.UserServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Header-Versioned API", description = "Demonstrates header-based versioning")
public class HeaderVersionController {

    private static final Logger log = LoggerFactory.getLogger(HeaderVersionController.class);
    private final UserServiceV1 userServiceV1;
    private final UserServiceV2 userServiceV2;

    public HeaderVersionController(UserServiceV1 userServiceV1, UserServiceV2 userServiceV2) {
        this.userServiceV1 = userServiceV1;
        this.userServiceV2 = userServiceV2;
    }

    @GetMapping
    @Operation(summary = "Get user by header version",
               description = "Set Accept-Version header to '1' for V1 or '2' for V2")
    public ResponseEntity<?> getUser(
            @RequestParam(defaultValue = "1") Long id) {
        String version = ApiVersionInterceptor.getCurrentVersion();
        log.info("Header version request: v{}, id={}", version, id);

        if ("2".equals(version)) {
            var user = userServiceV2.findById(id);
            if (user == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user);
        }
        var user = userServiceV1.findById(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }
}
