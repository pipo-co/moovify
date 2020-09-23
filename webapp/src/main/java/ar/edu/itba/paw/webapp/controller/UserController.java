package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.InvalidResetPasswordToken;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import ar.edu.itba.paw.webapp.form.UserCreateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("user/login");
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.GET)
    public ModelAndView showUserCreateForm(@ModelAttribute("userCreateForm") final UserCreateForm userCreateForm) {

        return new ModelAndView("user/create");
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.POST)
    public ModelAndView register(@Valid @ModelAttribute("userCreateForm") final UserCreateForm userCreateForm, final BindingResult bindingResult,
                                  HttpServletRequest request, final RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors())
            return showUserCreateForm(userCreateForm);


        final User user = userService.register(userCreateForm.getUsername(),
                userCreateForm.getPassword(), userCreateForm.getName(), userCreateForm.getEmail());

        createVerificationToken(user, request);

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        redirectAttributes.addFlashAttribute("user", user);

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
    public ModelAndView view(HttpServletRequest request, @PathVariable final long userId) {

        final ModelAndView mv = new ModelAndView("user/view");

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if(inputFlashMap == null || !inputFlashMap.containsKey("user"))
            mv.addObject("user", userService.findById(userId)
                .orElseThrow(UserNotFoundException::new));

        mv.addObject("posts", userService.findPostsByUserId(userId));

        return mv;
    }

    @RequestMapping(path = "/user/profile", method = RequestMethod.GET)
    public ModelAndView profile(HttpServletRequest request, Principal principal) {

        final ModelAndView mv = new ModelAndView("user/profile");

        User user;
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if(inputFlashMap == null || !inputFlashMap.containsKey("user"))
            user = userService.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
        else
            user = (User) inputFlashMap.get("user");

        mv.addObject("loggedUser", user);
        mv.addObject("posts", userService.findPostsByUserId(user.getId()));
        return mv;
    }

    // TODO: Hacer vista
    @RequestMapping(path = "/user/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(HttpServletRequest request, @RequestParam String token) {

        final Optional<User> optUser = userService.confirmRegistration(token);
        boolean success;


        ModelAndView mv = new ModelAndView("user/registrationConfirm");

        if(optUser.isPresent()) {
            success = true;
            final User user = optUser.get();

            mv.addObject("loggedUser", user);

            // User roles have been updates. We need to refresh authorities
            manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());
        }
        else
            success = false;

        mv.addObject("success", success);

        return mv;
    }

    @RequestMapping(path = "/user/resetPassword", method = RequestMethod.GET)
    public ModelAndView showResetPassword( @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm) {
        return new ModelAndView("user/resetPassword");
    }

    @RequestMapping(path = "/user/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@Valid @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm,
                                      HttpServletRequest request, final BindingResult bindingResult) {

        if(bindingResult.hasErrors())
            return showResetPassword(resetPasswordForm);

        final User user = userService.findByEmail(resetPasswordForm.getEmail()).orElseThrow(UserNotFoundException::new);

        createPasswordResetToken(user, request);

        final ModelAndView mv = new ModelAndView("user/resetPasswordTokenGenerated");

        mv.addObject("loggedUser", user);

        return mv;
    }

    // TODO: Hacer vista
    @RequestMapping(path = "/user/resendConfirmation", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(HttpServletRequest request, Principal principal) {

        // TODO: Validate url with Spring Security
        User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        createVerificationToken(user, request);

        ModelAndView mv = new ModelAndView("user/resendConfirmation");

        mv.addObject("loggedUser", user);

        return mv;
    }

    @RequestMapping(path = "/user/updatePassword/token", method = RequestMethod.GET)
    public ModelAndView validateResetPasswordToken(@RequestParam String token, RedirectAttributes redirectAttributes) {
        boolean validToken = userService.validatePasswordResetToken(token);

        if(validToken){
            redirectAttributes.addFlashAttribute("token", token);

            return new ModelAndView("redirect:/user/updatePassword");
        }

        return new ModelAndView("user/updatePasswordError");
    }

    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.GET)
    public ModelAndView showUpdatePassword(@ModelAttribute("updatePasswordForm") final UpdatePasswordForm updatePasswordForm,
                                           HttpServletRequest request) {

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if(inputFlashMap != null && inputFlashMap.containsKey("token"))
            updatePasswordForm.setToken((String) inputFlashMap.get("token"));

        return new ModelAndView("user/updatePassword");
    }

    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@Valid @ModelAttribute("updatePasswordForm") final UpdatePasswordForm updatePasswordForm,
                                        HttpServletRequest request, final BindingResult bindingResult) {

        if(bindingResult.hasErrors())
            return showUpdatePassword(updatePasswordForm, request);

        User user = userService.updatePassword(updatePasswordForm.getPassword(), updatePasswordForm.getToken())
                .orElseThrow(InvalidResetPasswordToken::new);

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        ModelAndView mv = new ModelAndView("user/passwordResetSuccess");

        mv.addObject("loggedUser", user);

        return mv;
    }

    private void createVerificationToken(User user, HttpServletRequest request) {
        final String token = userService.createVerificationToken(user.getId());

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("confirmationURL",
                ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/user/registrationConfirm").queryParam("token", token).build().toUriString()
        );

        try {
            mailService.sendEmail(user.getEmail(), "Confirmation Email", "confirmEmail", emailVariables);
        }
        catch (MessagingException e) {
            // TODO: Log
            System.out.println("Confirmation email failed to send");
        }
    }

    private void createPasswordResetToken(User user, HttpServletRequest request) {
        final String token = userService.createPasswordResetToken(user.getId());

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("confirmationURL",
                ServletUriComponentsBuilder.fromRequestUri(request)
                        .replacePath("/user/updatePassword/token").queryParam("token", token).build().toUriString()
        );

        try {
            mailService.sendEmail(user.getEmail(), "Password Reset", "passwordResetEmail", emailVariables);
        }
        catch (MessagingException e) {
            // TODO: Log
            System.out.println("Password reset email failed to send");
        }
    }

    private void manualLogin(HttpServletRequest request, String username, String password, Collection<Role> roles) {

        PreAuthenticatedAuthenticationToken token =
                new PreAuthenticatedAuthenticationToken(username, password, getGrantedAuthorities(roles));

        token.setDetails(new WebAuthenticationDetails(request));

        SecurityContextHolder.getContext().setAuthentication(token);

        //this step is important, otherwise the new login is not in session which is required by Spring Security
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        return roles.stream().map((role) -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).collect(Collectors.toList());
    }
}
