package care.solve.backend.controller;

import care.solve.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/current")
    public Map getCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        SimpleGrantedAuthority authority = (SimpleGrantedAuthority) authentication.getAuthorities().iterator().next();
        String role = authority.getAuthority();

        return new HashMap() {{
            put("name", userName);
            put("role", role);
        }};
    }

//    @PostMapping("{userName}/current")
//    public void setCurrentUser(@PathVariable String userName) throws Exception {
//        userService.setCurrentUser(userName);
//    }
}
