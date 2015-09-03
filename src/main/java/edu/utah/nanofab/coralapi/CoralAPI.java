
package edu.utah.nanofab.coralapi;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.opencoral.constants.Constants;
import org.opencoral.idl.AccountNotFoundSignal;
import org.opencoral.idl.Activity;
import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidAgentSignal;
import org.opencoral.idl.InvalidMemberSignal;
import org.opencoral.idl.InvalidNicknameSignal;
import org.opencoral.idl.InvalidProcessSignal;
import org.opencoral.idl.InvalidProjectSignal;
import org.opencoral.idl.InvalidResourceSignal;
import org.opencoral.idl.InvalidRoleSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.MemberDuplicateSignal;
import org.opencoral.idl.MemberNotFoundSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.Persona;
import org.opencoral.idl.ProjectNotFoundSignal;
import org.opencoral.idl.Relation;
import org.opencoral.idl.ResourceUnavailableSignal;
import org.opencoral.idl.RoleNotFoundSignal;
import org.opencoral.idl.Timestamp;
import org.opencoral.idl.Auth.AuthManager;
import org.opencoral.idl.Auth.AuthManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManager;
import org.opencoral.idl.Equipment.EquipmentManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManagerPackage.MachineRetrievalFailedSignal;
import org.opencoral.idl.Reservation.ReservationManager;
import org.opencoral.idl.Reservation.ReservationManagerHelper;
import org.opencoral.idl.Reservation.ReservationManagerPackage.ReservationNotFoundSignal;
import org.opencoral.idl.Resource.ResourceManager;
import org.opencoral.idl.Resource.ResourceManagerHelper;
import org.opencoral.util.ResourceRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.exceptions.ConfigLoaderException;
import edu.utah.nanofab.coralapi.exceptions.InvalidAccountException;
import edu.utah.nanofab.coralapi.exceptions.InvalidAgentException;
import edu.utah.nanofab.coralapi.exceptions.InvalidCallOrderException;
import edu.utah.nanofab.coralapi.exceptions.InvalidDateException;
import edu.utah.nanofab.coralapi.exceptions.InvalidMemberException;
import edu.utah.nanofab.coralapi.exceptions.InvalidProcessException;
import edu.utah.nanofab.coralapi.exceptions.InvalidProjectException;
import edu.utah.nanofab.coralapi.exceptions.InvalidResourceException;
import edu.utah.nanofab.coralapi.exceptions.InvalidRoleException;
import edu.utah.nanofab.coralapi.exceptions.UnknownMemberException;
import edu.utah.nanofab.coralapi.helper.ActivityString;
import edu.utah.nanofab.coralapi.helper.TimestampConverter;
import edu.utah.nanofab.coralapi.helper.Utils;
import edu.utah.nanofab.coralapi.collections.Accounts;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Machines;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
import edu.utah.nanofab.coralapi.resource.Enable;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Machine;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.coralapi.helper.CoralManagerConnector;

/**
 * The CoralAPI class provides a wrapper for the primary coral services.
 * 
 * @author University of Utah Nanofab
 * @contact nanofab-support@eng.utah.edu
 */
public class CoralAPI {
    
	private String coralUser = "coral";
	private String iorUrl = "http://coral-dev-box/IOR/";
	private String configUrl = "";
	private String ticketString = "";
	private String logLevel = "DEBUG";
	private AuthManager authManager;
	private CoralManagerConnector connector = null;
	private ResourceManager resourceManager = null;
	private EquipmentManager equipmentManager = null;
	private ReservationManager reservationManager = null;
	private CoralCrypto coralCrypto;
	public static Logger logger;
           
    public CoralAPI(String coralUser, String configUrl) {
	  this.coralUser = coralUser;
	  this.configUrl = configUrl;
	  setIorUrl(iorUrl);
		this.setLogLevel(this.logLevel);
		logger = LoggerFactory.getLogger(CoralAPI.class);
		logger.debug("configURL: " + configUrl);
		logger.debug("coralUser: " + coralUser);
		this.coralCrypto = new CoralCrypto(this.configUrl);
		if (this.coralCrypto.checkKeyIsValid() == false) {
			logger.error("Bad Key Detected. Check config.jar for certs/Coral.key");
		}
    }

