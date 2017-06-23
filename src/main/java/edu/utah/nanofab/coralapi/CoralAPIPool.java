package edu.utah.nanofab.coralapi;

import static edu.utah.nanofab.coralapi.helper.TimestampConverter.dateToAdapterString;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoralAPIPool {
 
        private HashMap<String, CoralAPISynchronized> pool;
        private HashMap<String, Date> accessTimes;   //when a connection was last accessed
        private HashMap<String, Date> creationTimes; //when a connection was created
        private String configUrl;
	private static CoralAPIPool singletonInstance;
        private int maxAgeInSeconds = -1;
        public static Logger logger;
 
	public CoralAPIPool(String coralConfigUrl) {
            pool = new HashMap<String, CoralAPISynchronized>();
            accessTimes = new HashMap<String, Date>();
            creationTimes = new HashMap<String, Date>();
            configUrl = coralConfigUrl;
            logger = LoggerFactory.getLogger(CoralAPIPool.class);
	}
        
        public CoralAPISynchronized getConnection(String user) {
            this.setTimestamp(user, this.accessTimes);
            this.expireConnectionIfOverMax(user, this.maxAgeInSeconds, this.creationTimes);
            if (pool.containsKey(user)) {
                return pool.get(user);
            } else {
                logger.debug("Creating new connection for " + user);
                CoralAPISynchronized newConnection = new CoralAPISynchronized(user, configUrl);
                pool.put(user, newConnection);
                this.setTimestamp(user, this.creationTimes);
                return newConnection;
            }
        }
        
        public void closeConnection(String user) {
            logger.debug("Closing connection for " + user);
            if (pool.containsKey(user)) {
                pool.get(user).close();
                pool.remove(user);
                accessTimes.remove(user);
                creationTimes.remove(user);
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
            Set<String> users = pool.keySet();
            for(String user : users) {
                this.expireConnectionIfOverMax(user, seconds, this.accessTimes);
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

    private void setTimestamp(String user, HashMap<String, Date> userTimeMap) {
        Date now = new Date();
        logger.debug("access to api pool for " + user + " at " +
            dateToAdapterString(now));
        if (userTimeMap.containsKey(user)) {
            logger.debug("Last access to api pool for " + user + " was " +
                    dateToAdapterString(userTimeMap.get(user))); 
        } else {
            logger.debug("First access to api pool for " + user );             
        }
        userTimeMap.put(user, now);
    }

    public int getMaxAgeInSeconds() {
        return maxAgeInSeconds;
    }

    /**
     * 
     * @param maxAgeInSeconds how long should a connection be cached?
     *                        -1 means no limit
     */
    public void setMaxAgeInSeconds(int maxAgeInSeconds) {
        this.maxAgeInSeconds = maxAgeInSeconds;
    }

    private void expireConnectionIfOverMax(String user, int seconds, HashMap<String, Date> userAccessTimeMap) {
        logger.debug("checking if expiration for " + user + " greater than " + seconds);
        if (seconds == -1) {
            return;
        }
        if (pool.containsKey(user)) {
            Date now = new Date();
            try {
                if ( userAccessTimeMap.containsKey(user) &&
                        ((now.getTime() - userAccessTimeMap.get(user).getTime()) > 
                        (seconds * 1000)) ) {
                        logger.debug("expired connection for api pool for " + user + " was " +
                                dateToAdapterString(userAccessTimeMap.get(user)));                     
                        closeConnection(user);
                        userAccessTimeMap.remove(user);
                } else {
                        logger.debug("NOT expired connection for api pool for " + user + " was " +
                                dateToAdapterString(userAccessTimeMap.get(user)));                     
                }
            } catch (Exception e) {
                logger.debug("exception caught!");
                e.printStackTrace();
            }
        } else {
            logger.debug("no connection for " + user);
        }
    }
    
}