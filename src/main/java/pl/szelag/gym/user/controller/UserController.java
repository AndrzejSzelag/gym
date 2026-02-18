package pl.szelag.gym.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.szelag.gym.user.dto.UserDto;
import pl.szelag.gym.user.service.UserCommandService;
import pl.szelag.gym.user.service.UserReadService;

import java.util.List;

/** Controller for user identity flows: registration, login, and administrative management. */
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserReadService userReadService;
    private final UserCommandService userCommandService;
    private final UserViewHelper userViewHelper;

    /** @param model UI model user list view for administrators */
    @GetMapping("/users")
    public String getUsers(Model model) {
        log.debug("GET /users - fetching admin list");
        List<UserDto> users = userReadService.getUsers();
        model.addAttribute("users", users);
        model.addAttribute("totalItems", users.size());
        return UserViewConstant.VIEW_USERS_LIST;
    }

    /** standard login view */
    @GetMapping("/login")
    public String login() {
        return UserViewConstant.VIEW_LOGIN;
    }

    /** @param model UI model registration form view with empty DTO if not present */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute(UserViewConstant.ATTR_USER_DTO)) {
            model.addAttribute(UserViewConstant.ATTR_USER_DTO, UserDto.builder().build());
        }
        return UserViewConstant.VIEW_REGISTRATION;
    }

    /**
     * @param userDto registration data
     * @param bindingResult validation results
     * @param ra redirect attributes
     * redirect path */
    @PostMapping("/register/save")
    public String registerUser(@Valid @ModelAttribute(UserViewConstant.ATTR_USER_DTO) UserDto userDto,
                               BindingResult bindingResult,
                               RedirectAttributes ra) {
        log.info("Registration attempt for: {}", userDto.email());

        if (bindingResult.hasErrors()) {
            return userViewHelper.redirectWithRegistrationErrors(userDto, bindingResult, ra);
        }

        userCommandService.registerNewUser(userDto);
        userViewHelper.addSuccessMessage(ra, "success.userCreated");

        return "redirect:/login";
    }
}