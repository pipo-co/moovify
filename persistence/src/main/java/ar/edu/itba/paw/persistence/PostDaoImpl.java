package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Repository
public class PostDaoImpl implements PostDao {

    // Constants with Table Names
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String MOVIES = TableNames.MOVIES.getTableName();
    private static final String POST_MOVIE = TableNames.POST_MOVIE.getTableName();
    private static final String TAGS = TableNames.TAGS.getTableName();
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();

    // Use each MAPPER with it's corresponding SELECT Macro. Update them together.

    // Mapper and Select for simple post retrieval.
    private static final String BASE_POST_SELECT = "SELECT " +
            // Posts Table Columns - Alias: p_column_name
            POSTS + ".post_id p_post_id, " + POSTS + ".creation_date p_creation_date, " + POSTS + ".title p_title, " +
            POSTS + ".body p_body, " + POSTS + ".word_count p_word_count, " + POSTS + ".email p_email";

    private static final String TAGS_SELECT = TAGS + ".tag p_tag";

    private static final String MOVIES_SELECT =
            MOVIES + ".movie_id m_movie_id, " + MOVIES + ".creation_date m_creation_date, " + MOVIES + ".title m_title, " +
            MOVIES + ".premier_date m_premier_date";

    private static final String BASE_POST_FROM = "FROM " + POSTS;

    private static final String TAGS_FROM = "LEFT OUTER JOIN " + TAGS + " ON " + POSTS + ".post_id = " + TAGS + ".post_id";

    private static final String MOVIES_FROM =
            "LEFT OUTER JOIN (" +
                    " SELECT " + MOVIES + ".movie_id, " + MOVIES + ".creation_date, " +
                    MOVIES + ".title, " + MOVIES + ".premier_date, " + "post_id" +
                    " FROM "+ POST_MOVIE +
                    " INNER JOIN " + MOVIES + " ON " + POST_MOVIE+ ".movie_id = " + MOVIES + ".movie_id" +
                    ") " + MOVIES + " on " + MOVIES + ".post_id = " + POSTS + ".post_id";

    private static final ResultSetExtractor<Collection<Post>> POST_ROW_MAPPER = (rs) -> {
        Map<Long, Post> resultMap = new HashMap<>();
        long post_id;

        while(rs.next()){
            post_id = rs.getLong("p_post_id");

            if(!resultMap.containsKey(post_id)){
                resultMap.put(post_id,
                        new Post(
                                post_id, rs.getObject("p_creation_date", LocalDateTime.class),
                                rs.getString("p_title"), rs.getString("p_body"),
                                rs.getInt("p_word_count"), rs.getString("p_email"),
                                new HashSet<>(), new ArrayList<>(), null // No haria falta que la coleccion de tags sea un set ya que no debieran haber tags repetidos, pero por si mas adelante cambia al agregar comentarios se dejo como uno
                        )
                );
            }
            String tag = rs.getString("p_tag");
            // If movies is not null. (Returns 0 on null)
            if(tag != null)
                resultMap.get(post_id).getTags().add(tag);

        }

        return resultMap.values();
    };

    private static final ResultSetExtractor<Collection<Post>> POST_ROW_MAPPER_WITH_MOVIES = (rs) -> {
        Map<Long, Post> resultMap = new HashMap<>();
        Map<Long, Map<Long, Movie>> movieMap = new HashMap<>();
        long post_id;

        while(rs.next()){
            post_id = rs.getLong("p_post_id");

            if(!resultMap.containsKey(post_id)){
                resultMap.put(post_id,
                        new Post(
                                post_id, rs.getObject("p_creation_date", LocalDateTime.class),
                                rs.getString("p_title"), rs.getString("p_body"),
                                rs.getInt("p_word_count"), rs.getString("p_email"),
                                new HashSet<>(), new ArrayList<>(), null
                        )
                );
            }

            long movie_id = rs.getLong("m_movie_id");
            // If movies is not null. (Returns 0 on null)
            if( movie_id != 0) {
                if (!movieMap.containsKey(post_id))
                    movieMap.put(post_id, new HashMap<>());

                if (!movieMap.get(post_id).containsKey(movie_id))
                    movieMap.get(post_id).put(movie_id, new Movie( movie_id, rs.getObject("m_creation_date", LocalDateTime.class),
                            rs.getString("m_title"), rs.getObject("m_premier_date", LocalDate.class)));
            }

            String tag = rs.getString("p_tag");

            if (tag != null)
                resultMap.get(post_id).getTags().add(tag);

        }
        Collection<Post> posts = resultMap.values();

        for (Post p: posts) {
            if(movieMap.containsKey(p.getId()))
                p.getMovies().addAll(movieMap.get(p.getId()).values());
        }

        return posts;
    };

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert postInsert;
    private final SimpleJdbcInsert postMoviesInsert;
    private final SimpleJdbcInsert tagsInsert;

