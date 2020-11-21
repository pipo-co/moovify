package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;
import ar.edu.itba.paw.models.PaginatedCollection;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface MovieService {

    Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage,
                   String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> categories);

    Optional<Movie> findMovieById(long id);

    Optional<byte[]> getPoster(long posterId);

    PaginatedCollection<Movie> getAllMovies(int pageNumber, int pageSize);

    Collection<Movie> getAllMoviesNotPaginated();

    Collection<MovieCategory> getAvailableCategories();
}
