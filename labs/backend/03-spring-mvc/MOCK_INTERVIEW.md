# Mock Interview: Spring MVC (Lab 03)

**Role:** Full-Stack Developer (Mid-Level)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Explain the MVC pattern and how Spring MVC implements it.

**Candidate:** MVC (Model-View-Controller) separates application concerns into three components:
- **Model** — data and business logic (POJOs, services, repositories)
- **View** — presentation layer (Thymeleaf, JSP, FreeMarker)
- **Controller** — handles requests, interacts with model, selects view

Spring MVC implements this through:
- `@Controller` or `@RestController` classes to handle requests
- `DispatcherServlet` as the front controller that routes requests
- `ViewResolver` to map logical view names to actual templates
- `Model` interface to pass data from controller to view
- `@RequestMapping` and its variants to map URL patterns to handler methods

**Interviewer:** What is the DispatcherServlet and how does the request flow work?

**Candidate:** `DispatcherServlet` is the front controller in Spring MVC. The request flow:

1. Client sends HTTP request → `DispatcherServlet`
2. `DispatcherServlet` queries `HandlerMapping` to find the appropriate controller method
3. Request is dispatched to the matched controller method
4. Controller processes request and returns `ModelAndView` (logical view name + model data)
5. `DispatcherServlet` uses `ViewResolver` to resolve the logical view name to an actual `View`
6. View renders the model data into HTML (Thymeleaf template processing)
7. Response is sent back to client

**Interviewer:** How do you handle form submission with Spring MVC?

**Candidate:** Spring MVC uses data binding to map form parameters to a backing object (DTO). Steps:
1. Controller serves `GET /form` → renders Thymeleaf template with form
2. Template uses Thymeleaf's `th:object` and `th:field` to bind form fields
3. User submits form via `POST /form` → controller method with `@ModelAttribute` parameter
4. Spring automatically binds request parameters to the DTO fields
5. Validation via `@Valid` on the DTO parameter
6. On validation error, `BindingResult` contains errors and returns to form view
7. On success, redirect to success page (PRG pattern)

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does Spring MVC handle data binding and validation? What are potential security concerns?

**Candidate:** Data binding automatically maps HTTP request parameters to Java objects. For a `UserForm` with `name` and `email` fields, Spring binds `?name=John&email=john@example.com` to the object's setters.

**Security concerns:**
1. **Mass assignment / binding injection:** If the form object has a `role` field and the request includes `role=ADMIN`, an attacker could escalate privileges. Solution: Use `@InitBinder` with `setDisallowedFields` or `setAllowedFields` to whitelist permitted fields.
2. **Cross-Site Request Forgery (CSRF):** Spring Security enables CSRF protection by default for state-changing requests. Thymeleaf automatically includes CSRF tokens in forms.
3. **Validation bypass:** Always validate on server-side, never rely solely on client-side validation.

**Interviewer:** How does Thymeleaf integrate with Spring MVC?

**Candidate:** Thymeleaf provides `spring-standard` and `spring-security` dialects. Key features:
- `th:object` binds to a form backing object
- `th:field` auto-generates `name`, `id`, `value`, and error classes from bound fields
- `th:errors` displays validation errors for a specific field
- `th:if`, `th:each`, `th:switch` for template logic
- `th:action` generates proper form action URLs
- `#fields.hasErrors()` for conditional error display
- `@{}` syntax for URL expressions with context path, `#{}` for i18n messages

**Interviewer:** Compare server-side rendering (SSR) with Single Page Applications (SPA). When would you choose each?

**Candidate:** 
| Aspect | SSR (Thymeleaf/JSP) | SPA (React/Angular) |
|--------|-------------------|---------------------|
| SEO | Excellent (full HTML) | Requires SSR workaround |
| Initial load | Faster perceived load | Slower initial bundle download |
| Interactivity | Full page reloads | Smooth transitions |
| Complexity | Simpler, backend-centric | Both frontend and backend |
| Caching | Server-side caching easy | Client-side caching |
| Best for | Content sites, admin panels | Highly interactive dashboards, apps |

I'd choose SSR for applications where SEO matters, content is the primary focus, or the team is backend-heavy. SPA for highly interactive user experiences like dashboards or collaborative tools.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design an MVC-based admin dashboard that handles 1000 concurrent admin users with complex forms, file uploads, and real-time updates.