	private void setIorUrl(String iorUrl) {
		ConfigLoader loader = new ConfigLoader(this.configUrl);
		Properties props;
		try {
			props = loader.load();
			this.iorUrl = (String) props.get("IOR_URL");
		} catch (ConfigLoaderException e) {
			e.printStackTrace();
		}
	}
    
    private void reconnectToCoral() {
      logger.debug("Reconnecting to Coral...");
    connector = new CoralManagerConnector();
    connector.setCoralUser(this.coralUser);
    connector.setIorUrl(this.iorUrl);
    }
    
    private void reconnectToResourceManager() {
      logger.debug("Reconnecting to Resource Manager");
    resourceManager = ResourceManagerHelper.narrow(connector.getManager(Constants.RSCMGR_NAME));
    }
    
    private void getReservationManager() {
      logger.info("Getting Reservation Manager...");
        if (connector == null) {
          logger.debug("ReservationManager connector is null. Reconnecting to coral...");
            reconnectToCoral();
      }     
    if (reservationManager == null){
      reservationManager = ReservationManagerHelper.narrow(connector.getManager(Constants.RESMGR_NAME));
    }
    this.ticketString = connector.getTicketString();
  }
    
    private void getEquipmentManager(){
      logger.info("Getting Equipment Manager");
        if (connector == null) {
            logger.debug("EquipmentManager connector is null. Reconnecting to coral...");
                reconnectToCoral();
        }     
      if (equipmentManager == null){
        equipmentManager = EquipmentManagerHelper.narrow(connector.getManager(Constants.EQUMGR_NAME));
      }
      this.ticketString = connector.getTicketString();
    }
    
    private void getAuthManager(){
        logger.info("Getting Authentication Manager");
        if (connector == null) {
                logger.debug("AuthManager connector is null. Reconnecting to coral...");
                reconnectToCoral();
        }     
      if (authManager == null){
        authManager = AuthManagerHelper.narrow(connector.getManager(Constants.ATHMGR_NAME));
      }
      this.ticketString = connector.getTicketString();
    }
    
    private void getResourceManager() {
        logger.info("Getting Resource Manager");
            if (connector == null) {
                    logger.debug("ResourceManager connector is null. Reconnecting to coral...");
                    reconnectToCoral();
            }
            if (resourceManager == null) {
                logger.debug("ResourceManaer is null. Attempting to reconnect to the ResourceManager");
                    reconnectToResourceManager();
            }

            this.ticketString = connector.getTicketString();
            logger.debug("Ticket String: " + this.ticketString);
            
            try {
                    resourceManager.getAllProjects();
            } catch (org.omg.CORBA.COMM_FAILURE comm) {
                logger.debug("ORB Comm Failure");
                    connector = null;
                    resourceManager = null;
            } catch (org.omg.CORBA.OBJECT_NOT_EXIST corbaException ) {
                logger.debug("Caught CORBA error. This error is probably due to a lost coral connection.");
                logger.debug("Trying to reconnect...");
                    reconnectToCoral() ;
                    reconnectToResourceManager() ;
            } catch (ProjectNotFoundSignal e) {
                logger.debug("Project Not Found!");
            } catch (Exception e) {
                    logger.debug("General Exception was caught. See the stacktrace for more details.");
                    logger.trace(e.getMessage(), e);
            }
    }

  public Projects getProjects() throws ProjectNotFoundSignal{
      this.getResourceManager();
      org.opencoral.idl.Project[] allProjects = resourceManager.getAllProjects();
      Projects projectCollection = Projects.fromIdlProjectArray(allProjects); 
      return projectCollection;
    }
    
