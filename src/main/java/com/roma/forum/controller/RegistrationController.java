package com.roma.forum.controller;

import com.roma.forum.domain.User;
import com.roma.forum.domain.dto.CaptchaResponseDto;
import com.roma.forum.service.UserService;
import java.util.Collections;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class RegistrationController {
    private static final String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&%s";

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${recaptcha.secret}")
    private String secret;

    public RegistrationController() {
    }

    @GetMapping({"/registration"})
    public String registration() {
        return "registration";
    }

    @PostMapping({"/registration"})
    public String addUser(@RequestParam("password2") String passwordConfirm,
                          @RequestParam("g-recaptcha-response") String recaptchaResponse,
                          @Valid User user, BindingResult bindingResult,
                          Model model) {
        String url = String.format(CAPTCHA_URL,
                this.secret, recaptchaResponse);

        CaptchaResponseDto response = restTemplate
                .postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        System.out.println(response);
        assert response != null;
        if (!response.isSuccess()) {
            model.addAttribute("captchaError", "Fill captcha");
        }

        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);

        if (isConfirmEmpty) {
            model.addAttribute("password2Error",
                    "Password confirmation cannot be empty");
        }

        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Passwords are different");
        }

        if (isConfirmEmpty || !bindingResult.hasErrors() || !response.isSuccess()) {
            Map<String, String> errors = ControllerUtils.getErros(bindingResult);

            model.mergeAttributes(errors);

            return "registration";
        }

        if (!this.userService.addUser(user)) {
            model.addAttribute("usernameError", "User exists!");

            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping({"activate/{code}"})
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = this.userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found");
        }

        return "login";
    }
}
