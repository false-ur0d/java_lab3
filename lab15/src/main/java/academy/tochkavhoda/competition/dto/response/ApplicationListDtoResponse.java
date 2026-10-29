package academy.tochkavhoda.competition.dto.response;

import java.util.List;

public class ApplicationListDtoResponse {
    private List<ApplicationDtoResponse> applications;

    public ApplicationListDtoResponse() {
    }

    public ApplicationListDtoResponse(List<ApplicationDtoResponse> applications) {
        this.applications = applications;
    }

    public List<ApplicationDtoResponse> getApplications() {
        return applications;
    }
}
