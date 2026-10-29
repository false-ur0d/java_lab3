package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.daoimpl.UserDaoImpl;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.HashSet;
import java.util.UUID;

import static academy.tochkavhoda.competition.service.ServiceUtils.*;

public class UserService {
    private final UserDao userDao = new UserDaoImpl();

    public ServerResponse registerParticipant(String requestJsonString) {
        try {
            RegisterParticipantDtoRequest request = fromJson(requestJsonString, RegisterParticipantDtoRequest.class);
            validate(request);
            Participant participant = new Participant(request.getFirstName(), request.getLastName(),
                    request.getCompanyName(), request.getLogin(), request.getPassword());
            userDao.insert(participant);
            return success();
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse registerExpert(String requestJsonString) {
        try {
            RegisterExpertDtoRequest request = fromJson(requestJsonString, RegisterExpertDtoRequest.class);
            validate(request);
            Expert expert = new Expert(request.getFirstName(), request.getLastName(),
                    new HashSet<>(request.getDirections()), request.getLogin(), request.getPassword());
            userDao.insert(expert);
            return success();
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse login(String requestJsonString) {
        try {
            LoginDtoRequest request = fromJson(requestJsonString, LoginDtoRequest.class);
            User user = userDao.getByLogin(request.getLogin());
            if (user == null || !user.getPassword().equals(request.getPassword())) {
                throw new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
            }
            String token = userDao.login(user, UUID.randomUUID().toString());
            return success(new LoginDtoResponse(token));
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse logout(String token) {
        try {
            userDao.getByToken(token);
            userDao.logout(token);
            return success();
        } catch (ServerException e) {
            return failure(e);
        }
    }

    public ServerResponse leave(String token) {
        try {
            User user = userDao.getByToken(token);
            userDao.delete(user);
            return success();
        } catch (ServerException e) {
            return failure(e);
        }
    }

    private static void validate(RegisterParticipantDtoRequest request) throws ServerException {
        validateNotBlank(request.getFirstName(), ServerErrorCode.WRONG_FIRST_NAME);
        validateNotBlank(request.getLastName(), ServerErrorCode.WRONG_LAST_NAME);
        validateNotBlank(request.getCompanyName(), ServerErrorCode.WRONG_COMPANY_NAME);
        validateLogin(request.getLogin());
        validatePassword(request.getPassword());
    }

    private static void validate(RegisterExpertDtoRequest request) throws ServerException {
        validateNotBlank(request.getFirstName(), ServerErrorCode.WRONG_FIRST_NAME);
        validateNotBlank(request.getLastName(), ServerErrorCode.WRONG_LAST_NAME);
        validateDirections(request.getDirections());
        validateLogin(request.getLogin());
        validatePassword(request.getPassword());
    }
}
