package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.webapp.exceptions.MovieNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.NonExistingSearchCriteriaException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.SearchMoviesForm;
import ar.edu.itba.paw.webapp.form.SearchPostsForm;
import ar.edu.itba.paw.webapp.form.SearchUsersForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping(path = "/search/posts/", method = RequestMethod.GET)
    public ModelAndView searchPosts(@ModelAttribute("searchPostsForm") final SearchPostsForm searchPostsForm) {

        final ModelAndView mv = new ModelAndView("search/posts");

        mv.addObject("query", searchPostsForm.getQuery());
        mv.addObject("posts",
                searchService.searchPosts(searchPostsForm.getQuery(), searchPostsForm.getPostCategory(), searchPostsForm.getPostAge(), searchPostsForm.getSortCriteria()).orElseThrow(NonExistingSearchCriteriaException::new));
        return mv;
    }

    @RequestMapping(path = "/search/movies/", method = RequestMethod.GET)
    public ModelAndView searchMovies(@ModelAttribute("searchMoviesForm") final SearchMoviesForm searchMoviesForm) {

        final ModelAndView mv = new ModelAndView("search/movies");
        mv.addObject("query", searchMoviesForm.getQuery());
        mv.addObject("movies",
                searchService.searchMovies(searchMoviesForm.getQuery()).orElseThrow(MovieNotFoundException::new));
        return mv;
    }

    @RequestMapping(path = "/search/users/", method = RequestMethod.GET)
    public ModelAndView searchUsers(@ModelAttribute("searchUsersForm") final SearchUsersForm searchUsersForm) {

        final ModelAndView mv = new ModelAndView("search/users");

        mv.addObject("query", searchUsersForm.getQuery());
        mv.addObject("users",
                searchService.searchUsers(searchUsersForm.getQuery()).orElseThrow(UserNotFoundException::new));

        return mv;
    }

}

