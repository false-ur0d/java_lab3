package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

import java.util.List;
import java.util.Set;

public interface ApplicationDao {
    int insert(Application application) throws ServerException;

    Application getById(int id) throws ServerException;

    void delete(Application application) throws ServerException;

    List<Application> getByDirections(Set<String> directions);

    List<Application> getRatedBy(Expert expert);

    List<Application> getAll();

    void setRating(Application application, Expert expert, int rating) throws ServerException;

    void deleteRating(Application application, Expert expert) throws ServerException;

    void finishCompetition() throws ServerException;
}
