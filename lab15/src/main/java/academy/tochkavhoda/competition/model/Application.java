package academy.tochkavhoda.competition.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Application {
    private int id;
    private String title;
    private String description;
    private Set<String> directions;
    private long amount;
    private Participant participant;
    // expert login -> rating (1..5)
    private Map<String, Integer> ratings = new HashMap<>();

    public Application(String title, String description, Set<String> directions, long amount, Participant participant) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.amount = amount;
        this.participant = participant;
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (int rating : ratings.values()) {
            sum += rating;
        }
        return sum / ratings.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getDirections() {
        return directions;
    }

    public void setDirections(Set<String> directions) {
        this.directions = directions;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Map<String, Integer> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Integer> ratings) {
        this.ratings = ratings;
    }
}
