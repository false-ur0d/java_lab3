package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.dto.request.AddApplicationDtoRequest;
import academy.tochkavhoda.competition.dto.request.SummarizeDtoRequest;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.dto.response.SummarizeDtoResponse;
import academy.tochkavhoda.competition.server.ServerResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSummarize extends TestBase {

    private SummarizeDtoResponse summarize(String token, long fund, double threshold) {
        ServerResponse response = server.summarize(token, gson.toJson(new SummarizeDtoRequest(fund, threshold)));
        assertEquals(200, response.getResponseCode());
        return gson.fromJson(response.getResponseData(), SummarizeDtoResponse.class);
    }

    private List<String> titles(SummarizeDtoResponse response) {
        return response.getGrantedApplications().stream().map(ApplicationDtoResponse::getTitle).toList();
    }

    @Test
    public void testSummarizeScenario() {
        String participant = registerParticipant("romashka");
        String expert1 = registerExpert("expert1", List.of("математика"));
        String expert2 = registerExpert("expert2", List.of("математика"));
        List<String> math = List.of("математика");

        int a = addApplication(participant, "A", math, 600);
        int b = addApplication(participant, "B", math, 500);
        int c = addApplication(participant, "C", math, 300);
        int d = addApplication(participant, "D", math, 50);
        addApplication(participant, "E", math, 10);    // no ratings at all

        rate(expert1, a, 5);
        rate(expert2, a, 5);    // average 5.0
        rate(expert1, b, 5);
        rate(expert2, b, 4);    // average 4.5, but does not fit into remaining fund
        rate(expert1, c, 4);    // average 4.0
        rate(expert1, d, 2);    // average 2.0, not above threshold

        SummarizeDtoResponse result = summarize(expert1, 1000, 2);
        assertEquals(List.of("A", "C"), titles(result));
        assertEquals(100, result.getRemainingFund());
    }

    @Test
    public void testEqualRatingCheaperFirst() {
        String participant = registerParticipant("romashka");
        String expert = registerExpert("expert", List.of("математика"));
        int expensive = addApplication(participant, "Expensive", List.of("математика"), 700);
        int cheap = addApplication(participant, "Cheap", List.of("математика"), 400);
        rate(expert, expensive, 4);
        rate(expert, cheap, 4);

        SummarizeDtoResponse result = summarize(expert, 1000, 0);
        assertEquals(List.of("Cheap"), titles(result));
        assertEquals(600, result.getRemainingFund());
    }

    @Test
    public void testNothingAllowedAfterSummarize() {
        String participant = registerParticipant("romashka");
        String expert = registerExpert("expert", List.of("математика"));
        int id = addApplication(participant, "Проект", List.of("математика"), 100);
        summarize(expert, 1000, 0);

        AddApplicationDtoRequest request = new AddApplicationDtoRequest("Поздно", "Описание", List.of("математика"), 5);
        assertError(server.addApplication(participant, gson.toJson(request)), "Прием заявок и оценок завершен");
        assertError(rate(expert, id, 5), "Прием заявок и оценок завершен");
        assertError(server.summarize(expert, gson.toJson(new SummarizeDtoRequest(1000, 0))),
                "Прием заявок и оценок завершен");
    }
}
