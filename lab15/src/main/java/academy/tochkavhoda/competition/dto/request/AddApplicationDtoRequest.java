package academy.tochkavhoda.competition.dto.request;

import java.util.List;

public class AddApplicationDtoRequest {
    private String title;
    private String description;
    private List<String> directions;
    private long amount;

    public AddApplicationDtoRequest() {
    }

    public AddApplicationDtoRequest(String title, String description, List<String> directions, long amount) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDirections() {
        return directions;
    }

    public long getAmount() {
        return amount;
    }
}
