package edu.utah.nanofab.coralapi.helper;

import edu.utah.nanofab.coralapi.exceptions.CoralConnectionException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import org.omg.CORBA.ORB;
import org.opencoral.constants.Constants;
import org.opencoral.idl.Admin.AdminManager;
import org.opencoral.idl.Admin.Agent;
import org.opencoral.idl.Auth.AuthManager;
import org.opencoral.idl.Auth.AuthManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManager;
import org.opencoral.idl.Equipment.EquipmentManagerHelper;
import org.opencoral.idl.Hardware.HardwareManager;
import org.opencoral.idl.Hardware.HardwareManagerHelper;
import org.opencoral.idl.Reservation.ReservationManager;
import org.opencoral.idl.Resource.EventManager;
import org.opencoral.idl.Resource.EventManagerHelper;
import org.opencoral.idl.Resource.ResourceManager;
import org.opencoral.idl.Resource.ResourceManagerHelper;
import org.opencoral.idl.Runtime.RuntimeManager;
import org.opencoral.idl.Runtime.RuntimeManagerHelper;
import org.opencoral.idl.Service.ServiceManager;
import org.opencoral.idl.Service.ServiceManagerHelper;
import org.opencoral.util.AdminManagerConnection;
import org.opencoral.util.Tstamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoralManagers implements CoralConnectorInterface {

    private int maxConnectionAttempts = 1;
    private final Logger logger;
    private String coralUser;
    private String iorUrl;
    private ORB orb = null;
    private AdminManager admMgr;
    private ResourceManager rscMgr;
    private AuthManager athMgr;
    private EventManager evtMgr;
    private EquipmentManager eqMgr;
    private RuntimeManager runMgr;
    private ServiceManager svcMgr;
    private HardwareManager hwrMgr;
    private String ticketString;
    private long creationEpochOfTicket;
    private long TICKET_LIFE = 5 * 60 * 1000; //five minutes of ticket life
    
    public CoralManagers(String coralUser, String iorUrl) throws CoralConnectionException {
        this.logger = LoggerFactory.getLogger(CoralManagerConnector.class);
        setup(coralUser, iorUrl);
    }

    @Override
    public void setup(String coralUser, String iorUrl) throws CoralConnectionException {
        this.coralUser = coralUser;
        this.iorUrl = iorUrl;
        this.initCorba();
    }

    @Override
    public AuthManager getAuthManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EquipmentManager getEquipmentManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReservationManager getReservationManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResourceManager getResourceManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RuntimeManager getRuntimeManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void release() {

        // Unsubscribe from all events.
        System.out.println("Unsubscribing from all events.");
        System.out.println("Shutdown by order of the Coral admin manager.");
        System.out.println("Shutting down the Orb in Coral main.");
        
        orb.shutdown(true);
        orb.destroy();
        orb = null;
    }

    private void initCorba() throws CoralConnectionException {
        Properties p = new Properties();

        if (System.getProperty("noKeepAlive") == null) {
                p.put(com.sun.corba.se.impl.orbutil.ORBConstants.CONNECTION_SOCKET_TYPE_PROPERTY,
                        com.sun.corba.se.impl.orbutil.ORBConstants.SOCKET);
                p.put(com.sun.corba.se.impl.orbutil.ORBConstants.SOCKET_FACTORY_CLASS_PROPERTY,
                        org.opencoral.corba.SocketFactory.class.getName());
                System.out.println("Set SocketFactory to the one that uses keep alives");
        } else {
                System.out.println("Set SocketFactory to not use keepalive");
        }

        System.out.println("this.ORBIPAddress is not set");
        this.orb = ORB.init((String[]) null, p);

        String ior = null;
        org.omg.CORBA.Object obj = null;
        boolean isConnected = false;

        AdminManagerConnection admConn = new AdminManagerConnection(orb);
        this.admMgr = admConn.connect(this.iorUrl);

        if (this.admMgr != null)
                isConnected = true;

        int attempts = 0;

        while (isConnected == false && attempts < maxConnectionAttempts) {

                displayError("Unable to get AdminManager IOR.\n" + "Trying again in 3 seconds....");
                try {
                        Thread.sleep(3000);
                        this.admMgr = admConn.connect(this.iorUrl);
                        attempts++;
                        if (this.admMgr != null)
                                isConnected = true;

                } catch (Exception e) {
                        System.err.println(e);
                        attempts++;
                }

        }

        if (!isConnected) {
                displayError("Unable to get AdminManager IOR.\n" + "Please contact lab staff.");
                throw new CoralConnectionException("Unable to get AdminManager IOR.");
        }

        isConnected = false;
        attempts = 0;

        while (isConnected == false && attempts < maxConnectionAttempts) {

                try {
                        ior = this.admMgr.getServerIOR(Constants.RSCMGR_NAME);
                        obj = this.orb.string_to_object(ior);
                        this.rscMgr = ResourceManagerHelper.narrow(obj);
                        attempts++;
                        if (this.rscMgr != null)
                                isConnected = true;
                } catch (Exception e) {
                        System.err.println(e);
                        displayError("Admin Manager is not responding.\n" + "Trying again in 3 seconds....");
                        attempts++;
                        try {
                                Thread.sleep(3000);
                        } catch (Exception e2) {
                                System.err.println("Sleep interrupted.");
                        }
                }
        }

        if (!isConnected) {
                displayError("Unable to get ResourceManager IOR.\n" + "Please contact lab staff.");
                throw new CoralConnectionException("Unable to get ResourceManager IOR.");
        }

        System.out.println("Resource Manager contacted.");

        try {
                ior = this.admMgr.getServerIOR(Constants.ATHMGR_NAME);
                obj = this.orb.string_to_object(ior);
                this.athMgr = AuthManagerHelper.narrow(obj);
        } catch (Exception e) {
                System.err.println(e);
                displayError("Authorization Manager is not responding.");
                throw new CoralConnectionException("Authorization Manager is not responding.");
        }

        System.out.println("Authorization Manager contacted.");

        try {
                ior = this.admMgr.getServerIOR(Constants.EVTMGR_NAME);
                obj = this.orb.string_to_object(ior);
                this.evtMgr = EventManagerHelper.narrow(obj);
        } catch (Exception e) {
                System.err.println(e);
                displayError("Event Manager is not responding.");
                throw new CoralConnectionException("Event Manager is not responding.");
        }

        System.out.println("Event Manager contacted.");

        try {
                ior = this.admMgr.getServerIOR(Constants.EQUMGR_NAME);
                obj = this.orb.string_to_object(ior);
                this.eqMgr = EquipmentManagerHelper.narrow(obj);
        } catch (Exception e) {
                System.err.println(e);
                displayError("Equipment Manager is not responding.");
                throw new CoralConnectionException("Equipment Manager is not responding.");
        }

        System.out.println("Equipment Manager contacted.");


        try {
                ior = this.admMgr.getServerIOR(Constants.RUNMGR_NAME);
                obj = this.orb.string_to_object(ior);
                this.runMgr = RuntimeManagerHelper.narrow(obj);
        } catch (Exception e) {
                System.err.println(e);
                displayError("Runtime Manager is not responding.");
                throw new CoralConnectionException("Runtime Manager is not responding.");
        }

        System.out.println("Runtime Manager contacted.");

        try {
                ior = this.admMgr.getServerIOR(Constants.SVCMGR_NAME);
                obj = this.orb.string_to_object(ior);
                this.svcMgr = ServiceManagerHelper.narrow(obj);
        } catch (Exception e) {
                System.err.println(e);
                displayError("Service Manager is not responding.");
                throw new CoralConnectionException("Service Manager is not responding.");
        }

        System.out.println("Service Manager contacted.");

        try {
                ior = this.admMgr.getServerIOR(Constants.HWRMGR_NAME);
                obj = this.orb.string_to_object(ior);
                this.hwrMgr = HardwareManagerHelper.narrow(obj);
                System.out.println("Hardware Manager contacted.");
        } catch (Exception e) {
                System.err.println("Hardware manager is not available.");
                System.err.println(e);
                System.err.println("Sensors access will not be available.");
        }
    }    

    @Override
    public String getTicketString() {
        return this.getTicket();
    }
    
    private void displayError(String string) {
        this.logger.error(string);
    }

    private String getTicket() {
        if (this.ticketString != null && (!this.ticketExpired())) {
            return this.ticketString;
        }
        try {
          this.ticketString = this.athMgr.authenticateByUserName(coralUser);
          this.creationEpochOfTicket = new Date().getTime();
          System.out.println ( "Got ticket: " + this.ticketString);
        } catch (Exception e) {
          System.err
              .println("CoralManagerConnector Unable to get ticket from Coral authentication manager."
                  + e.getMessage());
        }
        return this.ticketString;
    }
    
    private boolean ticketExpired() {
        if (this.ticketString == null) {
            return true;
        }
        long now = new Date().getTime();
        if (now - creationEpochOfTicket > TICKET_LIFE) {
            return true;
        }
        return false;
    }    

}
