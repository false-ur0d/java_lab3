package academy.tochkavhoda.competition.dto.response;

import java.util.List;

public class SummarizeDtoResponse {
    private List<ApplicationDtoResponse> grantedApplications;
    private long remainingFund;

    public SummarizeDtoResponse() {
    }

    public SummarizeDtoResponse(List<ApplicationDtoResponse> grantedApplications, long remainingFund) {
        this.grantedApplications = grantedApplications;
        this.remainingFund = remainingFund;
    }

    public List<ApplicationDtoResponse> getGrantedApplications() {
        return grantedApplications;
    }

    public long getRemainingFund() {
        return remainingFund;
    }
}
