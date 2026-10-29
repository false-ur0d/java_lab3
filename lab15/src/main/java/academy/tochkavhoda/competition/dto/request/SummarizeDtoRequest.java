package academy.tochkavhoda.competition.dto.request;

public class SummarizeDtoRequest {
    private long fund;
    private double threshold;

    public SummarizeDtoRequest() {
    }

    public SummarizeDtoRequest(long fund, double threshold) {
        this.fund = fund;
        this.threshold = threshold;
    }

    public long getFund() {
        return fund;
    }

    public double getThreshold() {
        return threshold;
    }
}
