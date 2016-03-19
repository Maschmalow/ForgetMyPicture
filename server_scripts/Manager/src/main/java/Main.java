import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import database.Request;
import database.Result;
import database.User;


public class Main {
    private static final String TAG = Main.class.getName();
    private static final String DB_PATH = "sqlite:/var/databases/forgetmypicture.db";

    private static Main instance = null;

    private Dao<User, String> userDao;
    private Dao<Request, Integer> requestDao;
    private Dao<Result, String> resultDao;

    public static void main(String[] args) throws Exception {
        JdbcConnectionSource source = new JdbcPooledConnectionSource(DB_PATH);

        new Main(source).run();

        source.close();
    }


    private Main(ConnectionSource source) throws Exception {
        userDao = DaoManager.createDao(source, User.class);
        requestDao = DaoManager.createDao(source, Request.class);
        resultDao = DaoManager.createDao(source, Result.class);

        instance = this;
    }

    private void run() { //checks for new jobs and run them
        while( true ) {
            for(Result result : resultDao) {
                if(result.getMatch() == -1)
            }
        }
    }

    public static Dao<User, String> getUserDao() {
        return instance.userDao;
    }

    public static Dao<Request, Integer> getRequestDao() {
        return instance.requestDao;
    }

    public static Dao<Result, String> getResultDao() {
        return instance.resultDao;
    }

}
