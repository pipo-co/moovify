package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao.SortCriteria;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.exceptions.NonReachableStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@DependsOn("dataSourceInitializer")
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private UserDao userDao;

    private enum SearchOptions{
        BY_CATEGORY, OLDER_THAN
    }

    private final List<String> categoriesOptions;
    private final static Map<String, SortCriteria> sortCriteriaMap = getSortCriteriaMap();
    private final static Map<String, LocalDateTime> periodOptionsMap = getPeriodOptionsMap();

    private static Map<String, SortCriteria> getSortCriteriaMap(){
        Map<String, SortCriteria> sortCriteriaMap = new HashMap<>();
        sortCriteriaMap.put("newest", SortCriteria.NEWEST);
        sortCriteriaMap.put("oldest", SortCriteria.OLDEST);
        sortCriteriaMap.put("default", SortCriteria.NEWEST);
        return sortCriteriaMap;
    }

    private static Map<String, LocalDateTime> getPeriodOptionsMap(){
        Map<String, LocalDateTime> periodOptions = new HashMap<>();
        periodOptions.put("past-year", LocalDateTime.now().minusYears(1));
        periodOptions.put("past-month", LocalDateTime.now().minusMonths(1));
        periodOptions.put("past-week", LocalDateTime.now().minusWeeks(1));
        periodOptions.put("past-day", LocalDateTime.now().minusDays(1));
        return periodOptions;
    }

    @Autowired
    public SearchServiceImpl(PostCategoryDao postCategoryDao) {
        categoriesOptions = postCategoryDao.getAllPostCategories().stream().map(PostCategory::getName).collect(Collectors.toList());
    }

    @Override
    public Optional<Collection<Post>> searchPosts(String query, String category, String period, String sortCriteria) {

        Objects.requireNonNull(query);

        final EnumSet<SearchOptions> options = EnumSet.noneOf(SearchOptions.class);
        final SortCriteria sc;

        if(category != null && categoriesOptions.contains(category))
            options.add(SearchOptions.BY_CATEGORY);


        if(period != null && periodOptionsMap.containsKey(period))
            options.add(SearchOptions.OLDER_THAN);


        if(sortCriteria != null && sortCriteriaMap.containsKey(sortCriteria))
            sc = sortCriteriaMap.get(sortCriteria);

        else
            sc = sortCriteriaMap.get("default");


        if(options.isEmpty())
            return Optional.of(postDao.searchPosts(query, sc));

        else if(options.size() == 1){
            if(options.contains(SearchOptions.OLDER_THAN))
                return Optional.of(postDao.searchPostsOlderThan(query, periodOptionsMap.get(period), sc));
            else if(options.contains(SearchOptions.BY_CATEGORY))
                return Optional.of(postDao.searchPostsByCategory(query, category, sc));
        }
        else if(options.contains(SearchOptions.OLDER_THAN) && options.contains(SearchOptions.BY_CATEGORY) && options.size() == 2)
            return Optional.of(postDao.searchPostsByCategoryAndOlderThan(query, category, periodOptionsMap.get(period), sc));

        throw new NonReachableStateException();
    }

    @Override
    public Optional<Collection<Movie>> searchMovies(String query){
        Objects.requireNonNull(query);
        return Optional.of(movieDao.searchMovies(query));
    }

    @Override
    public Optional<Collection<User>> searchUsers(String query) {
        Objects.requireNonNull(query);
        return Optional.of(userDao.searchUsers(query));
    }
}