    public Project getProject(String name) throws ProjectNotFoundSignal {
      this.getResourceManager();
      Project project = new Project();
      try {
        project.populateFromIdlProject(resourceManager.getProject(name));
        return project;
      } catch (InvalidProjectSignal e) {
        ProjectNotFoundSignal notfound = new ProjectNotFoundSignal(name);
        throw notfound;
      } 
    }
    public void createNewMember(Member member) throws MemberDuplicateSignal, InvalidProjectSignal, Exception {
      this.getResourceManager();
      this.getAuthManager();
      member.setActive(true);
      resourceManager.addMember(member.convertToIDLMemberForRscMgr(), this.ticketString);
      
      String pass = member.getPassword();
      if (pass != "" && pass != null) {
    	  this.coralCrypto.initialize();
        byte[] encrypted_pass = this.coralCrypto.encrypt(pass);
        authManager.update(member.getName(), encrypted_pass);
      }
    }


    public void createNewProject(Project project) throws Exception {
      this.getResourceManager();
      project.setActive(true);
      resourceManager.addProject(project.convertToIdlProjectForRscMgr(), this.ticketString);
    }
    
    public void createNewProjectUnlessExists(Project project) throws Exception {
      try {
        this.getProject(project.getName());
      } catch (ProjectNotFoundSignal e) {
        this.createNewProject(project);
      }
    }
 
    public void deleteMemberFromProject(String memberName, String projectName) throws InvalidTicketSignal, MemberDuplicateSignal, InvalidProjectSignal, NotAuthorizedSignal, InvalidMemberSignal {
        this.getResourceManager();
        resourceManager.removeMemberFromProject(memberName, projectName, this.ticketString);
    }

