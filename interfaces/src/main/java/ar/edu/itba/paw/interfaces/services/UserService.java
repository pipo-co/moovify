package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {

    User register(String username, String password, String name, String email, String description, byte[] avatar, String confirmationMailTemplate) throws DuplicateUniqueUserAttributeException;

    void updateName(User user, String name);

    void updateUsername(User user, String username) throws DuplicateUniqueUserAttributeException;

    void updateDescription(User user, String description);

    void updatePassword(User user, String password);

    void deleteUser(User user);

    void restoreUser(User user);

    void promoteUserToAdmin(User user);

    Optional<User> confirmRegistration(String token);

    void createConfirmationEmail(User user, String confirmationMailTemplate);

    void createPasswordResetEmail(User user, String passwordResetMailTemplate);

    boolean validatePasswordResetToken(String token);

    Optional<User> updatePassword(String password, String token);

    Optional<byte[]> getAvatar(long avatarId);

    void updateAvatar(User user, byte[] newAvatar);

    Optional<User> findUserById(long id);

    Optional<User> findDeletedUserById(long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    PaginatedCollection<User> getAllUsers(int pageNumber, int pageSize);
}
