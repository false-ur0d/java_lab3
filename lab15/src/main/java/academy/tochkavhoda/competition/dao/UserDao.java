package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;

public interface UserDao {
    void insert(User user) throws ServerException;

    User getByLogin(String login);

    String login(User user, String token);

    User getByToken(String token) throws ServerException;

    void logout(String token);

    void delete(User user);
}
