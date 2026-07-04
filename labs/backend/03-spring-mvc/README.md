# Spring MVC

Spring MVC is the web framework component of Spring, providing Model-View-Controller architecture.

## Topics
- @Controller and @RestController
- HandlerMapping and HandlerAdapter
- View resolvers (Thymeleaf, JSP, Freemarker)
- Data binding and validation
- Interceptors and filters
- Content negotiation
- Exception handling in MVC

## Example
```java
@Controller
@RequestMapping("/users")
public class UserController {
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }
}
```
