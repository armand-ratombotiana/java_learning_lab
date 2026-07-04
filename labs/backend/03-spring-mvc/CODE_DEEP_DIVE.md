# Code Deep Dive: Spring MVC

## Complete Controller with Validation
```java
@Controller
@RequestMapping("/users")
@SessionAttributes("user")
public class UserController {

    @ModelAttribute("departments")
    public List<Department> populateDepartments() {
        return departmentService.findAll();
    }

    @GetMapping
    public String listUsers(Model model, @PageableDefault Pageable pageable) {
        model.addAttribute("users", userService.findAll(pageable));
        return "users/list";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "users/view";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute UserForm form,
                             BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) {
            return "users/form";
        }
        userService.create(form);
        attr.addFlashAttribute("success", "User created");
        return "redirect:/users";
    }
}
```

## Interceptor
```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        log.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(request, response, handler, modelAndView) {
        log.info("Response status: {}", response.getStatus());
    }
}
```
