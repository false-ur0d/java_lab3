package academy.tochkavhoda.competition.exception;

public enum ServerErrorCode {
    WRONG_JSON("Некорректный формат запроса"),
    WRONG_FIRST_NAME("Имя не может быть пустым"),
    WRONG_LAST_NAME("Фамилия не может быть пустой"),
    WRONG_COMPANY_NAME("Название фирмы не может быть пустым"),
    WRONG_LOGIN("Логин должен содержать не менее 4 символов без пробелов"),
    WRONG_PASSWORD("Пароль должен содержать не менее 6 символов, включая хотя бы одну цифру"),
    WRONG_DIRECTIONS("Список направлений не может быть пустым"),
    WRONG_TITLE("Название заявки не может быть пустым"),
    WRONG_DESCRIPTION("Описание заявки не может быть пустым"),
    WRONG_AMOUNT("Запрашиваемая сумма должна быть больше нуля"),
    WRONG_RATING("Оценка должна быть в диапазоне от 1 до 5"),
    WRONG_FUND("Размер фонда должен быть больше нуля"),
    WRONG_THRESHOLD("Пороговое значение должно быть в диапазоне от 0 до 5"),
    LOGIN_ALREADY_EXISTS("Этот логин уже используется"),
    WRONG_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    WRONG_TOKEN("Пользователь не авторизован"),
    NOT_PARTICIPANT("Операция доступна только участникам конкурса"),
    NOT_EXPERT("Операция доступна только экспертам"),
    APPLICATION_NOT_FOUND("Заявка не найдена"),
    NOT_OWN_APPLICATION("Можно отменить только свою заявку"),
    NOT_EXPERT_DIRECTION("Эксперт не является специалистом по направлениям этой заявки"),
    RATING_NOT_FOUND("Оценка для этой заявки не найдена"),
    COMPETITION_FINISHED("Прием заявок и оценок завершен");

    private final String message;

    ServerErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
