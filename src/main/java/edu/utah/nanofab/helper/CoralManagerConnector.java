package edu.utah.nanofab.helper;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.opencoral.constants.Constants;
import org.opencoral.idl.Admin.AdminManager;
import org.opencoral.idl.Admin.Server;
import org.opencoral.idl.Auth.AuthManager;
import org.opencoral.idl.Auth.AuthManagerHelper;
import org.opencoral.idl.Hardware.HardwareManager;
import org.opencoral.idl.Hardware.HardwareManagerHelper;
import org.opencoral.util.AdminManagerConnection;

public class CoralManagerConnector {
	private AuthManager authManager;
	private AdminManager adminManager;
	private AdminManagerConnection adminManagerConnection;
	private ORB orb;
	
	private String iorUrl = null;
	private String coralUser = null;
	private String ticketString = null;

	public void setIorUrl(String iorUrl) {
		this.iorUrl = iorUrl;
	}

	public void setCoralUser(String coralUser) {
		this.coralUser = coralUser;
	}

	
	/*
	 * Get an arbitrary Manager from coral
	 * 
	 * example: 
	 * CoralManagerConnector cmc = new CoralManagerConnector();  
	 * hardwareManager = HardwareManagerHelper.narrow( cmc.getManager(Constants.HWRMGR_NAME) );
	 * //do some stuff with hardwareManager
	 * hardwareManager._release();
	 * 
	 */
	public org.omg.CORBA.Object getManager(String serverName ) {
		
		org.omg.CORBA.Object returnManager;
		String authIorUrl = null;
		String costIorUrl = null;
		
		// get coral ior
		if (this.iorUrl == null) {
			this.iorUrl = "http://localhost/IOR/";
		}

		// get default user to communicate with coral
		if (this.coralUser == null) {
			this.coralUser = "coral";
		}

		//we probably don't need this
		String lab = null;

		// get admin manager via ior
		adminManager = null;
		orb = ORB.init((String[]) null, null);
		adminManagerConnection = new AdminManagerConnection(orb);
		int maxAttempts = 1;
		int attempts = 0;
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
					return null;
				}
			}
		}

		if (adminManager == null) {
			System.err
					.println("CoralManagerConnector Unable to connect to Coral admin manager.");
			return null;
		}

		// get authentication manager from admin manager
		authManager = null;
		attempts = 0;
		while (attempts++ < maxAttempts && authManager == null) {
			try {
				authIorUrl = adminManager.getServerIOR(Constants.ATHMGR_NAME);
				org.omg.CORBA.Object obj = orb.string_to_object(authIorUrl);
				authManager = AuthManagerHelper.narrow(obj);
			} catch (Exception e) {
				System.err
				.println("CoralManagerConnector Unable to connect to Coral authentication manager.  "
						+ e.getMessage());
			}
		}

		if (authManager == null) {
			System.err
					.println("CoralManagerConnector Unable to connect to Coral authentication manager.");
			return null;
		}

		//get the ticket from admin manager
		this.ticketString = null;
		try {
			this.ticketString = authManager.authenticateByUserName(coralUser);
			System.out.println ( "Got ticket: " + this.ticketString);
		} catch (Exception e) {
			System.err
					.println("CoralManagerConnector Unable to get ticket from Coral authentication manager."
							+ e.getMessage());
			return null;
		}

		if (this.ticketString == null) {
			System.err
					.println("CoralManagerConnector Unable to get ticket from Coral authentication manager.");
			return null;
		}

		// get cost manager from admin manager
		returnManager = null;
		attempts = 0;
		while (attempts++ < maxAttempts && returnManager == null) {
			try {
				costIorUrl = adminManager.getServerIOR(serverName);
				org.omg.CORBA.Object obj = orb.string_to_object(costIorUrl);
				returnManager = obj;
			} catch (Exception e) {
				try {
					Thread.sleep(3000);
				} catch (Exception e2) {
					System.err
							.println("CoralManagerConnector Unable to connect to Coral hardware manager.  "
									+ e2.getMessage());
					return null;
				}
			}
		}

		if (returnManager == null) {
			System.err
					.println("CoralManagerConnector Unable to connect to Coral hardware manager.");
			return null;
		}
		
		return returnManager;
	}	
	
	public String getTicketString() {
		return this.ticketString;
	}

	public void release() {
		System.out.println("CoralManagerConnector release called");
		authManager._release();
		adminManager._release();
		adminManagerConnection.disconnect();

		orb.destroy();
		System.out.println("CoralManagerConnector release finished");
	}
	
}