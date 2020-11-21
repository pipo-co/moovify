package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.annotations.TagSize;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Set;

public class PostCreateForm {

    @Size(min = 6, max = 200)
    private String title;

    @Size(min = 1, max = 100000)
    private String body;

    @Min(1)
    private long category;

    @Size(max = 5)
    @TagSize(max = 50)
    private Set<String> tags;

    @Size(min=1, max = 20)
    private Set<Long> movies;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<Long> getMovies() {
        return movies;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public void setMovies(Set<Long> movies) {
        this.movies = movies;
    }

}
