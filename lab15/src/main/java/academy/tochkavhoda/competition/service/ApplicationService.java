package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.ApplicationDaoImpl;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.GetApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.request.RateApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.SummarizeDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationListDtoResponse;
import academy.tochkavhoda.competition.dto.response.SummarizeDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static academy.tochkavhoda.competition.service.ServiceUtils.*;

public class ApplicationService {
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final UserDao userDao = new UserDaoImpl();
    private final ApplicationDao applicationDao = new ApplicationDaoImpl();

    public ServerResponse addApplication(String token, String requestJsonString) {
        try {
            Participant participant = getParticipant(token);
            AddApplicationDtoRequest request = fromJson(requestJsonString, AddApplicationDtoRequest.class);
            validate(request);
            Application application = new Application(request.getTitle(), request.getDescription(),
                    new HashSet<>(request.getDirections()), request.getAmount(), participant);
            int id = applicationDao.insert(application);
            return success(new AddApplicationDtoResponse(id));
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        try {
            Participant participant = getParticipant(token);
            ApplicationIdDtoRequest request = fromJson(requestJsonString, ApplicationIdDtoRequest.class);
            Application application = applicationDao.getById(request.getApplicationId());
            if (application.getParticipant() != participant) {
                throw new ServerException(ServerErrorCode.NOT_OWN_APPLICATION);
            }
            applicationDao.delete(application);
            return success();
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse rateApplication(String token, String requestJsonString) {
        try {
            Expert expert = getExpert(token);
            RateApplicationDtoRequest request = fromJson(requestJsonString, RateApplicationDtoRequest.class);
            if (request.getRating() < MIN_RATING || request.getRating() > MAX_RATING) {
                throw new ServerException(ServerErrorCode.WRONG_RATING);
            }
            Application application = applicationDao.getById(request.getApplicationId());
            if (Collections.disjoint(application.getDirections(), expert.getDirections())) {
                throw new ServerException(ServerErrorCode.NOT_EXPERT_DIRECTION);
            }
            applicationDao.setRating(application, expert, request.getRating());
            return success();
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse deleteRating(String token, String requestJsonString) {
        try {
            Expert expert = getExpert(token);
            ApplicationIdDtoRequest request = fromJson(requestJsonString, ApplicationIdDtoRequest.class);
            Application application = applicationDao.getById(request.getApplicationId());
            applicationDao.deleteRating(application, expert);
            return success();
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse getApplicationsByDirections(String token, String requestJsonString) {
        try {
            Expert expert = getExpert(token);
            Set<String> directions = expert.getDirections();
            if (!isBlank(requestJsonString)) {
                GetApplicationsDtoRequest request = fromJson(requestJsonString, GetApplicationsDtoRequest.class);
                if (request.getDirections() != null && !request.getDirections().isEmpty()) {
                    directions = new HashSet<>(request.getDirections());
                    if (!expert.getDirections().containsAll(directions)) {
                        throw new ServerException(ServerErrorCode.NOT_EXPERT_DIRECTION);
                    }
                }
            }
            return success(toListResponse(applicationDao.getByDirections(directions), expert));
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse getRatedApplications(String token) {
        try {
            Expert expert = getExpert(token);
            return success(toListResponse(applicationDao.getRatedBy(expert), expert));
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse summarize(String token, String requestJsonString) {
        try {
            userDao.getByToken(token);
            SummarizeDtoRequest request = fromJson(requestJsonString, SummarizeDtoRequest.class);
            validate(request);
            applicationDao.finishCompetition();

            List<Application> ranking = applicationDao.getAll();
            // shuffle first: the sort is stable, so applications with equal rating and amount stay in random order
            Collections.shuffle(ranking);
            ranking.sort(Comparator.comparingDouble(Application::getAverageRating).reversed()
                    .thenComparingLong(Application::getAmount));

            long fund = request.getFund();
            List<ApplicationDtoResponse> granted = new ArrayList<>();
            for (Application application : ranking) {
                if (application.getAverageRating() <= request.getThreshold()) {
                    break;
                }
                if (application.getAmount() <= fund) {
                    fund -= application.getAmount();
                    granted.add(toResponse(application, null));
                }
            }
            return success(new SummarizeDtoResponse(granted, fund));
        } catch (ServerException e) {
            return failure(e);
        }
    }

    private Participant getParticipant(String token) throws ServerException {
        User user = userDao.getByToken(token);
        if (!(user instanceof Participant)) {
            throw new ServerException(ServerErrorCode.NOT_PARTICIPANT);
        }
        return (Participant) user;
    }

    private Expert getExpert(String token) throws ServerException {
        User user = userDao.getByToken(token);
        if (!(user instanceof Expert)) {
            throw new ServerException(ServerErrorCode.NOT_EXPERT);
        }
        return (Expert) user;
    }

    private static ApplicationListDtoResponse toListResponse(List<Application> applications, Expert expert) {
        List<ApplicationDtoResponse> result = new ArrayList<>();
        for (Application application : applications) {
            result.add(toResponse(application, application.getRatings().get(expert.getLogin())));
        }
        return new ApplicationListDtoResponse(result);
    }

    private static ApplicationDtoResponse toResponse(Application application, Integer rating) {
        return new ApplicationDtoResponse(application.getId(), application.getTitle(),
                application.getDescription(), new ArrayList<>(application.getDirections()),
                application.getAmount(), application.getParticipant().getCompanyName(), rating);
    }

    private static void validate(AddApplicationDtoRequest request) throws ServerException {
        validateNotBlank(request.getTitle(), ServerErrorCode.WRONG_TITLE);
        validateNotBlank(request.getDescription(), ServerErrorCode.WRONG_DESCRIPTION);
        validateDirections(request.getDirections());
        if (request.getAmount() <= 0) {
            throw new ServerException(ServerErrorCode.WRONG_AMOUNT);
        }
    }

    private static void validate(SummarizeDtoRequest request) throws ServerException {
        if (request.getFund() <= 0) {
            throw new ServerException(ServerErrorCode.WRONG_FUND);
        }
        if (request.getThreshold() < 0 || request.getThreshold() > MAX_RATING) {
            throw new ServerException(ServerErrorCode.WRONG_THRESHOLD);
        }
    }
}
