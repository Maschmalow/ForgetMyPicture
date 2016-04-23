package net.tenwame.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import net.tenwame.manager.database.Request;
import net.tenwame.manager.database.Result;
import net.tenwame.manager.database.Selfie;
import net.tenwame.manager.database.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String DB_PATH = "jdbc:mysql:///forgetmypicture";
    private static final String USERNAME = "manager";
    private static final String PASSWORD = "AtosManager2016";
    private static final int NB_WORKERS = 16;
    private static final int REFRESH_RATE = 1000; //ms


    private static ConnectionSource source;

    private Dao<User, String> userDao;
    private Dao<Request, String> requestDao;
    private Dao<Result, String> resultDao;
    private Dao<Selfie, String> selfieDao;

    public static void main(String[] args) throws Exception {
        System.setProperty(LoggerFactory.LOG_TYPE_SYSTEM_PROPERTY, "LOCAL");
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "INFO");

        source = new JdbcPooledConnectionSource(DB_PATH, USERNAME, PASSWORD);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                source.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        new Main().run();
    }


    private Main() throws Exception {
        if(source == null)
            throw new IllegalArgumentException("Source can't be null");

        // eager init, and database scheme compatibility check
        userDao = DaoManager.createDao(source, User.class);
        requestDao = DaoManager.createDao(source, Request.class);
        resultDao = DaoManager.createDao(source, Result.class);
        selfieDao = DaoManager.createDao(source, Selfie.class);
    }

    private void run() throws SQLException { //checks for new jobs and runs them
        logger.log(Level.INFO, "Server started.");
        logger.log(Level.INFO, String.valueOf(getResultDao().queryForAll().size()));

        ExecutorService pool = Executors.newFixedThreadPool(NB_WORKERS);
        Runtime.getRuntime().addShutdownHook(new Thread(pool::shutdownNow));
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

            for(Result result : resultDao.queryForEq("pic_match", -1)) {
                if(!processingResults.contains(result)) {
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
        return DaoManager.lookupDao(source, User.class);
    }

    public static Dao<Request, String> getRequestDao() {
        return DaoManager.lookupDao(source, Request.class);
    }

    public static Dao<Result, String> getResultDao() {
        return DaoManager.lookupDao(source, Result.class);
    }

    public static Dao<Selfie, String> getSelfieDao() {
        return DaoManager.lookupDao(source, Selfie.class);
    }

}