    @Autowired
    public PostDaoImpl(final DataSource ds){

        jdbcTemplate = new JdbcTemplate(ds);

        postInsert = new SimpleJdbcInsert(ds)
                .withTableName(POSTS)
                .usingGeneratedKeyColumns("post_id");

        postMoviesInsert = new SimpleJdbcInsert(ds)
                .withTableName(POST_MOVIE);

        tagsInsert = new SimpleJdbcInsert(ds)
                .withTableName(TAGS);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + POSTS + " (" +
                "post_id SERIAL PRIMARY KEY," +
                "creation_date TIMESTAMP NOT NULL," +
                "title VARCHAR(50) NOT NULL," +
                "email VARCHAR(40) NOT NULL," +
                "word_count INTEGER," +
                "body VARCHAR )"
        );

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + POST_MOVIE + " (" +
                "post_id integer," +
                "movie_id integer," +
                "PRIMARY KEY (post_id, movie_id)," +
                "FOREIGN KEY (post_id) REFERENCES " + POSTS + " (post_id)," +
                "FOREIGN KEY (movie_id) REFERENCES " + MOVIES + " (movie_id))"
        );

        jdbcTemplate.execute( "CREATE TABLE IF NOT EXISTS " + TAGS + " (" +
                "post_id integer," +
                "tag VARCHAR(30) NOT NULL," +
                "PRIMARY KEY (post_id, tag)," +
                "FOREIGN KEY (post_id) REFERENCES " + POSTS + " (post_id))");
    }


    @Override
    public Post register(String title, String email, String body, Collection<String> tags, Set<Long> movies) {

        body = body.trim();
        LocalDateTime creationDate = LocalDateTime.now();
        int wordCount = body.split("\\s+").length;

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("email", email);
        map.put("word_count", wordCount);
        map.put("body", body);
        map.put("tags", tags);

        final long postId = postInsert.executeAndReturnKey(map).longValue();

        for(Long movie_id: movies){
            map = new HashMap<>();
            map.put("movie_id", movie_id);
            map.put("post_id", postId);
            postMoviesInsert.execute(map);
        }

        for(String tag: tags){
            map = new HashMap<>();
            map.put("tag", tag);
            map.put("post_id", postId);
            tagsInsert.execute(map);
        }

        return new Post(postId, creationDate, title, body, wordCount, email, tags, null, null);
    }

    // This two methods abstract the logic needed to perform select queries with or without movies.
    private Collection<Post> findPostsBy(String queryAfterFrom, Object[] args, boolean withMovies){

        final String select = BASE_POST_SELECT
                + ", " + TAGS_SELECT
                + (withMovies? ", " + MOVIES_SELECT : "");

        final String from = BASE_POST_FROM
               + " " + TAGS_FROM
                + (withMovies? " " + MOVIES_FROM : "");

        final String query = select + " " + from + " " + queryAfterFrom;

        if(args != null) {
            if (withMovies)
                return jdbcTemplate.query(query, args, POST_ROW_MAPPER_WITH_MOVIES);
            else
                return jdbcTemplate.query(query, args, POST_ROW_MAPPER);
        } else {
            if(withMovies)
                return jdbcTemplate.query(query, POST_ROW_MAPPER_WITH_MOVIES);
            else
                return jdbcTemplate.query(query, POST_ROW_MAPPER);
        }
    }

    private Collection<Post> findPostsBy(String queryAfterFrom, boolean withMovies){
        return findPostsBy(queryAfterFrom, null, withMovies);
    }

    @Override
    public Optional<Post> findPostById(long id, boolean withMovies){
        return findPostsBy(
                " WHERE " + POSTS + ".post_id = ?", new Object[]{ id }, withMovies).stream().findFirst();
    }

    @Override
    public Collection<Post> findPostsByTitle(String title, boolean withMovies) {
        return findPostsBy(
                " WHERE " + POSTS + ".title ILIKE '%' || ? || '%' " +
                        "ORDER BY " + POSTS + ".creation_date", new Object[] { title }, withMovies);
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies) {
        return findPostsBy(
                " WHERE " + POSTS + ".post_id IN ( " +
                        "SELECT " + POST_MOVIE + ".post_id " +
                        "FROM " + POST_MOVIE +
                        " WHERE " + POST_MOVIE + ".movie_id = ?) " +
                        "ORDER BY " + POSTS + ".creation_date", new Object[] { movie_id }, withMovies);
    }

    @Override
    public Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies) {
        return findPostsBy(
                " WHERE " + POSTS + ".post_id IN ( " +
                        "SELECT " + POSTS + ".post_id " +
                        "FROM " + POST_MOVIE +
                        " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                        "WHERE " + POSTS + ".title ILIKE '%' || ? || '%') " +
                        "ORDER BY " + POSTS + ".creation_date", new Object[] { movie_title }, withMovies);
    }

    @Override
    public Collection<Post> getAllPosts(boolean withMovies) {
        return findPostsBy(" ORDER BY " + POSTS + ".creation_date", withMovies);
    }

    @Override
    public Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies) {
        return findPostsBy(
                " WHERE " + POSTS + ".title ILIKE '%' || ? || '%'" +
                "OR " + POSTS + ".post_id in ( " +
                "SELECT " + POST_MOVIE + ".post_id " +
                "FROM " + POST_MOVIE +
                " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                "WHERE " + MOVIES + ".title ILIKE '%' || ? || '%') " +
                "ORDER BY " + POSTS + ".creation_date", new Object[] {title, title}, withMovies);
    }

}