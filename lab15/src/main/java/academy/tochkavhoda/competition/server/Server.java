package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.service.ApplicationService;
import academy.tochkavhoda.competition.service.DebugService;
import academy.tochkavhoda.competition.service.UserService;

public class Server {
    private final UserService userService = new UserService();
    private final ApplicationService applicationService = new ApplicationService();
    private final DebugService debugService = new DebugService();

    public ServerResponse registerParticipant(String requestJsonString) {
        return userService.registerParticipant(requestJsonString);
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return userService.registerExpert(requestJsonString);
    }

    public ServerResponse login(String requestJsonString) {
        return userService.login(requestJsonString);
    }

    public ServerResponse logout(String token) {
        return userService.logout(token);
    }

    public ServerResponse leave(String token) {
        return userService.leave(token);
    }

    public ServerResponse addApplication(String token, String requestJsonString) {
        return applicationService.addApplication(token, requestJsonString);
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        return applicationService.cancelApplication(token, requestJsonString);
    }

    public ServerResponse rateApplication(String token, String requestJsonString) {
        return applicationService.rateApplication(token, requestJsonString);
    }

    public ServerResponse deleteRating(String token, String requestJsonString) {
        return applicationService.deleteRating(token, requestJsonString);
    }

    public ServerResponse getApplicationsByDirections(String token, String requestJsonString) {
        return applicationService.getApplicationsByDirections(token, requestJsonString);
    }

    public ServerResponse getRatedApplications(String token) {
        return applicationService.getRatedApplications(token);
    }

    public ServerResponse summarize(String token, String requestJsonString) {
        return applicationService.summarize(token, requestJsonString);
    }

    public ServerResponse clearDatabase() {
        return debugService.clear();
    }
}
