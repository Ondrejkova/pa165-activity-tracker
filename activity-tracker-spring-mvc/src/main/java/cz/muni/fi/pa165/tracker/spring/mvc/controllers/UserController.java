package cz.muni.fi.pa165.tracker.spring.mvc.controllers;

import cz.muni.fi.pa165.tracker.dto.UserDTO;
import cz.muni.fi.pa165.tracker.enums.UserRole;
import cz.muni.fi.pa165.tracker.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * @author Petra Onrejkova
 * @version 8.12.2016
 */
@Controller
@RequestMapping(value = {"/"})
public class UserController extends ActivityTrackerController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Inject
    private UserFacade userFacade;

    @RequestMapping(value = {"settings", "settings/"}, method = RequestMethod.GET)
    public String settings(Model model) {
        log.debug("update userUpdate({})", getLoggedUser());
        return "user/settings";
    }

    @RequestMapping(value = {"settings", "settings/"}, method = RequestMethod.POST)
    public String settings(
            @Valid @ModelAttribute("loggedUser") UserDTO formData,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        UserDTO notUpdated = getLoggedUser();
        formData.setId(notUpdated.getId());

        log.debug("update user({})", formData);

        if (bindingResult.hasErrors()) {
            addValidationErrors(bindingResult, model);
            return "user/settings";
        }

        userFacade.updateUser(formData);
        redirectAttributes.addFlashAttribute("alert_success", "User " + formData.getEmail() + " was updated");

        return "redirect:";
    }

    @RequestMapping(value = {"users", "users/"}, method = RequestMethod.GET)
    public String index(Model model) {
        log.info("users()" + userFacade.findAll());
        model.addAttribute("users", userFacade.findAll());
        return "user/users";
    }

    @RequestMapping(value = {"/users/makeAdmin/{id}"}, method = RequestMethod.GET)
    public String makeAdmin(@PathVariable long id, Model model, UriComponentsBuilder uriBuilder,
                         RedirectAttributes redirectAttributes) {
        log.info("make admin from user with id", id);
        UserDTO updated = userFacade.findUserById(id);
        updated.setRole(UserRole.ADMIN);
        userFacade.updateUser(updated);
        return "redirect:" + uriBuilder.path("/users").toUriString();
    }

    @RequestMapping(value = "users/remove/{id}", method = RequestMethod.POST)
    public String remove(@PathVariable long id, Model model, UriComponentsBuilder uriBuilder,
                         RedirectAttributes redirectAttributes) {

        try {
            userFacade.removeUser(userFacade.findUserById(id));
            redirectAttributes.addFlashAttribute("alert_success", "User with id " + id + " deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alert_danger", "User with id " + id + " can not be deleted");
        }
        return "redirect:" + uriBuilder.path("/users").toUriString();
    }
}
