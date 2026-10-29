package academy.tochkavhoda.competition.exception;

public class ServerException extends Exception {
    private final ServerErrorCode errorCode;

    public ServerException(ServerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServerErrorCode getErrorCode() {
        return errorCode;
    }
}
