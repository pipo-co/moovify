package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    public static final String TABLE_NAME = "users";
    public static final String USER_ROLE_TABLE_NAME = "user_role";
    public static final String USER_FAV_POST = "user_fav_post";
    public static final String USERS_FOLLOWS = "users_follows";

    public static final long DEFAULT_AVATAR_ID = 0;

    public static boolean userHasFavedPost(User user, Post post) {
        return user.isPostFavourite(post);
    }

    static public boolean hasUserFollowed(User user, User followedUser) {
        return user.getFollowing().contains(followedUser);
    }

    static public boolean hasDescription(User user){
        return user.getDescription().isEmpty();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;

    @Column(nullable = false, unique = true, length = 50)
    @Basic(optional = false)
    private String username;

    @Column(nullable = false, length = 200)
    @Basic(optional = false)
    private String password;

    @Column(nullable = false, length = 50)
    @Basic(optional = false)
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    @Basic(optional = false)
    private String email;

    @Column(nullable = false, length = 400)
    @Basic(optional = false)
    private String description;

    @Column(nullable = false, length = 15)
    @Basic(optional = false)
    private String language;

    @OneToOne(optional = true, fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id", referencedColumnName = "image_id")
    private Image avatar;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<PostLike> postLikes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<CommentLike> commentLikes;

    @Transient
    private Long totalLikes;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = USER_ROLE_TABLE_NAME,
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Post> posts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_follows",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_follow_id")
    )
    private Set<User> following;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = USER_FAV_POST,
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> favouritePosts;

    @Column(nullable = false)
    private boolean enabled;

    public User(long id, LocalDateTime creationDate, String username, String password, String name, String email, String description, String language, Image avatar, Set<Role> roles, boolean enabled, Set<PostLike> postLikes, Set<CommentLike> commentLikes, Set<Post> posts, Set<Comment> comments, Set<User> following, Set<Post> favouritePosts) {
        this(creationDate, username, password, name, email, description, language, avatar, roles, enabled, postLikes, commentLikes, posts, comments, following, favouritePosts);
        this.id = id;
    }

    public User(LocalDateTime creationDate, String username, String password, String name, String email, String description, String language, Image avatar, Set<Role> roles, boolean enabled, Set<PostLike> postLikes, Set<CommentLike> commentLikes, Set<Post> posts, Set<Comment> comments, Set<User> following, Set<Post> favouritePosts) {
        this.creationDate = creationDate;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.description = description;
        this.language = language;
        this.avatar = avatar;
        this.roles = roles;
        this.enabled = enabled;
        this.postLikes = postLikes;
        this.commentLikes = commentLikes;
        this.posts = posts;
        this.comments = comments;
        this.following = following;
        this.favouritePosts = favouritePosts;
    }

    protected User() {
        //JPA
    }

    @PostLoad
    public void calculateTotalLikes() {
        if(totalLikes == null) {
            final long totalPostLikes = postLikes.stream()
                    .reduce(0L, (acum, postLike) -> acum += (long) postLike.getValue(), Long::sum);

            final long totalCommentLikes = commentLikes.stream()
                    .reduce(0L, (acum, commentLike) -> acum += (long) commentLike.getValue(), Long::sum);

            totalLikes = totalPostLikes + totalCommentLikes;
        }
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        getRoles().add(role);
    }

    public void removeRole(Role role) {
        getRoles().removeIf(r -> r.equals(role));
    }

    public Collection<Post> getPosts() {
        return posts;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Collection<CommentLike> getCommentLikes() {
        return commentLikes;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void removeCommentLike(CommentLike like) {
        getCommentLikes().remove(like);
    }

    public void addCommentLike(CommentLike like) {
        getCommentLikes().add(like);
    }

    public Collection<PostLike> getPostLikes() {
        return postLikes;
    }

    public void removePostLike(PostLike like) {
        getPostLikes().remove(like);
    }

    public void addPostLike(PostLike like) {
        getPostLikes().add(like);
    }

    public boolean hasRole(Role role) {
        return getRoles().stream().anyMatch(r -> r.equals(role));
    }

    public Collection<Post> getFavouritePosts() {
        return favouritePosts;
    }

    public boolean isPostFavourite(Post post) {
        return favouritePosts.contains(post);
    }

    public void addFavouritePost(Post post) {
        favouritePosts.add(post);
    }

    public void removeFavouritePost(Post post) {
        favouritePosts.remove(post);
    }

    public Collection<User> getFollowingUsers() {
        return following;
    }

    public void followUser(User user) {
        getFollowing().add(user);
    }

    public void unfollowUser(User user) {
        getFollowing().remove(user);
    }

    public Duration getTimeSinceCreation() {
        return Duration.between(creationDate, LocalDateTime.now());
    }

    public long getDaysSinceCreation() {
        return getTimeSinceCreation().toDays();
    }

    public long getHoursSinceCreation() {
        return getTimeSinceCreation().toHours();
    }

    public long getMinutesSinceCreation() {
        return getTimeSinceCreation().toMinutes();
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getAvatarId() {
        if(avatar == null)
            return DEFAULT_AVATAR_ID;

        return avatar.getId();
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    public boolean isValidated() {
        return hasRole(Role.USER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", roles=" + roles +
                ", enabled=" + enabled +
                '}';
    }
}
