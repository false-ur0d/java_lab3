package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public final class ServiceUtils {
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_FAILURE = 400;
    public static final String EMPTY_JSON = "{}";

    private static final int MIN_LOGIN_LENGTH = 4;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final Gson GSON = new Gson();

    private ServiceUtils() {
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws ServerException {
        if (isBlank(json)) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        try {
            T result = GSON.fromJson(json, clazz);
            if (result == null) {
                throw new ServerException(ServerErrorCode.WRONG_JSON);
            }
            return result;
        } catch (JsonSyntaxException e) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
    }

    public static ServerResponse success(Object dtoResponse) {
        return new ServerResponse(CODE_SUCCESS, GSON.toJson(dtoResponse));
    }

    public static ServerResponse success() {
        return new ServerResponse(CODE_SUCCESS, EMPTY_JSON);
    }

    public static ServerResponse failure(ServerException e) {
        return new ServerResponse(CODE_FAILURE, GSON.toJson(new ErrorDtoResponse(e.getMessage())));
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void validateNotBlank(String value, ServerErrorCode errorCode) throws ServerException {
        if (isBlank(value)) {
            throw new ServerException(errorCode);
        }
    }

    public static void validateLogin(String login) throws ServerException {
        if (login == null || login.length() < MIN_LOGIN_LENGTH || login.contains(" ")) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN);
        }
    }

    public static void validatePassword(String password) throws ServerException {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH
                || password.chars().noneMatch(Character::isDigit)) {
            throw new ServerException(ServerErrorCode.WRONG_PASSWORD);
        }
    }

    public static void validateDirections(List<String> directions) throws ServerException {
        if (directions == null || directions.isEmpty()) {
            throw new ServerException(ServerErrorCode.WRONG_DIRECTIONS);
        }
        for (String direction : directions) {
            validateNotBlank(direction, ServerErrorCode.WRONG_DIRECTIONS);
        }
    }
}