    public void addProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
       this.getResourceManager();
       for (String member: members ) {
         resourceManager.addMemberToProject(member, project, this.ticketString);
       }
    }

    public void removeProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
       this.getResourceManager();
       for (String member: members ) {
         resourceManager.removeMemberFromProject(member, project, this.ticketString);
       }      
    }
    
    /**
     * Gets the member details as a Member object.
     * 
     * @param member The name of the coral member.
     * @return A new Member object with the members details.
     * @throws UnknownMemberException If the member wasn't found.
     * @throws Exception If any other error occurs while trying to get the member from the resource manager.
     */
    public Member getMember(String member) throws UnknownMemberException, Exception {
        this.getResourceManager();

        Member mem;
        try {
            mem = new Member(resourceManager.getMember(member));
        } catch (InvalidMemberSignal e) {
            throw new UnknownMemberException("Cannot find member: " + member);
        }

        return mem;
    }
    
    /**
     * Gets the equipment qualifications for a member.
     * 
     * @param member The name of the coral member.
     * @return A a collection of qualifications
     * @throws UnknownMemberException If the member wasn't found.
     * @throws Exception If any other error occurs while trying to get the member from the resource manager.
     */
    public Member getQualifications(String member) throws UnknownMemberException, Exception {
        this.getResourceManager();
		return null;
    }
        
    
    /**
     * Gets all the projects that the given member is currently working on.
     * 
     * @param member The name of the coral member.
     * @return The Projects collection.
     */
    public Projects getMemberProjects(String member) {
      this.getResourceManager();
      org.opencoral.idl.Project[] memberProjects = resourceManager.getAllProjectsForMember(member);
      Projects projectCollection = Projects.fromIdlProjectArray(memberProjects);
      
      return projectCollection;
    }
    
    public void addMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
       this.getResourceManager();
       for (String project: projects ) {
         resourceManager.addMemberToProject(member, project, this.ticketString);
       }      
    }
    
    public void addMemberProjectCollection(String member, Projects projects) {
      this.getResourceManager();
      
      /*
       * foreach project in projects
       *    addMemberToProject
       */
    }
    
    public void removeMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
       this.getResourceManager();
       for (String project: projects ) {
         resourceManager.removeMemberFromProject(member, project, this.ticketString);
       }      
    }

    public void addEquipmentRoleToMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
      this.getResourceManager();
     
      try {
        resourceManager.addRoleToMember(member, roleName, resource, ResourceRoles.EQUIPMENT, this.ticketString);
      } catch (InvalidRoleSignal irs ) {
        throw new InvalidRoleSignal("IOR: " + this.iorUrl + " : " + irs.getMessage());
        
      }
    }
  public void removeEquipmentRoleFromMember(String member, String roleName,
      String resource) throws IOException, InvalidTicketSignal,
      InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
        this.getResourceManager();
        resourceManager.removeRoleFromMember(member, roleName, resource, ResourceRoles.EQUIPMENT, this.ticketString);
  } 
  public void addProjectRoleToMember(String member, String roleName,
      String resource) throws IOException, InvalidTicketSignal,
      InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
      this.getResourceManager();
      resourceManager.addRoleToMember(member, roleName, resource, ResourceRoles.PROJECT, this.ticketString);
  }

  public void removeProjectRoleFromMember(String member, String roleName,
      String resource) throws IOException, InvalidTicketSignal,
      InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
      this.getResourceManager();
      resourceManager.removeRoleFromMember(member, roleName, resource, ResourceRoles.PROJECT, this.ticketString);
  }
  
  public void addSafetyFlagToMember(String member ) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
      this.addEquipmentRoleToMember(member, "safety", "Door Access");
  }
  
  public void removeSafetyFlagFromMember(String member) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
      this.removeEquipmentRoleFromMember(member, "safety", "Door Access");
  }
  
  public Members getProjectMembers(String projectName) {
    logger.debug("GetProjectMembers called for project " + projectName);
    Members matches = new Members();
    this.getResourceManager();
    Relation[] relations;
    try {
      logger.debug("Calling getMemberInfoForProject");
      relations = resourceManager.getMemberInfoForProject(projectName, true);
      for (Relation relation : relations ) {
        String memberName = relation.master;
        try {
          logger.debug("Adding " + memberName + " to resultset");
          Member temp = new Member(resourceManager.getMember(memberName));
          matches.add(temp);
          logger.debug("Added " + memberName + " to resultset");
        } catch (InvalidMemberSignal e) {
          logger.debug("Error fetching member from Relation: " + memberName + ". Not including in the result set.");
        } catch (Exception e) {
          logger.error("Error casting idlMember to apiMember");
        }
      }
    } catch (MemberNotFoundSignal e) {
      logger.debug("No members found for projectName: " + projectName + ". Returning empty list.");
    }
    logger.debug("size of resultset: " + matches.size());
    return matches;
  }

  public void createNewAccount(Account acct) throws Exception {
    this.getResourceManager();
    resourceManager.addAccount(acct.convertToIdlAccountForRscMgr(), this.ticketString);
  }

  public void createNewAccountUnlessExists(Account acct) throws Exception {
    try {
      this.getAccount(acct.getName());
    } catch (InvalidAccountSignal e) {
      this.createNewAccount(acct);
    }
  }
  
  public edu.utah.nanofab.coralapi.resource.Machine getMachine(String name) throws MachineRetrievalFailedSignal  {
    this.getEquipmentManager();
    org.opencoral.idl.Machine idlMachine = equipmentManager.findMachineNamed(name);
    logger.debug("Machine fetched: " + idlMachine.name );
    edu.utah.nanofab.coralapi.resource.Machine apiMachine = new edu.utah.nanofab.coralapi.resource.Machine();
    apiMachine.populateFromIdlMachine(idlMachine);
    return apiMachine;
  }
  
  public edu.utah.nanofab.coralapi.collections.Machines getAllMachines()  {
    this.getEquipmentManager();
    org.opencoral.idl.Machine[] allMachines = equipmentManager.allMachines();
    return Machines.fromIdlMachineArray(allMachines);
  }
  
  public edu.utah.nanofab.coralapi.resource.Account getAccount(String name) throws InvalidAccountSignal {
    this.getResourceManager();
    org.opencoral.idl.Account idlAccount = resourceManager.getAccount(name);
    logger.debug("Account fetched: " + idlAccount.name + " with edate fields (year, month, day, hour, isnull: " + idlAccount.edate.year + "," + idlAccount.edate.month + "," + idlAccount.edate.day +"," + idlAccount.edate.hour + "," + idlAccount.edate.isNull);
    Account acct = new Account();
    acct.populateFromIdlAccount(idlAccount);
    return acct;
  }
  
  public edu.utah.nanofab.coralapi.collections.Accounts getAccounts() throws AccountNotFoundSignal {
    this.getResourceManager();
    org.opencoral.idl.Account[] allAccounts = resourceManager.getAllAccounts();
    Accounts accountCollection = Accounts.fromIdlAccountArray(allAccounts); 
    return accountCollection;
  }
  public void deleteProject(String projectName) throws InvalidTicketSignal, NotAuthorizedSignal, Exception {
    this.getResourceManager();
    Project p = this.getProject(projectName);
    resourceManager.removeProject(p.convertToIdlProjectForRscMgr(), this.ticketString);
  }
        
  /**
   * Authenticates the coral account. If the coral account with the supplied
   * username and password is a valid coral account, this will return true.
   * Otherwise it will return false. Note, this function will return false if
   * null parameters are supplied.
   * 
   * @param username
   *            - the username of the coral member
   * @param password
   *            - the password for the coral username
   * @return - True if the coral account exists. False otherwise.
   */
  public boolean authenticate(String username, String password) {

    if (username == null || password == null) {
      return false;
    }
    
    this.coralCrypto.initialize();

    boolean result = false;
    this.getAuthManager();
    byte[] u = this.coralCrypto.encrypt(username);
    byte[] p = this.coralCrypto.encrypt(password);

    try {
      authManager.authenticateByUserNamePassword(u, p);
      result = true;
    } catch (InvalidMemberSignal e) {
      logger.debug("authenticate: Invalid member " + username);
    } catch (NotAuthorizedSignal e) {
      logger.debug("authenticate: Not authorized " + username);
    } catch (InvalidTicketSignal e) {
      logger.debug("authenticate: Invalid ticket " + username);
    }
    return result;
  }
  
  public void updateProject(Project project) throws InvalidTicketSignal, ProjectNotFoundSignal, InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal, Exception {
    this.getResourceManager();
    resourceManager.updateProject(project.convertToIdlProjectForRscMgr(), this.ticketString);
  }

  public void updateAccount(Account account) throws InvalidTicketSignal, AccountNotFoundSignal, NotAuthorizedSignal, Exception {
    this.getResourceManager();
    resourceManager.updateAccount(account.convertToIdlAccountForRscMgr(), this.ticketString);
  }

  public void updateMember(Member member) throws InvalidTicketSignal, MemberNotFoundSignal, InvalidProjectSignal, NotAuthorizedSignal, Exception {
    this.getResourceManager();
    resourceManager.updateMember(member.convertToIDLMemberForRscMgr(), this.ticketString);
  }
  public void close() {
    logger.debug("Shutting down Coral Services...");
    
    if (resourceManager != null) { resourceManager._release(); }
    if (authManager != null) { authManager._release(); }
    if (equipmentManager != null) { equipmentManager._release(); }
    
    try {
      connector.release();
    } catch (Exception e) {
      logger.error("Could not release the connector: " + e.getMessage());
    }
  }

  public void addLabRoleToMember(LabRole newRole) throws Exception {
    this.getResourceManager();
    
    try {
      resourceManager.addRoleToMember(
          newRole.getMember(), 
          newRole.getRole(),
          newRole.getLab(),
          "lab",
          this.ticketString);
    } catch(Exception e) {
      String message = e.getMessage();
      Throwable cause = e.getCause();
      
      if (e instanceof org.opencoral.idl.InvalidTicketSignal) {
        throw new edu.utah.nanofab.coralapi.exceptions.InvalidTicketException(message, cause);
      }
      
      if (e instanceof org.opencoral.idl.InvalidRoleSignal) {
        throw new edu.utah.nanofab.coralapi.exceptions.InvalidRoleException(message, cause);
      }
      
      if (e instanceof org.opencoral.idl.InvalidMemberSignal) {
        throw new edu.utah.nanofab.coralapi.exceptions.InvalidMemberException(message, cause);
      }
      
      if (e instanceof org.opencoral.idl.NotAuthorizedSignal) {
        throw new edu.utah.nanofab.coralapi.exceptions.NotAuthorizedException(message, cause);
      }
      
      throw e;
    }

  }

  public LabRoles getLabRoles(String username) throws RoleNotFoundSignal {
    this.getResourceManager();
    Persona[] personas = resourceManager.getPersonas(username, "*", "*", ResourceRoles.LAB, true);
    return LabRoles.fromIdlPersonaArray(personas);
  }

  /**
   * Creates a new role with the supplied name, description, and type. By default, this will
   * set the new role to active and the end date to null.
   * 
   * @param name The name of the role.
   * @param description A short description for the new role.
   * @param type The type of role.
   * 
   * @throws Exception 
   */
  public void createNewRole(String name, String description, String type) throws Exception {
    this.getResourceManager();
    
    Date date = new Date();
    Timestamp bdate = TimestampConverter.dateToTimestamp(date);
    Timestamp edate = TimestampConverter.dateToTimestamp(null);
    
    // Creates an active new Role with the supplied name, description, and type.
    org.opencoral.idl.Role r = new org.opencoral.idl.Role(false, name, description, type, true, bdate, edate);
    try {
      resourceManager.addRole(r, this.ticketString);
    } catch (Exception e) {
      String message = e.getMessage();
      Throwable cause = e.getCause();
      
      if (e instanceof org.opencoral.idl.InvalidTicketSignal) {
        throw new edu.utah.nanofab.coralapi.exceptions.InvalidTicketException(message, cause);
      }
      
      if (e instanceof org.opencoral.idl.RoleDuplicateSignal) {
        throw new edu.utah.nanofab.coralapi.exceptions.RoleDuplicateException(message, cause);
      }
      
      if (e instanceof org.opencoral.idl.NotAuthorizedSignal) {
        throw new edu.utah.nanofab.coralapi.exceptions.NotAuthorizedException(message, cause);
      }
      
      throw e;
    }
  }
  
  /**
   * Gets the given role with the supplied type.
   * 
   * @param name The name of the role.
   * @param type The type of the role.
   * 
   * @throws InvalidRoleException 
   */
  public edu.utah.nanofab.coralapi.resource.Role getRole(String name, String type) throws InvalidRoleException {
    this.getResourceManager();
    try {
      org.opencoral.idl.Role idlRole = resourceManager.getRole(name, type);
      edu.utah.nanofab.coralapi.resource.Role r = new edu.utah.nanofab.coralapi.resource.Role();
      r.populateFromIdlRole(idlRole);
      return r;
    } catch (org.opencoral.idl.InvalidRoleSignal e) {
      String message = e.getMessage();
      Throwable cause = e.getCause();
      
      throw new InvalidRoleException(message, cause);
    }
  }
  
  /**
   * Creates a new coral reservation with supplied Reservation object.
   * 
   * @param r The reservation to be created.
   * 
   * @throws Exception
   */
    public void createNewReservation(Reservation r) throws Exception {
        this.getReservationManager();
        Activity a = ActivityFactory.createReservationActivity(
            this.coralUser,
            r.getMember().getName(), 
            r.getItem(), 
          r.getProject().getName(), 
          r.getAccount().getName(),
          r.getLab(),
          r.getBdate(),
          r.getEdate());
        Activity[] activity_array = {a};
        logger.debug("Making reservation for '" + r.getMember().getName() + "' " + r.getItem());
        reservationManager.makeReservation(activity_array, this.ticketString);
    }

	public void createNewReservation(String agent, String member,
			String project, String item, String bdate, int lengthInMinutes) throws UnknownMemberException, ParseException, Exception {
		Reservation r = generateReservationObject(agent, member, project, item,
				bdate, lengthInMinutes);
		this.createNewReservation(r);
	}

	  /**
	   * Delete a coral reservation with supplied Reservation object.
	   * 
	   * @param r The reservation to be deleted.
	   * 
	   * @throws Exception
	   */
	public void deleteReservation(Reservation r) throws InvalidTicketSignal, NotAuthorizedSignal, ReservationNotFoundSignal {
		this.getReservationManager();
		Activity a = ActivityFactory.createReservationActivity(
	            this.coralUser,
	            r.getMember().getName(), 
	            r.getItem(), 
	          r.getProject().getName(), 
	          r.getAccount().getName(),
	          r.getLab(),
	          r.getBdate(),
	          r.getEdate());
		Activity[] activity_array = {a};
		reservationManager.deleteReservation(activity_array, this.ticketString);
	}

	public void deleteReservation(String agent, String member,
			String project, String item, String bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception {
		Reservation r = generateReservationObject(agent, member, project, item,
				bdate, lengthInMinutes);
		this.deleteReservation(r);
	}
	
	private Reservation generateReservationObject(String agent, String member,
			String project, String item, String bdate, int lengthInMinutes)
			throws UnknownMemberException, Exception, ProjectNotFoundSignal,
			ParseException, InvalidCallOrderException, InvalidAccountSignal,
			MachineRetrievalFailedSignal {
		Reservation r = new Reservation();
		r.setAgent(this.getMember(agent));		
		r.setMember(this.getMember(member));
		Project p = this.getProject(project);
		r.setProject(p);
		r.setItem(item);
		r.setBdate(bdate);
		r.setLength(lengthInMinutes);
		Account a = this.getAccount(p.getAccount());
		r.setAccount(a);
		Machine m = this.getMachine(item);
		r.setLab(m.getLab());
		return r;
	}
	
	
    /**
     * Gets an array of all the reservations for the specified tool that were created by the 
     * member within the time interval specified between the beginning date and ending date 
     * (bdate and edate).
     * 
     * For example, say that the member 'coral' creates a reservation for the TMV Super from 
     * 12:00-2:00 and from 2:00-3:00. If this function is then subsequently called with a 
     * bdate and edate within the 12:00-3:00 time period, then both of the reservations will 
     * be retrieved. If the bdate and edate ranges from 12:00-2:00, then only the first 
     * reservation will be retrieved.
     * 
     * @param member The member whose reservation is being retrieved.
     * @param tool The tool that the reservation was created for.
     * @param bdate The beginning date of the search.
     * @param edate The ending date of the search.
     * 
     * @return An array of Reservation objects corresponding to all of the reservations made for
     * the specified tool by the specified member in the given time range.
     * 
     * @throws Exception 
     */
  public Reservation[] getReservations(String member, String tool, Date bdate, Date edate) throws Exception {
    this.getReservationManager();
    
    Activity[] filters = Utils.createReservationSearchFilter(member, tool,
        bdate, edate);
    
    Activity lowerBound = filters[0];
    Activity upperBound = filters[1];
    
    this.logger.debug("Created filter with Lower Bound: " + TimestampConverter.dateToAdapterString(bdate));
    this.logger.debug(new ActivityString(lowerBound).toString());
    
    this.logger.debug("And Upper Bound:" + TimestampConverter.dateToAdapterString(edate));
    this.logger.debug(new ActivityString(upperBound).toString());
    
    Activity[] activities = null;
    try {
      activities = this.reservationManager.findReservation(lowerBound, upperBound);
    } catch(Exception e) {
      String message = e.getMessage();
      Throwable cause = e.getCause();
      
      if(e instanceof org.opencoral.idl.InvalidAgentSignal) {
        throw new InvalidAgentException(message, cause);
      }
      
      if(e instanceof org.opencoral.idl.InvalidProjectSignal) {
        throw new InvalidProjectException(message, cause);
      }
      
      if(e instanceof org.opencoral.idl.InvalidAccountSignal) {
        throw new InvalidAccountException(message, cause);
      }
      
      if(e instanceof org.opencoral.idl.InvalidMemberSignal) {
        throw new InvalidMemberException(message, cause);
      }
      
      if(e instanceof org.opencoral.idl.InvalidResourceSignal) {
        throw new InvalidResourceException(message, cause);
      }
      
      if(e instanceof org.opencoral.idl.InvalidProcessSignal) {
        throw new InvalidProcessException(message, cause);
      }
      
      if(e instanceof org.opencoral.idl.InvalidDateSignal) {
        throw new InvalidDateException(message, cause);
      }
      
      // If non of the exceptions above occurred, just forward the caught
      // exception onward.
      throw e;
    }
    
    Reservation[] reservations = new Reservation[activities.length];
    int i = 0;
    for (Activity act : activities) {
      reservations[i++] = ActivityFactory.convertActivityToReservation(act);
    }
    
    return reservations;
  }
  
  /**
   * Same as above, but without member filter
   * @param tool
   * @param bdate
   * @param edate
   * @return
   * @throws Exception
   */
  public Reservation[] getReservations(String tool, Date bdate, Date edate) throws Exception {
	  return getReservations("*", tool, bdate, edate);
  }

  public void enable(Enable enableActivity) throws InvalidTicketSignal, InvalidAgentSignal, InvalidProjectSignal, InvalidAccountSignal, InvalidMemberSignal, InvalidResourceSignal, InvalidProcessSignal, ResourceUnavailableSignal, NotAuthorizedSignal{
    this.getEquipmentManager();
    Activity activity = ActivityFactory.createRunActivity(
            this.coralUser,
            enableActivity.getMember().getName(), 
            enableActivity.getItem(), 
          enableActivity.getProject().getName(), 
          enableActivity.getAccount().getName(),
          enableActivity.getLab(),
          enableActivity.getBdate(),
          enableActivity.getEdate());   
    activity.agent = enableActivity.getAgent().getName();
    equipmentManager.enable(activity, false, this.ticketString);
  }
  
  public void enable(String agent, String member, String project, String account, String machineName) throws UnknownMemberException, Exception {
    Enable enableActivity = new Enable();
    enableActivity.setAgent(this.getMember(agent));
    enableActivity.setMember(this.getMember(member));
    enableActivity.setBdate(new Date());
    enableActivity.setEdate(new Date());
    
    edu.utah.nanofab.coralapi.resource.Machine m = this.getMachine(machineName);
    enableActivity.setItem(m.getName());
    enableActivity.setLab(m.getLab());
    
    Project p = this.getProject(project);
    enableActivity.setProject(p);
    
    Account a =  this.getAccount(account);
    enableActivity.setAccount(a);
    
    this.enable(enableActivity);
  }
  
  public void enable(String agent, String member, String project, String machineName) throws UnknownMemberException, Exception {
    Project p = this.getProject(project);
    Account account = this.getAccount(p.getAccount());
    this.enable(agent, member, project, account.getName(), machineName);
  }
  
  public void disable (String agent, String machine) throws InvalidTicketSignal, InvalidAgentSignal, InvalidResourceSignal, ResourceUnavailableSignal, NotAuthorizedSignal {
    this.getEquipmentManager();
    equipmentManager.disable(agent, machine, false, this.ticketString);
  }
  
  /**
   * Updates a member's remote password.
   * 
   * @param member
   *            The user name of the coral member.
   * @param newPassword
   *            The users new remote password.
   * @throws InvalidTicketSignal
   * @throws NotAuthorizedSignal
   * @throws InvalidMemberSignal
   */
  public void updatePassword(String member, String newPassword)
      throws InvalidMemberSignal, NotAuthorizedSignal,
      InvalidTicketSignal {

    this.getAuthManager();
    this.coralCrypto.initialize();    
    byte[] pass = this.coralCrypto.encrypt(newPassword);
    this.authManager.update(member, pass);
  }
  
  public boolean checkKeyIsValid() {
	  return this.coralCrypto.checkKeyIsValid();
  }
  
//  qualify(tool, member, role)
//  disqualify(tool, member, role)
//  reserve( tool, agent, member, project, account, begin time, end time(or length) ) 
  
  /**
   * Gets the log level for this CoralAPI instance.
   * 
   * @return The logLevel
   */
  public String getLogLevel() {
    return logLevel;
  }

  /**
   * Sets the log level for this CoralAPI instance. If the supplied log level is
   * invalid, the log level will remain unchanged.
   * 
   * The six logging levels used by Log are (in order):
   *
   *  trace (the least serious)
   *  debug
   *  info
   *  warn
   *  error
   *  fatal (the most serious)
   * 
   * @param logLevel The logLevel to set
   */
  public void setLogLevel(String logLevel) {
    
    String level = logLevel.toUpperCase(); // Convert the supplied level to upper case.
    String[] levels = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"};
    boolean validLevel = false;
    
    // Loop through each of the log levels and check if the supplied level
    // matches one of the valid levels.
    for (String l : levels) {
      if (level.equals(l)) {
        validLevel = true;
        break;
      }
    }
    
    // If the supplied level was valid, change the log level.
    if(validLevel) {
      this.logLevel = level;
      System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, level);
    }
  }
}
