package academy.tochkavhoda.competition.dto.request;

import java.util.List;

public class GetApplicationsDtoRequest {
    // if empty, all expert's directions are used
    private List<String> directions;

    public GetApplicationsDtoRequest() {
    }

    public GetApplicationsDtoRequest(List<String> directions) {
        this.directions = directions;
    }

    public List<String> getDirections() {
        return directions;
    }
}
