import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.Request;
import database.Result;
import database.User;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String DB_PATH = "sqlite:/var/databases/forgetmypicture.db";
    private static final int NB_WORKERS = 16;
    private static final int REFRESH_RATE = 100; //ms

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

    private void run() { //checks for new jobs and runs them
        logger.log(Level.INFO, "Server started.");
        ExecutorService pool = Executors.newFixedThreadPool(NB_WORKERS);
        Set<Result> processingResults = new HashSet<>();

        while( true ) {
            for(Result r : processingResults) {
                try {
                    resultDao.refresh(r);
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "SQL error while refreshing", e);
                    continue;
                }
                if( r.isProcessed() )
                    processingResults.remove(r);
            }

            for(Result result : resultDao) {
                if(!result.isProcessed() && !processingResults.contains(result)) {
                    logger.log(Level.INFO, "New result is being processed.");
                    pool.execute(new ProcessingUnit(result));
                    processingResults.add(result);
                }
            }

            try {
                Thread.sleep(REFRESH_RATE);
            } catch (InterruptedException e) {
                pool.shutdown();
                return;
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
