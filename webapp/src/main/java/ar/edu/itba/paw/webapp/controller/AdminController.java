package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
public class AdminController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private SearchService searchService;

    @RequestMapping(path = "/admin/deleted/posts", method = RequestMethod.GET)
    public ModelAndView deletedPosts(@RequestParam(defaultValue = "") final String query,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        ModelAndView mv = new ModelAndView("adminPanel/deleted/posts");
        mv.addObject("query", query);
        mv.addObject("posts", searchService.searchDeletedPosts(query, pageNumber, pageSize));
        return mv;
    }

    @RequestMapping(path = "/admin/deleted/comments", method = RequestMethod.GET)
    public ModelAndView deletedComments(@RequestParam(defaultValue = "") final String query,
                                        @RequestParam(defaultValue = "5") final int pageSize,
                                        @RequestParam(defaultValue = "0") final int pageNumber) {

        ModelAndView mv = new ModelAndView("adminPanel/deleted/comments");
        mv.addObject("query", query);
        mv.addObject("comments", searchService.searchDeletedComments(query, pageNumber, pageSize));
        return mv;
    }

    @RequestMapping(path = "/admin/deleted/users", method = RequestMethod.GET)
    public ModelAndView deletedUsers(@RequestParam(defaultValue = "") final String query,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        ModelAndView mv = new ModelAndView("adminPanel/deleted/users");
        mv.addObject("query", query);
        mv.addObject("users", searchService.searchDeletedUsers(query, pageNumber, pageSize));
        return mv;
    }

    //    ================ COMMENTS PRIVILEGES ================

    @RequestMapping(path = "/comment/delete/{id}", method = RequestMethod.POST)
    public ModelAndView deleteComment(@PathVariable final long id) {

        commentService.delete(id);

        return new ModelAndView("redirect:/comment/" + id);
    }

    @RequestMapping(path = "/comment/restore/{id}", method = RequestMethod.POST)
    public ModelAndView restoreComment(@PathVariable final long id) {

        commentService.restore(id);

        return new ModelAndView("redirect:/comment/" + id);
    }

    //    ================ POSTS PRIVILEGES ================

    @RequestMapping(path = "/post/delete/{postId}", method = RequestMethod.POST)
    public ModelAndView deletePost(@PathVariable final long postId) {

        postService.delete(postId);

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/post/restore/{postId}", method = RequestMethod.POST)
    public ModelAndView restorePost(@PathVariable final long postId) {

        postService.restore(postId);

        return new ModelAndView("redirect:/post/" + postId);
    }

    //    ================ USERS PRIVILEGES ================

    @RequestMapping(path = "/user/promote/{id}", method = RequestMethod.POST)
    public ModelAndView promoteUser(@PathVariable long id, RedirectAttributes redirectAttributes) {

        final User user = userService.findById(id).orElseThrow(UserNotFoundException::new);

        userService.promoteUserToAdmin(user);

        redirectAttributes.addFlashAttribute("user", user);

        return new ModelAndView("redirect:/user/" + user.getId());
    }

    @RequestMapping(path = "/user/delete/{id}", method = RequestMethod.POST)
    public ModelAndView deleteUser(@PathVariable long id) {

        userService.delete(id);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/user/restore/{id}", method = RequestMethod.POST)
    public ModelAndView restoreUser(@PathVariable long id) {

        userService.restore(id);
        return new ModelAndView("redirect:/user/" + id);
    }
}
