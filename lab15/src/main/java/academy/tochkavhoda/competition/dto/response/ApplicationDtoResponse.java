package academy.tochkavhoda.competition.dto.response;

import java.util.List;

public class ApplicationDtoResponse {
    private int id;
    private String title;
    private String description;
    private List<String> directions;
    private long amount;
    private String companyName;
    // rating of the current expert, null if not rated
    private Integer rating;

    public ApplicationDtoResponse() {
    }

    public ApplicationDtoResponse(int id, String title, String description, List<String> directions,
                                  long amount, String companyName, Integer rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
        this.companyName = companyName;
        this.rating = rating;
    }

    public int getId() {
        return id;
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

    public String getCompanyName() {
        return companyName;
    }

    public Integer getRating() {
        return rating;
    }
}
