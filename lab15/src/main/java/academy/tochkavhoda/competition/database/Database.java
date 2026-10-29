package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Database {
    private static final Database INSTANCE = new Database();

    private final Map<String, User> usersByLogin = new HashMap<>();
    // token <-> user, at most one active session per user
    private final BidiMap<String, User> sessions = new DualHashBidiMap<>();
    private final Map<Integer, Application> applications = new TreeMap<>();
    private int nextApplicationId = 1;
    private boolean finished = false;

    private Database() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public void insertUser(User user) throws ServerException {
        if (usersByLogin.putIfAbsent(user.getLogin(), user) != null) {
            throw new ServerException(ServerErrorCode.LOGIN_ALREADY_EXISTS);
        }
    }

    public User getUserByLogin(String login) {
        return usersByLogin.get(login);
    }

    public String login(User user, String token) {
        sessions.removeValue(user);
        sessions.put(token, user);
        return token;
    }

    public User getUserByToken(String token) throws ServerException {
        User user = token == null ? null : sessions.get(token);
        if (user == null) {
            throw new ServerException(ServerErrorCode.WRONG_TOKEN);
        }
        return user;
    }

    public void logout(String token) {
        sessions.remove(token);
    }

    public void deleteUser(User user) {
        sessions.removeValue(user);
        usersByLogin.remove(user.getLogin());
        if (user instanceof Participant) {
            applications.values().removeIf(application -> application.getParticipant() == user);
        } else if (user instanceof Expert) {
            for (Application application : applications.values()) {
                application.getRatings().remove(user.getLogin());
            }
        }
    }

    public int insertApplication(Application application) throws ServerException {
        checkNotFinished();
        application.setId(nextApplicationId++);
        applications.put(application.getId(), application);
        return application.getId();
    }

    public Application getApplicationById(int id) throws ServerException {
        Application application = applications.get(id);
        if (application == null) {
            throw new ServerException(ServerErrorCode.APPLICATION_NOT_FOUND);
        }
        return application;
    }

    public void deleteApplication(Application application) throws ServerException {
        checkNotFinished();
        applications.remove(application.getId());
    }

    public List<Application> getApplicationsByDirections(Set<String> directions) {
        List<Application> result = new ArrayList<>();
        for (Application application : applications.values()) {
            if (!Collections.disjoint(application.getDirections(), directions)) {
                result.add(application);
            }
        }
        return result;
    }

    public List<Application> getApplicationsRatedBy(Expert expert) {
        List<Application> result = new ArrayList<>();
        for (Application application : applications.values()) {
            if (application.getRatings().containsKey(expert.getLogin())) {
                result.add(application);
            }
        }
        return result;
    }

    public List<Application> getAllApplications() {
        return new ArrayList<>(applications.values());
    }

    public void setRating(Application application, Expert expert, int rating) throws ServerException {
        checkNotFinished();
        application.getRatings().put(expert.getLogin(), rating);
    }

    public void deleteRating(Application application, Expert expert) throws ServerException {
        checkNotFinished();
        if (application.getRatings().remove(expert.getLogin()) == null) {
            throw new ServerException(ServerErrorCode.RATING_NOT_FOUND);
        }
    }

    public void finish() throws ServerException {
        checkNotFinished();
        finished = true;
    }

    public void clear() {
        usersByLogin.clear();
        sessions.clear();
        applications.clear();
        nextApplicationId = 1;
        finished = false;
    }

    private void checkNotFinished() throws ServerException {
        if (finished) {
            throw new ServerException(ServerErrorCode.COMPETITION_FINISHED);
        }
    }
}
