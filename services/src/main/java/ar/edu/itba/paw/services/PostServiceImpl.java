package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostCategoryDao categoryDao;

    @Autowired
    private MovieDao movieDao;

    @Transactional
    @Override
    public Post register(String title, String body, PostCategory category, User user, Set<String> tags, Set<Long> moviesId) {

        Objects.requireNonNull(body);

        final Collection<Movie> movies = movieDao.findMoviesById(moviesId);

        final Post post = postDao.register(title, body.trim(),
                body.split("\\s+").length, category, user, tags, new HashSet<>(movies), true);

        LOGGER.info("Created Post {}", post.getId());

        return post;
    }

    @Transactional
    @Override
    public void deletePost(Post post) {
        LOGGER.info("Delete Post {}", post.getId());
        post.delete();
    }

    @Transactional
    @Override
    public void restorePost(Post post) {
        LOGGER.info("Restore Post {}", post.getId());
        post.restore();
    }

    @Transactional
    @Override
    public void likePost(Post post, User user, int value) {

        if(value == 0) {
            LOGGER.info("Delete Like: User {} Post {}", user.getId(), post.getId());
            post.removeLike(user);
        }

        else if(value == -1 || value == 1) {
            LOGGER.info("Like: User {} Post {} Value {}", user.getId(), post.getId(), value);
            post.like(user, value);
        }
    }

    @Transactional
    @Override
    public void editPost(Post post, String newBody) {
        Objects.requireNonNull(newBody);

        post.setBody(newBody.trim());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Post> findPostById(long id) {
        return postDao.findPostById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Post> findDeletedPostById(long id) {
        return postDao.findDeletedPostById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> findPostsByMovie(Movie movie, int pageNumber, int pageSize) {
        return postDao.findPostsByMovie(movie, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> findPostsByUser(User user, int pageNumber, int pageSize) {
        return postDao.findPostsByUser(user, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getAllPostsOrderByNewest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getAllPostsOrderByOldest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.OLDEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getAllPostsOrderByHottest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.HOTTEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getUserFavouritePosts(User user, int pageNumber, int pageSize) {
        return postDao.getUserFavouritePosts(user, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<PostCategory> getAllPostCategories() {
        return categoryDao.getAllPostCategories();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PostCategory> findCategoryById(long categoryId) {
        return categoryDao.findPostCategoryById(categoryId);
    }
}