**Candidate:** Architecture:

```
                                  ┌──────────────┐
                                  │ Redis Session │
                                  │   Replication │
                                  └──────┬───────┘
                                         │
  Browser → Load Balancer → Spring Boot (x3 instances)
                              ├─ DispatcherServlet
                              ├─ Controller Layer (admin/*)
                              ├─ Service Layer
                              ├─ Thymeleaf Templates (fragments)
                              └─ WebSocket (STOMP) for real-time
```

**Performance optimizations:**
1. **Thymeleaf caching:** `spring.thymeleaf.cache=true` in production
2. **Fragment composition:** Use `th:replace` with template fragments for reusable components. Cache rendered fragments for dynamic content that changes infrequently.
3. **Static resource versioning:** `spring.resources.chain.strategy.content.enabled=true` for cache busting
4. **Lazy loading:** Defer non-critical sections with AJAX load
5. **WebSocket push:** Replace polling with STOMP WebSocket for real-time metrics (Lab 19 SSE)
6. **File upload streaming:** Use `MultipartFile.transferTo()` directly to target storage, avoid buffer in memory
7. **Session management:** Redis-backed session for horizontal scaling (`@EnableRedisHttpSession`)

**Form complexity handling:**
- Use `@SessionAttributes` for multi-step wizards with form progress
- JavaScript-driven dynamic form sections with Thymeleaf fragments
- Debounced auto-save via AJAX `@PostMapping` partial updates
- Drag-and-drop file upload with AJAX progress indicators

**Interviewer:** How would you implement form validation across multiple steps of a wizard?

**Candidate:** I'd use a session-scoped wizard DTO with `@SessionAttributes`:

```java
@Controller
@SessionAttributes("wizardForm")
@RequestMapping("/admin/wizard")
public class WizardController {
    
    @GetMapping("/step1")
    public String step1(Model model) {
        if (!model.containsAttribute("wizardForm")) {
            model.addAttribute("wizardForm", new WizardForm());
        }
        return "admin/wizard/step1";
    }
    
    @PostMapping("/step1")
    public String processStep1(@Valid @ModelAttribute("wizardForm") WizardForm form,
                               BindingResult result, SessionStatus status) {
        if (result.hasErrors()) return "admin/wizard/step1";
        // Store step1 data in session, no DB save yet
        return "redirect:/admin/wizard/step2";
    }
    
    @PostMapping("/complete")
    public String complete(@ModelAttribute("wizardForm") @Valid WizardForm form,
                           BindingResult result, SessionStatus status) {
        // Validate all fields across steps
        wizardService.save(form);
        status.setComplete(); // Clean session
        return "redirect:/admin/wizard/success";
    }
}
```

Group validation can apply different groups for each step: `@Validated(Step1.class)`.

**Interviewer:** How do you handle exception handling in Spring MVC with proper error pages?

**Candidate:** Spring Boot's `ErrorController` auto-configures `/error` mapping. For custom handling:
1. **`@ControllerAdvice` with `@ExceptionHandler`** for controller-level exceptions:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(HttpServletRequest request, ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("url", request.getRequestURL());
        return mav;
    }
}
```
2. **Custom `ErrorController`** implementing `ErrorController` for global error handling
3. **Error page templates:** `src/main/resources/templates/error/404.html`, `error/5xx.html`
4. **Proper HTTP status codes** from `@ResponseStatus` annotations

For AJAX requests, I check `X-Requested-With` header and return JSON for AJAX vs HTML for direct navigation.

---

## Interviewer Feedback

**Strengths:**
- Clear understanding of MVC request lifecycle
- Practical approach to security concerns
- Good wizard/multi-step form design
- Performance optimization for SSR applications

**Areas to Improve:**
- Could discuss HTMX as a modern alternative to traditional form handling
- Should mention `th:fragment` and template inheritance patterns

**Verdict:** Hire

---

## Follow-Up Questions

1. How would you implement i18n in Spring MVC with Thymeleaf?
2. What is the Post/Redirect/Get pattern and why is it important?
3. How does Spring MVC handle `@MatrixVariable` and when would you use it?
4. Explain the role of `HandlerInterceptor` and how it differs from a filter.
5. How do you secure server-rendered pages against CSRF, XSS, and clickjacking?

---

*Lab 03 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
