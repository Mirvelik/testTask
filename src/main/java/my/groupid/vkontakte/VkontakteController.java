package my.groupid.vkontakte;

import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by root on 11.06.15.
 */

@Controller
public class VkontakteController {

    private final VKontakte vkontakte;

    @Inject
    public VkontakteController(VKontakte vkontakte) {
        this.vkontakte = vkontakte;
    }

    @RequestMapping(value="/vkontakte/friends", method= RequestMethod.GET)
    public String showFeed(Model model) {
        model.addAttribute("friends", vkontakte.friendsOperations().get());
        return "vkontakte/friends";
    }
}
