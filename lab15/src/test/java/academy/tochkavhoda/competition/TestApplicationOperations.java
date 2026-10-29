package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.ApplicationIdDtoRequest;
import academy.tochkavhoda.competition.dto.request.GetApplicationsDtoRequest;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.ApplicationListDtoResponse;
import academy.tochkavhoda.competition.server.ServerResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestApplicationOperations extends TestBase {

    private List<ApplicationDtoResponse> getApplications(ServerResponse response) {
        assertEquals(200, response.getResponseCode());
        return gson.fromJson(response.getResponseData(), ApplicationListDtoResponse.class).getApplications();
    }

    @Test
    public void testGetApplicationsByDirections() {
        String participant = registerParticipant("romashka");
        String expert = registerExpert("expert", List.of("математика", "информатика"));
        int mathId = addApplication(participant, "Матан", List.of("математика"), 1000);
        int itId = addApplication(participant, "Сайт", List.of("информатика"), 2000);
        addApplication(participant, "Химия", List.of("химия"), 3000);

        List<ApplicationDtoResponse> all = getApplications(server.getApplicationsByDirections(expert, null));
        assertEquals(2, all.size());
        assertEquals(mathId, all.get(0).getId());
        assertEquals(itId, all.get(1).getId());
        assertEquals("ООО romashka", all.get(0).getCompanyName());

        String onlyIt = gson.toJson(new GetApplicationsDtoRequest(List.of("информатика")));
        List<ApplicationDtoResponse> it = getApplications(server.getApplicationsByDirections(expert, onlyIt));
        assertEquals(1, it.size());
        assertEquals("Сайт", it.get(0).getTitle());

        String foreign = gson.toJson(new GetApplicationsDtoRequest(List.of("химия")));
        assertError(server.getApplicationsByDirections(expert, foreign),
                "Эксперт не является специалистом по направлениям этой заявки");
    }

    @Test
    public void testAddApplicationWithWrongData() {
        String participant = registerParticipant("romashka");
        String expert = registerExpert("expert", List.of("математика"));
        AddApplicationDtoRequest negativeAmount =
                new AddApplicationDtoRequest("Проект", "Описание", List.of("математика"), -5);
        assertError(server.addApplication(participant, gson.toJson(negativeAmount)),
                "Запрашиваемая сумма должна быть больше нуля");

        AddApplicationDtoRequest correct = new AddApplicationDtoRequest("Проект", "Описание", List.of("математика"), 5);
        assertError(server.addApplication(expert, gson.toJson(correct)), "Операция доступна только участникам конкурса");
        assertError(server.addApplication("bad-token", gson.toJson(correct)), "Пользователь не авторизован");
    }

    @Test
    public void testCancelApplication() {
        String owner = registerParticipant("romashka");
        String stranger = registerParticipant("vasilek");
        String expert = registerExpert("expert", List.of("математика"));
        int id = addApplication(owner, "Проект", List.of("математика"), 1000);
        String request = gson.toJson(new ApplicationIdDtoRequest(id));

        assertError(server.cancelApplication(stranger, request), "Можно отменить только свою заявку");
        assertEquals(200, server.cancelApplication(owner, request).getResponseCode());
        assertTrue(getApplications(server.getApplicationsByDirections(expert, null)).isEmpty());
        assertError(server.cancelApplication(owner, request), "Заявка не найдена");
    }

    @Test
    public void testRateChangeAndDeleteRating() {
        String participant = registerParticipant("romashka");
        String expert = registerExpert("expert", List.of("математика"));
        int id = addApplication(participant, "Проект", List.of("математика"), 1000);

        assertEquals(200, rate(expert, id, 3).getResponseCode());
        assertEquals(200, rate(expert, id, 5).getResponseCode());
        List<ApplicationDtoResponse> rated = getApplications(server.getRatedApplications(expert));
        assertEquals(1, rated.size());
        assertEquals(5, rated.get(0).getRating());

        String request = gson.toJson(new ApplicationIdDtoRequest(id));
        assertEquals(200, server.deleteRating(expert, request).getResponseCode());
        assertTrue(getApplications(server.getRatedApplications(expert)).isEmpty());
        assertError(server.deleteRating(expert, request), "Оценка для этой заявки не найдена");
    }

    @Test
    public void testRateWithWrongData() {
        String participant = registerParticipant("romashka");
        String mathExpert = registerExpert("expert", List.of("математика"));
        String chemExpert = registerExpert("chemist", List.of("химия"));
        int id = addApplication(participant, "Проект", List.of("математика"), 1000);

        assertError(rate(mathExpert, id, 6), "Оценка должна быть в диапазоне от 1 до 5");
        assertError(rate(mathExpert, 100, 4), "Заявка не найдена");
        assertError(rate(chemExpert, id, 4), "Эксперт не является специалистом по направлениям этой заявки");
        assertError(rate(participant, id, 4), "Операция доступна только экспертам");
    }
}
