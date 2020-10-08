package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.InvalidResetPasswordToken;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import ar.edu.itba.paw.webapp.form.UserCreateForm;
import ar.edu.itba.paw.webapp.form.editProfile.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

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
                                  final HttpServletRequest request, final RedirectAttributes redirectAttributes) throws IOException {

        if(bindingResult.hasErrors())
            return showUserCreateForm(userCreateForm);

        final User user;

        try {
            user = userService.register(userCreateForm.getUsername(),
                    userCreateForm.getPassword(), userCreateForm.getName(),
                    userCreateForm.getEmail(), userCreateForm.getDescription(), userCreateForm.getAvatar().getBytes(), "confirmEmail");
        }

        catch(DuplicateUsernameException dupUsername) {
            bindingResult.rejectValue("username", "validation.user.UniqueUsername");
            return showUserCreateForm(userCreateForm);
        }

        catch (DuplicateEmailException dupEmail) {
            bindingResult.rejectValue("email", "validation.user.UniqueEmail");
            return showUserCreateForm(userCreateForm);
        }

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        redirectAttributes.addFlashAttribute("user", user);

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = {"/user/{userId}", "/user/{userId}/posts"} , method = RequestMethod.GET)
    public ModelAndView viewPosts(HttpServletRequest request,
                                  @PathVariable final long userId,
                                  @RequestParam(defaultValue = "5") final int pageSize,
                                  @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("user/view/viewPosts");

        final User user = getUserFromFlashParamsOrById(userId, request);

        mv.addObject("user", user);

        mv.addObject("posts", postService.findPostsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/{userId}/comments", method = RequestMethod.GET)
    public ModelAndView viewComments(HttpServletRequest request,
                                     @PathVariable final long userId,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("user/view/viewComments");

        final User user = getUserFromFlashParamsOrById(userId, request);

        mv.addObject("user", user);

        mv.addObject("comments", commentService.findCommentsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = {"/user/profile", "/user/profile/posts"}, method = RequestMethod.GET)
    public ModelAndView profilePosts(@ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                     final HttpServletRequest request, final Principal principal,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("user/profile/profilePosts");

        User user;
        final Map<String, ?> flashParams = RequestContextUtils.getInputFlashMap(request);

        if(flashParams != null && flashParams.containsKey("user"))
            user = (User) flashParams.get("user");

        else
            user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        mv.addObject("loggedUser", user);
        mv.addObject("posts", postService.findPostsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/profile/comments", method = RequestMethod.GET)
    public ModelAndView profileComments(@ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm, Principal principal,
                                        @RequestParam(defaultValue = "5") final int pageSize,
                                        @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("user/profile/profileComments");

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        mv.addObject("loggedUser", user);
        mv.addObject("comments", commentService.findCommentsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/profile/edit", method = RequestMethod.GET)
    public ModelAndView editProfile(@ModelAttribute("nameEditForm") final NameEditForm nameEditForm, @ModelAttribute("usernameEditForm") final UsernameEditForm usernameEditForm, @ModelAttribute("descriptionEditForm") final DescriptionEditForm descriptionEditForm) {
        return new ModelAndView("user/profile/profileEdit");
    }

    @RequestMapping(path = "/user/edit/name", method = RequestMethod.POST)
    public ModelAndView editName(@Valid @ModelAttribute("nameEditForm") final NameEditForm nameEditForm, final BindingResult bindingResult,
                                 @ModelAttribute("usernameEditForm") final UsernameEditForm usernameEditForm,
                                 @ModelAttribute("descriptionEditForm") final DescriptionEditForm descriptionEditForm,
                                 Principal principal) {

        if(bindingResult.hasErrors())
            return editProfile(nameEditForm, usernameEditForm, descriptionEditForm ) ;

        User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.updateName(user, nameEditForm.getName());

        return new ModelAndView("redirect:/user/profile/edit");
    }

    @RequestMapping(path = "/user/edit/username", method = RequestMethod.POST)
    public ModelAndView usernameEdit(@Valid @ModelAttribute("usernameEditForm") final UsernameEditForm usernameEditForm, final BindingResult bindingResult,
                                     @ModelAttribute("nameEditForm") final NameEditForm nameEditForm,
                                     @ModelAttribute("descriptionEditForm") final DescriptionEditForm descriptionEditForm,
                                     HttpServletRequest request, Principal principal) {

        if(bindingResult.hasErrors())
            return editProfile(nameEditForm, usernameEditForm , descriptionEditForm);

        User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        try {
            userService.updateUsername(user, usernameEditForm.getUsername());
        }
        catch(DuplicateUsernameException e) {
            bindingResult.rejectValue("username", "validation.user.UniqueUsername");
        }

        manualLogin(request, usernameEditForm.getUsername(), user.getPassword(), user.getRoles());

        return new ModelAndView("redirect:/user/profile/edit");
    }

    @RequestMapping(path = "/user/edit/description", method = RequestMethod.POST)
    public ModelAndView executeEdit(@Valid @ModelAttribute("descriptionEditForm") final DescriptionEditForm descriptionEditForm, final BindingResult bindingResult,
                                    @ModelAttribute("nameEditForm") final NameEditForm nameEditForm,
                                    @ModelAttribute("usernameEditForm") final UsernameEditForm usernameEditForm,
                                    Principal principal) {

        if(bindingResult.hasErrors())
            return editProfile(nameEditForm, usernameEditForm , descriptionEditForm);

        User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.updateDescription(user, descriptionEditForm.getDescription());

        return new ModelAndView("redirect:/user/profile/edit");
    }

    @RequestMapping(path = "/user/changePassword", method = RequestMethod.GET)
    public ModelAndView changePassword(@ModelAttribute("changePasswordForm") final ChangePasswordForm changePasswordForm) {

        return new ModelAndView("user/profile/changePassword");
    }

    @RequestMapping(path = "/user/changePassword", method = RequestMethod.POST)
    public ModelAndView executeChangePassword(@Valid @ModelAttribute("changePasswordForm") final ChangePasswordForm changePasswordForm, final BindingResult bindingResult, Principal principal) {

        if(bindingResult.hasErrors())
            return changePassword(changePasswordForm);

        User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.updatePassword(user, changePasswordForm.getPassword());

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = "/user/profile/avatar", method = RequestMethod.POST)
    public ModelAndView registerProfilePost(@Valid @ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                            final BindingResult bindingResult,
                                            final HttpServletRequest request,
                                            final Principal principal) throws IOException {
        if(bindingResult.hasErrors())
            return profilePosts(avatarEditForm, request, principal,5,0);


        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.updateAvatar(user, avatarEditForm.getAvatar().getBytes());

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = "/user/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(HttpServletRequest request, @RequestParam String token) {

        final Optional<User> optUser = userService.confirmRegistration(token);
        boolean success;

        final ModelAndView mv = new ModelAndView("user/confirmRegistration/registrationConfirm");

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
    public ModelAndView showResetPassword(@ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm) {
        return new ModelAndView("user/resetPassword/resetPassword");
    }

    @RequestMapping(path = "/user/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@Valid @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm, final BindingResult bindingResult) {

        if(bindingResult.hasErrors())
            return showResetPassword(resetPasswordForm);

        final Optional<User> optUser = userService.findUserByEmail(resetPasswordForm.getEmail());

        if(!optUser.isPresent()) {
            bindingResult.rejectValue("email", "validation.resetPassword.InvalidEmail");
            return showResetPassword(resetPasswordForm);
        }

        final User user = optUser.get();

        if(!user.isValidated()) {
            bindingResult.rejectValue("email", "validation.resetPassword.EmailNotValidated");
            return showResetPassword(resetPasswordForm);
        }

        userService.createPasswordResetEmail(user, "passwordResetEmail");

        final ModelAndView mv = new ModelAndView("user/resetPassword/resetPasswordTokenGenerated");

        mv.addObject("loggedUser", user);

        return mv;
    }

    @RequestMapping(path = "/user/resendConfirmation", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(Principal principal) {

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.createConfirmationEmail(user, "confirmEmail");

        final ModelAndView mv = new ModelAndView("user/confirmRegistration/resendConfirmation");

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

        return new ModelAndView("user/resetPassword/updatePasswordError");
    }

    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.GET)
    public ModelAndView showUpdatePassword(@ModelAttribute("updatePasswordForm") final UpdatePasswordForm updatePasswordForm,
                                           HttpServletRequest request) {

        final Map<String, ?> flashParams = RequestContextUtils.getInputFlashMap(request);

        if(flashParams != null && flashParams.containsKey("token"))
            updatePasswordForm.setToken((String) flashParams.get("token"));

        return new ModelAndView("user/resetPassword/updatePassword");
    }

    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@Valid @ModelAttribute("updatePasswordForm") final UpdatePasswordForm updatePasswordForm,
                                       final BindingResult bindingResult, HttpServletRequest request) {

        if(bindingResult.hasErrors())
            return showUpdatePassword(updatePasswordForm, request);

        final User user = userService.updatePassword(updatePasswordForm.getPassword(), updatePasswordForm.getToken())
                .orElseThrow(InvalidResetPasswordToken::new);

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        final ModelAndView mv = new ModelAndView("user/resetPassword/passwordResetSuccess");

        mv.addObject("loggedUser", user);

        return mv;
    }

    @RequestMapping(path = "/user/avatar/{avatarId}", method = RequestMethod.GET, produces = "image/*")
    public @ResponseBody byte[] getAvatar(@PathVariable long avatarId) {

        return userService.getAvatar(avatarId).orElseThrow(ImageNotFoundException::new);
    }

    private void manualLogin(HttpServletRequest request, String username, String password, Collection<Role> roles) {

        final PreAuthenticatedAuthenticationToken token =
                new PreAuthenticatedAuthenticationToken(username, password, getGrantedAuthorities(roles));

        SecurityContextHolder.getContext().setAuthentication(token);

        //this step is important, otherwise the new login is not in session which is required by Spring Security
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        return roles.stream().map((role) -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).collect(Collectors.toList());
    }

    private User getUserFromFlashParamsOrById(long userId, HttpServletRequest request) {

        final Map<String, ?> flashParams = RequestContextUtils.getInputFlashMap(request);

        if(flashParams != null && flashParams.containsKey("user"))
            return (User) flashParams.get("user");

        else
            return userService.findUserById(userId).orElseThrow(UserNotFoundException::new);
    }
}
