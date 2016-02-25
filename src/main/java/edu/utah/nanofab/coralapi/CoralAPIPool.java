package edu.utah.nanofab.coralapi;

import static edu.utah.nanofab.coralapi.helper.TimestampConverter.dateToAdapterString;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoralAPIPool {
 
        private HashMap<String, CoralAPISynchronized> pool;
        private HashMap<String, Date> accessTimes;
        private String configUrl;
	private static CoralAPIPool singletonInstance;
        public static Logger logger;
 
	public CoralAPIPool(String coralConfigUrl) {
            pool = new HashMap<String, CoralAPISynchronized>();
            accessTimes = new HashMap<String, Date>();
            configUrl = coralConfigUrl;
            logger = LoggerFactory.getLogger(CoralAPIPool.class);
	}
        
        public CoralAPISynchronized getConnection(String user) {
            this.setTimestamp(user);
            if (pool.containsKey(user)) {
                return pool.get(user);
            } else {
                CoralAPISynchronized newConnection = new CoralAPISynchronized(user, configUrl);
                pool.put(user, newConnection);
                return newConnection;
            }
        }
        
        public void closeConnection(String user) {
            logger.debug("Closing connection for " + user);
            if (pool.containsKey(user)) {
                pool.get(user).close();
                pool.remove(user);
                accessTimes.remove(user);
            }
        }

        public void closeAllConnections() {
            Set<String> keys = pool.keySet();
            for(String k : keys) {
                try {
                    closeConnection(k);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        public void closeConnectionsOlderThan(int seconds) {
            Set<String> keys = pool.keySet();
            Date now = new Date();
            for(String k : keys) {
                try {
                    if ( (now.getTime() - accessTimes.get(k).getTime()) > 
                            (seconds * 1000) ) {
                            closeConnection(k);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

	public static CoralAPIPool getInstance(String configUrl) {
		if (singletonInstance == null) {
			// Thread Safe. Might be costly operation in some case
			synchronized (CoralAPIPool.class) {
				if (singletonInstance == null) {
					singletonInstance = new CoralAPIPool(configUrl);
				}
			}
		}
		return singletonInstance;
	}

    private void setTimestamp(String user) {
        Date now = new Date();
        logger.debug("access to api pool for " + user + " at " +
            dateToAdapterString(now));
        if (accessTimes.containsKey(user)) {
            logger.debug("Last access to api pool for " + user + " was " +
                    dateToAdapterString(accessTimes.get(user))); 
        } else {
            logger.debug("First access to api pool for " + user );             
        }
        this.accessTimes.put(user, now);
    }
}