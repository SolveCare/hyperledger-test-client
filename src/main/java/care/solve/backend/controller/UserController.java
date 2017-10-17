package care.solve.backend.controller;

import care.solve.backend.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("{userName}")
    public void create(@PathVariable String userName) throws Exception {
        userService.registerUser(userName);
    }

    @PostMapping("{userName}/current")
    public void setCurrentUser(@PathVariable String userName) throws Exception {
        userService.setCurrentUser(userName);
    }
}
