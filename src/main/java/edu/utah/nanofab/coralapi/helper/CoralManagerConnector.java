package edu.utah.nanofab.coralapi.helper;

import java.util.Date;
import java.util.HashMap;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.opencoral.constants.Constants;
import org.opencoral.idl.Admin.AdminManager;
import org.opencoral.idl.Auth.AuthManager;
import org.opencoral.idl.Auth.AuthManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManager;
import org.opencoral.idl.Reservation.ReservationManager;
import org.opencoral.idl.Resource.ResourceManager;
import org.opencoral.idl.Cost.CostManager;
import org.opencoral.idl.Cost.CostManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManagerHelper;
import org.opencoral.idl.Hardware.HardwareManager;
import org.opencoral.idl.Hardware.HardwareManagerHelper;
import org.opencoral.idl.Policy.PolicyManager;
import org.opencoral.idl.Policy.PolicyManagerHelper;
import org.opencoral.idl.Reservation.ReservationManagerHelper;
import org.opencoral.idl.Resource.ResourceManagerHelper;
import org.opencoral.idl.Runtime.RuntimeManager;
import org.opencoral.idl.Runtime.RuntimeManagerHelper;
import org.opencoral.idl.Staff.StaffManager;
import org.opencoral.idl.Service.ServiceManager;
import org.opencoral.idl.Service.ServiceManagerHelper;
import org.opencoral.idl.Staff.StaffManagerHelper;

import org.opencoral.util.AdminManagerConnection;

public class CoralManagerConnector {
  
  private AdminManagerConnection adminManagerConnection;
  private final String[] validServers= {
            Constants.ATHMGR_NAME,
            Constants.ADMMGR_NAME,
            Constants.EQUMGR_NAME,
            Constants.RSCMGR_NAME,
            Constants.RESMGR_NAME,
            Constants.CSTMGR_NAME,
            Constants.HWRMGR_NAME,
            Constants.POLMGR_NAME,
            Constants.RUNMGR_NAME,
            Constants.STFMGR_NAME,
            Constants.SVCMGR_NAME
        };

  private AuthManager authManager;
  private AdminManager adminManager;
  private EquipmentManager equipmentManager;  
  private ResourceManager resourceManager;
  private ReservationManager reservationManager;
  private CostManager costManager;
  private HardwareManager hardwareManager;
  private PolicyManager policyManager;
  private RuntimeManager runtimeManager;
  private StaffManager staffManager;
  private ServiceManager serviceManager;
  
  private ORB orb = null;
  
  private String iorUrl;
  private String coralUser;
  private String ticketString;
  
  private long creationEpochOfTicket = 0; //when a ticket
  private long TICKET_LIFE = 5 * 60 * 1000; //five minutes of ticket life

    public CoralManagerConnector(String coralUser, String iorUrl) {
        this.setCoralUser(coralUser);
        this.setIorUrl(iorUrl);
    }
  
  public void setIorUrl(String iorUrl) {
    this.iorUrl = iorUrl;
  }

  public void setCoralUser(String coralUser) {
    this.coralUser = coralUser;
  }
  
  /**
   * Gets the supplied server manager fom coral.
   * 
   * @param serverName The name of the server..
   * @return
   */
  public org.omg.CORBA.Object getManager(String serverName) {
    
    org.omg.CORBA.Object returnManager;
    String authIorUrl;
    String costIorUrl;
    
    if (!ensureConnection()) return null;

    if (serverName.equalsIgnoreCase(Constants.ATHMGR_NAME)) {
        return this.getAuthManager();
    } else if (serverName.equalsIgnoreCase(Constants.ADMMGR_NAME)) {
        return this.getAdminManager();
    } else if (serverName.equalsIgnoreCase(Constants.CSTMGR_NAME)) {
        return this.getCostManager();
    } else if (serverName.equalsIgnoreCase(Constants.EQUMGR_NAME)) {
        return this.getEquipmentManager();
    } else if (serverName.equalsIgnoreCase(Constants.HWRMGR_NAME)) {
        return this.getHardwareManager();
    } else if (serverName.equalsIgnoreCase(Constants.POLMGR_NAME)) {
        return this.getPolicyManager();
    } else if (serverName.equalsIgnoreCase(Constants.RESMGR_NAME)) {
        return this.getReservationManager();
    } else if (serverName.equalsIgnoreCase(Constants.RSCMGR_NAME)) {
        return this.getResourceManager();
    } else if (serverName.equalsIgnoreCase(Constants.RUNMGR_NAME)) {
        return this.getRuntimeManager();
    } else if (serverName.equalsIgnoreCase(Constants.SVCMGR_NAME)) {
        return this.getServiceManager();
    } else if (serverName.equalsIgnoreCase(Constants.STFMGR_NAME)) {
        return this.getStaffManager();
    } else { 
        Exception e = new InvalidManagerNameException("invalid server name: " + serverName);
        return null;
    }
  } 

    private boolean ensureConnection() {
        // Get the coral IOR.
        if (this.iorUrl == null) {
            this.iorUrl = "http://localhost/IOR/";
        }   // Get default user to communicate with coral.
        if (this.coralUser == null) {
            this.coralUser = "coral";
        }
        
        if (this.getAdminManager() == null) {
            System.err.println("CoralManagerConnector Unable to connect to Coral admin manager.");
            return false;
        }
        if (this.getAuthManager() == null) {
            System.err
                    .println("CoralManagerConnector Unable to connect to Coral authentication manager.");
            return false;
        }
        //get the ticket from admin manager
        if (this.getTicket() == null) {
            System.err
                    .println("CoralManagerConnector Unable to get ticket from Coral authentication manager.");
            return false;
        }
        return true;
    }
    
