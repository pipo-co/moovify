package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {

    User register(String username, String password, String name, String email, String description, byte[] avatar, String confirmationMailTemplate);

    void updateName(User user, String name);

    void updateUsername(User user, String username);

    void updateDescription(User user, String description);

    void updatePassword(User user, String password);

    void delete(long user_id);

    void restore(long user_id);

    void promoteUserToAdmin(User user);

    Optional<User> confirmRegistration(String token);

    void createConfirmationEmail(User user, String confirmationMailTemplate);

    void createPasswordResetEmail(User user, String passwordResetMailTemplate);

    boolean validatePasswordResetToken(String token);

    int hasUserLiked(long user_id, long post_id);

    Optional<User> updatePassword(String password, String token);

    Optional<byte[]> getAvatar(long avatarId);

    void updateAvatar(User user, byte[] newAvatar);

    boolean emailExistsAndIsValidated(String email);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    PaginatedCollection<User> getAllUsers(int pageNumber, int pageSize);

    PaginatedCollection<User> getDeletedUsers(int pageNumber, int pageSize);
}
