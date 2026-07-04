# Theory: Spring MVC

## DispatcherServlet Architecture
Spring MVC is built around the Front Controller pattern. The DispatcherServlet acts as the central entry point.

### Request Handling Flow
1. DispatcherServlet receives HTTP request
2. HandlerMapping determines which controller handles it
3. HandlerAdapter invokes the controller method
4. Controller returns ModelAndView or @ResponseBody
5. ViewResolver resolves view templates
6. Response is rendered and sent

### Key Components
- **DispatcherServlet**: Front controller, manages the entire request lifecycle
- **HandlerMapping**: Maps URLs to controller methods
- **HandlerAdapter**: Executes the controller method
- **ViewResolver**: Resolves logical view names to actual views
- **Model**: Container for data passed to views

### Annotations
- @Controller: Marks a class as web controller
- @RequestMapping: Maps HTTP requests to methods
- @ModelAttribute: Binds request parameters to model objects
- @SessionAttributes: Maintains model attributes across sessions
- @InitBinder: Customizes data binding
- @ExceptionHandler: Handles exceptions in controller
