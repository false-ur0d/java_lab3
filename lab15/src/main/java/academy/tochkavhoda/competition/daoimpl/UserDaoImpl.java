package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;

public class UserDaoImpl implements UserDao {
    private final Database database = Database.getInstance();

    @Override
    public void insert(User user) throws ServerException {
        database.insertUser(user);
    }

    @Override
    public User getByLogin(String login) {
        return database.getUserByLogin(login);
    }

    @Override
    public String login(User user, String token) {
        return database.login(user, token);
    }

    @Override
    public User getByToken(String token) throws ServerException {
        return database.getUserByToken(token);
    }

    @Override
    public void logout(String token) {
        database.logout(token);
    }

    @Override
    public void delete(User user) {
        database.deleteUser(user);
    }
}
