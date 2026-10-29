package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.DebugDao;
import academy.tochkavhoda.competition.database.Database;

public class DebugDaoImpl implements DebugDao {
    private final Database database = Database.getInstance();

    @Override
    public void clear() {
        database.clear();
    }
}
