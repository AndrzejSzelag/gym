package pl.szelag.gym.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.szelag.gym.user.dto.UserDto;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/index")
    public String index(Principal principal, Model model) {
        String loggedUserPrincipalName = principal.getName(); // name=email
        model.addAttribute("name", loggedUserPrincipalName);
        return "index";
    }

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/users")
    public String getUsers(Model model) {
        List<UserDto> users = userService.getUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping(value = "/register")
    public String registerUser(Model model) {
        UserDto newUser = UserDto.builder().build();
        model.addAttribute("user", newUser);
        model.addAttribute("action", "/register");
        return "user";
    }

    @PostMapping(value = "/register/save")
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
        User existingUser = getExistingUserEmail(userDto);
        checkIsUserEmailExist(existingUser, result);
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            model.addAttribute("failure", "failure"); // alert-danger in failure (user.html)
            return "user";
        }
        userService.registerUser(userDto);
        return "redirect:/register?success";
    }

    private User getExistingUserEmail(UserDto userDto) {
        return userService.getUser(userDto.getEmail());
    }

    private static void checkIsUserEmailExist(User existingUser, BindingResult result) {
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email",null, "PROBLEM");
        }
    }
}