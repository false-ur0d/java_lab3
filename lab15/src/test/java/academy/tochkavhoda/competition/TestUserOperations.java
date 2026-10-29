package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.LoginDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterExpertDtoRequest;
import academy.tochkavhoda.competition.dto.request.RegisterParticipantDtoRequest;
import academy.tochkavhoda.competition.dto.response.ApplicationListDtoResponse;
import academy.tochkavhoda.competition.server.ServerResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserOperations extends TestBase {

    @Test
    public void testRegisterAndLogin() {
        String token = registerParticipant("romashka");
        assertNotNull(token);
    }

    @Test
    public void testRegisterDuplicateLogin() {
        registerParticipant("romashka");
        RegisterExpertDtoRequest request =
                new RegisterExpertDtoRequest("Анна", "Смирнова", List.of("математика"), "romashka", "pass123");
        assertError(server.registerExpert(gson.toJson(request)), "Этот логин уже используется");
    }

    @Test
    public void testRegisterWithWrongData() {
        RegisterParticipantDtoRequest weakPassword =
                new RegisterParticipantDtoRequest("Иван", "Петров", "ООО Ромашка", "romashka", "qwerty");
        assertError(server.registerParticipant(gson.toJson(weakPassword)),
                "Пароль должен содержать не менее 6 символов, включая хотя бы одну цифру");

        RegisterParticipantDtoRequest noCompany =
                new RegisterParticipantDtoRequest("Иван", "Петров", " ", "romashka", "pass123");
        assertError(server.registerParticipant(gson.toJson(noCompany)), "Название фирмы не может быть пустым");

        RegisterExpertDtoRequest noDirections =
                new RegisterExpertDtoRequest("Анна", "Смирнова", List.of(), "expert", "pass123");
        assertError(server.registerExpert(gson.toJson(noDirections)), "Список направлений не может быть пустым");

        assertError(server.registerParticipant("{not a json"), "Некорректный формат запроса");
        assertError(server.registerParticipant(null), "Некорректный формат запроса");
    }

    @Test
    public void testLoginWithWrongPassword() {
        registerParticipant("romashka");
        ServerResponse response = server.login(gson.toJson(new LoginDtoRequest("romashka", "wrong123")));
        assertError(response, "Неверный логин или пароль");
    }

    @Test
    public void testLogoutAndLoginAgain() {
        String oldToken = registerExpert("expert", List.of("математика"));
        assertEquals(200, server.logout(oldToken).getResponseCode());

        assertError(server.getRatedApplications(oldToken), "Пользователь не авторизован");

        String newToken = login("expert", "pass123");
        assertNotEquals(oldToken, newToken);
        assertEquals(200, server.getRatedApplications(newToken).getResponseCode());
    }

    @Test
    public void testParticipantLeaveRemovesApplications() {
        String participant = registerParticipant("romashka");
        String expert = registerExpert("expert", List.of("математика"));
        addApplication(participant, "Проект", List.of("математика"), 1000);

        assertEquals(200, server.leave(participant).getResponseCode());

        ServerResponse loginResponse = server.login(gson.toJson(new LoginDtoRequest("romashka", "pass123")));
        assertError(loginResponse, "Неверный логин или пароль");

        ServerResponse response = server.getApplicationsByDirections(expert, null);
        assertEquals(200, response.getResponseCode());
        ApplicationListDtoResponse list = gson.fromJson(response.getResponseData(), ApplicationListDtoResponse.class);
        assertTrue(list.getApplications().isEmpty());
    }
}
