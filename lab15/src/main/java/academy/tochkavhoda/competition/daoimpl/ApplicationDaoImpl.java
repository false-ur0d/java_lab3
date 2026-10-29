package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

import java.util.List;
import java.util.Set;

public class ApplicationDaoImpl implements ApplicationDao {
    private final Database database = Database.getInstance();

    @Override
    public int insert(Application application) throws ServerException {
        return database.insertApplication(application);
    }

    @Override
    public Application getById(int id) throws ServerException {
        return database.getApplicationById(id);
    }

    @Override
    public void delete(Application application) throws ServerException {
        database.deleteApplication(application);
    }

    @Override
    public List<Application> getByDirections(Set<String> directions) {
        return database.getApplicationsByDirections(directions);
    }

    @Override
    public List<Application> getRatedBy(Expert expert) {
        return database.getApplicationsRatedBy(expert);
    }

    @Override
    public List<Application> getAll() {
        return database.getAllApplications();
    }

    @Override
    public void setRating(Application application, Expert expert, int rating) throws ServerException {
        database.setRating(application, expert, rating);
    }

    @Override
    public void deleteRating(Application application, Expert expert) throws ServerException {
        database.deleteRating(application, expert);
    }

    @Override
    public void finishCompetition() throws ServerException {
        database.finish();
    }
}