  public AdminManager getAdminManager() {
    if (this.adminManager != null) {
        return this.adminManager;
    }
       
    AdminManagerConnection adminConnection = this.getAdminManagerConnection();
    int attempts = 0;
    int maxAttempts = 1;
    
    while (attempts++ < maxAttempts && adminManager == null) {
      try {
        adminManager = adminManagerConnection.connect(iorUrl);
      } catch (Exception e) {
        try {
          Thread.sleep(3000);
        } catch (Exception e2) {
          System.err
              .println("CoralManagerConnector Unable to connect to Coral admin manager.  "
                  + e2.getMessage());
        }
      }
    }
    return adminManager;
  }
    
    public AuthManager getAuthManager() {
        if (authManager != null) {
            return authManager;
        }

        org.omg.CORBA.Object authManagerGeneric = getArbitraryManager(Constants.ATHMGR_NAME);
        authManager = AuthManagerHelper.narrow(authManagerGeneric);
        return authManager;
    }

    public CostManager getCostManager() {
        this.ensureConnection();
        if (costManager != null) {
            return costManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.CSTMGR_NAME);
        costManager = CostManagerHelper.narrow(managerGeneric);
        return costManager;
    }
    
    public EquipmentManager getEquipmentManager() {
        this.ensureConnection();
        if (equipmentManager != null) {
            return equipmentManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.EQUMGR_NAME);
        equipmentManager = EquipmentManagerHelper.narrow(managerGeneric);
        return equipmentManager;
    }

    public HardwareManager getHardwareManager() {
        this.ensureConnection();
        if (hardwareManager != null) {
            return hardwareManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.HWRMGR_NAME);
        hardwareManager = HardwareManagerHelper.narrow(managerGeneric);
        return hardwareManager;
    }
    
    public PolicyManager getPolicyManager() {
        this.ensureConnection();
        if (policyManager != null) {
            return policyManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.POLMGR_NAME);
        policyManager = PolicyManagerHelper.narrow(managerGeneric);
        return policyManager;
    }
    
    public ReservationManager getReservationManager() {
        this.ensureConnection();
        if (reservationManager != null) {
            return reservationManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.RESMGR_NAME);
        reservationManager = ReservationManagerHelper.narrow(managerGeneric);
        return reservationManager;
    }
    
    public ResourceManager getResourceManager() {
        this.ensureConnection();
        if (resourceManager != null) {
            return resourceManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.RSCMGR_NAME);
        resourceManager = ResourceManagerHelper.narrow(managerGeneric);
        return resourceManager;
    }
    
    public RuntimeManager getRuntimeManager() {
        this.ensureConnection();
        if (runtimeManager != null) {
            return runtimeManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.RUNMGR_NAME);
        runtimeManager = RuntimeManagerHelper.narrow(managerGeneric);
        return runtimeManager;
    }

    public StaffManager getStaffManager() {
        this.ensureConnection();
        if (staffManager != null) {
            return staffManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.STFMGR_NAME);
        staffManager = StaffManagerHelper.narrow(managerGeneric);
        return staffManager;
    }
    
    public ServiceManager getServiceManager() {
        this.ensureConnection();
        if (serviceManager != null) {
            return serviceManager;
        }

        org.omg.CORBA.Object managerGeneric = getArbitraryManager(Constants.SVCMGR_NAME);
        serviceManager = ServiceManagerHelper.narrow(managerGeneric);
        return serviceManager;
    }

  public org.omg.CORBA.Object getArbitraryManager(String serverName) {
        int attempts = 0;
        int maxAttempts = 1;
        org.omg.CORBA.Object theManager = null;
        while (attempts++ < maxAttempts && theManager == null) {
            try {
                String authIorUrl = getAdminManager().getServerIOR(serverName);
                theManager = orb.string_to_object(authIorUrl);
            } catch (Exception e) {
                System.err
                        .println("CoralManagerConnector Unable to connect to Coral authentication manager.  "
                                + e.getMessage());
            }
        }
        return theManager;      
  }
  
  public String getTicketString() {
    if ( this.ticketExpired()) {
        System.out.println("Generating new ticket after expiration");
        this.getTicket();
    }
    return this.ticketString;
  }

  public void release() {
    System.out.println("CoralManagerConnector release called");
    
    if (authManager != null) {
      authManager._release();
    }
    if (adminManager != null) {
      adminManager._release();
    }
    if (equipmentManager != null) {
      equipmentManager._release();
    }
    if (resourceManager != null) {
      resourceManager._release();
    }
    if (reservationManager != null) {
      reservationManager._release();
    }
    if (costManager != null) {
      costManager._release();
    }
    if (hardwareManager != null) {
      hardwareManager._release();
    }
    if (policyManager != null) {
      policyManager._release();
    }
    if (runtimeManager != null) {
      runtimeManager._release();
    }
    if (staffManager != null) {
      staffManager._release();
    }
    if (serviceManager != null) {
      serviceManager._release();
    }
    if (adminManagerConnection != null) {
      adminManagerConnection.disconnect();
    }
    if (orb != null) {
      orb.destroy();
    }
    System.out.println("CoralManagerConnector release finished");
  }

    private AdminManagerConnection getAdminManagerConnection() {
        if (this.adminManagerConnection == null) {
            this.adminManagerConnection = new AdminManagerConnection(this.getOrb());            
        }
        return this.adminManagerConnection;
    }
    
    private ORB getOrb() {
        if (this.orb == null) {
            this.orb = ORB.init((String[]) null, null);
        }
        return this.orb;
    }

    private String getTicket() {
        if (this.ticketString != null && (!this.ticketExpired())) {
            return this.ticketString;
        }
        try {
          this.ticketString = authManager.authenticateByUserName(coralUser);
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
