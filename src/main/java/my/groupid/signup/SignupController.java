package my.groupid.signup;

import javax.inject.Inject;
import javax.validation.Valid;

import my.groupid.signin.SignInUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import my.groupid.account.*;
import my.groupid.support.web.*;

import java.awt.*;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;
    private final AccountRepository accountRepository;
    private final ProviderSignInUtils providerSignInUtils;
    private static final String SIGNUP_VIEW_NAME = "signup/signup";

    @Inject
    public SignupController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.providerSignInUtils = new ProviderSignInUtils();
    }


    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(WebRequest request, Model model) {

        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        if (connection != null) {
            request.setAttribute(
                    "message",
                    new Message(StringUtils.capitalize(connection.getKey().getProviderId()), Message.Type.INFO),
                    WebRequest.SCOPE_REQUEST
            );
            model.addAttribute(SignupForm.fromProviderUser(connection.fetchUserProfile()));
        } else {
            model.addAttribute(new SignupForm());
        }

        return SIGNUP_VIEW_NAME;
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public String signup(@Valid @ModelAttribute SignupForm signupForm,
                         Errors errors, RedirectAttributes ra, WebRequest request) {

        if (errors.hasErrors()) {
            return SIGNUP_VIEW_NAME;
        }
        Account account = accountRepository.save(signupForm.createAccount());
        userService.signin(account);
        SignInUtils.signin(account.getEmail());
        providerSignInUtils.doPostSignUp(account.getEmail(), request);
        // see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
        MessageHelper.addSuccessAttribute(ra, "signup.success");
        return "redirect:/";
    }

}
