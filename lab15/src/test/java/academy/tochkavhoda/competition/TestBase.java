package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RateApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.AddApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import academy.tochkavhoda.competition.server.Server;
import academy.tochkavhoda.competition.server.ServerResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TestBase {
    protected final Gson gson = new Gson();
    protected final Server server = new Server();

    @BeforeEach
    public void clearDatabase() {
        server.clearDatabase();
    }

    protected String registerParticipant(String login) {
        RegisterParticipantDtoRequest request =
                new RegisterParticipantDtoRequest("Иван", "Петров", "ООО " + login, login, "pass123");
        assertEquals(200, server.registerParticipant(gson.toJson(request)).getResponseCode());
        return login(login, "pass123");
    }

    protected String registerExpert(String login, List<String> directions) {
        RegisterExpertDtoRequest request =
                new RegisterExpertDtoRequest("Анна", "Смирнова", directions, login, "pass123");
        assertEquals(200, server.registerExpert(gson.toJson(request)).getResponseCode());
        return login(login, "pass123");
    }

    protected String login(String login, String password) {
        ServerResponse response = server.login(gson.toJson(new LoginDtoRequest(login, password)));
        assertEquals(200, response.getResponseCode());
        return gson.fromJson(response.getResponseData(), LoginDtoResponse.class).getToken();
    }

    protected int addApplication(String token, String title, List<String> directions, long amount) {
        AddApplicationDtoRequest request = new AddApplicationDtoRequest(title, "Описание", directions, amount);
        ServerResponse response = server.addApplication(token, gson.toJson(request));
        assertEquals(200, response.getResponseCode());
        return gson.fromJson(response.getResponseData(), AddApplicationDtoResponse.class).getId();
    }

    protected ServerResponse rate(String expertToken, int applicationId, int rating) {
        return server.rateApplication(expertToken, gson.toJson(new RateApplicationDtoRequest(applicationId, rating)));
    }

    protected void assertError(ServerResponse response, String expectedError) {
        assertEquals(400, response.getResponseCode());
        assertEquals(expectedError, gson.fromJson(response.getResponseData(), ErrorDtoResponse.class).getError());
    }
}
