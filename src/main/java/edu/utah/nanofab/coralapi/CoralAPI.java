
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
import edu.utah.nanofab.coralapi.collections.EquipmentRoles;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Machines;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
import edu.utah.nanofab.coralapi.collections.Reservations;
import edu.utah.nanofab.coralapi.exceptions.CoralConnectionException;
import edu.utah.nanofab.coralapi.exceptions.RequestFailedException;
import edu.utah.nanofab.coralapi.helper.CoralConnectorInterface;
import edu.utah.nanofab.coralapi.resource.Enable;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Machine;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.ProjectRole;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.coralapi.helper.CoralManagerConnector;
import edu.utah.nanofab.coralapi.helper.CoralManagers;
import static edu.utah.nanofab.coralapi.helper.TimestampConverter.stringToDate;
import edu.utah.nanofab.coralapi.resource.RunDataProcess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import org.opencoral.corba.RundataAdapter;
import org.opencoral.idl.InvalidDateSignal;
import org.opencoral.idl.Reservation.ReservationManagerPackage.ReservationDuplicateSignal;
import org.opencoral.idl.Runtime.NullReturnException;
import org.opencoral.idl.Runtime.RuntimeManager;
import org.opencoral.idl.Runtime.ServerErrorException;
import org.opencoral.runtime.xml.RmRunData;
import org.opencoral.util.RmUtil;
import org.opencoral.util.XMLType;

/**
 * The CoralAPI class provides a wrapper for the primary coral services.
 * 
 * @author University of Utah Nanofab
 * @contact nanofab-support@eng.utah.edu
 */
public class CoralAPI implements CoralAPIInterface {
    
	private String coralUser = "coral";
	private String iorUrl = "http://coral-dev-box/IOR/";
	private String configUrl = "";
	private String logLevel = "DEBUG";
	private CoralConnectorInterface connector = null;
	private CoralCrypto coralCrypto;
	public static Logger logger;
           
    public CoralAPI(String coralUser, String configUrl) throws CoralConnectionException {
        setup(coralUser, configUrl);
    }

    public void setup(String coralUser, String configUrl) throws CoralConnectionException {
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
        connector = new CoralManagers(this.coralUser, this.iorUrl);
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
    
  public Projects getProjects() throws ProjectNotFoundSignal{
      org.opencoral.idl.Project[] allProjects = connector.getResourceManager().getAllProjects();
      Projects projectCollection = Projects.fromIdlProjectArray(allProjects); 
      return projectCollection;
    }
  public Projects getAllProjects() throws ProjectNotFoundSignal{
      return getProjects();
  }
  
    public Project getProject(String name) throws ProjectNotFoundSignal {
      Project project = new Project();
      try {
        project.populateFromIdlProject(connector.getResourceManager().getProject(name));
        return project;
      } catch (InvalidProjectSignal e) {
        ProjectNotFoundSignal notfound = new ProjectNotFoundSignal(name);
        throw notfound;
      }
    }
    
    public void createNewMember(Member member) throws MemberDuplicateSignal, InvalidProjectSignal, Exception {
      member.setActive(true);
      connector.getResourceManager().addMember(member.convertToIDLMemberForRscMgr(), connector.getTicketString());
      
      String pass = member.getPassword();
      if (pass != "" && pass != null) {
    	  this.coralCrypto.initialize();
        byte[] encrypted_pass = this.coralCrypto.encrypt(pass);
        connector.getAuthManager().update(member.getName(), encrypted_pass);
      }
    }

    public void createNewProject(Project project) throws Exception {
      project.setActive(true);
      connector.getResourceManager().addProject(project.convertToIdlProjectForRscMgr(), connector.getTicketString());
    }
    
    public void createNewProjectUnlessExists(Project project) throws Exception {
      try {
        this.getProject(project.getName());
      } catch (ProjectNotFoundSignal e) {
        this.createNewProject(project);
      }
    }
 
    public void deleteMemberFromProject(String memberName, String projectName) throws InvalidTicketSignal, MemberDuplicateSignal, InvalidProjectSignal, NotAuthorizedSignal, InvalidMemberSignal {
        connector.getResourceManager().removeMemberFromProject(memberName, projectName, connector.getTicketString());
    }

    public void addProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
       
       for (String member: members ) {
         connector.getResourceManager().addMemberToProject(member, project, connector.getTicketString());
       }
    }

    public void removeProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
       
       for (String member: members ) {
         connector.getResourceManager().removeMemberFromProject(member, project, connector.getTicketString());
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
        

        Member mem;
        try {
            mem = new Member(connector.getResourceManager().getMember(member));
        } catch (InvalidMemberSignal e) {
            throw new UnknownMemberException("Cannot find member: " + member);
        }

        return mem;
    }
    
    /**
     */
    public Members getAllMembers() throws UnknownMemberException, Exception {
        return getAllMembersWithActiveOption(false);
    }    
    
    public Members getAllActiveMembers() throws UnknownMemberException, Exception {
        return getAllMembersWithActiveOption(true);
    }    
    
    private Members getAllMembersWithActiveOption(boolean activeOnly) throws UnknownMemberException, Exception {
        
        org.opencoral.idl.Member[] rscMembers = connector.getResourceManager().getAllMembers();
        Members members = new Members();
        for( org.opencoral.idl.Member m : rscMembers) {
            if (activeOnly) {
                if (m.active) {
                    members.add(new Member(m));
                }
            } else {
                members.add(new Member(m));
            }
        }
        return members;
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
        
		return null;
    }
        
    
    /**
     * Gets all the projects that the given member is currently working on.
     * 
     * @param member The name of the coral member.
     * @return The Projects collection.
     */
    public Projects getMemberProjects(String member) {
      
      org.opencoral.idl.Project[] memberProjects = connector.getResourceManager().getAllProjectsForMember(member);
      Projects projectCollection = Projects.fromIdlProjectArray(memberProjects);
      
      return projectCollection;
    }
    
    public HashMap<String, ArrayList<String>> getAllMemberProjects(boolean activeOnly) {
        Relation[] memberProjects = new Relation[0];
        try {
            memberProjects = connector.getResourceManager().getProjectInfoForAllMembers(activeOnly);
        } catch (ProjectNotFoundSignal ex) {
        }
        
        if (activeOnly) {
            memberProjects = this.filterRelationsToOnlyActiveMembers(memberProjects);
        }
        
        HashMap<String, ArrayList<String>> map = this.convertMemberProjectsRelationToMap(memberProjects);
        return map;
    }
    
    public void addMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
       
       for (String project: projects ) {
         connector.getResourceManager().addMemberToProject(member, project, connector.getTicketString());
       }      
    }
    
    public void addMemberProjectCollection(String member, Projects projects) {
      
      
      /*
       * foreach project in projects
       *    addMemberToProject
       */
    }
    
    public void removeMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
       
       for (String project: projects ) {
         connector.getResourceManager().removeMemberFromProject(member, project, connector.getTicketString());
       }      
    }

    public void addEquipmentRoleToMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
      try {
        connector.getResourceManager().addRoleToMember(member, roleName, resource, ResourceRoles.EQUIPMENT, connector.getTicketString());
      } catch (InvalidRoleSignal irs ) {
        throw new InvalidRoleSignal("IOR: " + this.iorUrl + " : " + irs.getMessage());
        
      }
    }
  
  public void removeEquipmentRoleFromMember(String member, String roleName,
      String resource) throws IOException, InvalidTicketSignal,
      InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
        
        removeGenericRoleFromMember(member, roleName, resource, ResourceRoles.EQUIPMENT);
  }
  
  public void removeLabRoleFromMember(String member, String roleName,
      String lab) throws IOException, InvalidTicketSignal,
      InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
        
        connector.getResourceManager().removeRoleFromMember(member, roleName, lab, ResourceRoles.LAB, connector.getTicketString());
  }
    
  public void removeGenericRoleFromMember(String member, String roleName,
      String resource, String roleType) throws IOException, InvalidTicketSignal,
      InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
        
        connector.getResourceManager().removeRoleFromMember(member, roleName, resource, roleType, connector.getTicketString());
  }
  
  public void addProjectRoleToMember(String member, String roleName,
      String resource) throws IOException, InvalidTicketSignal,
      InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
      
      connector.getResourceManager().addRoleToMember(member, roleName, resource, ResourceRoles.PROJECT, connector.getTicketString());
  }

  public void removeProjectRoleFromMember(String member, String roleName,
      String resource) throws IOException, InvalidTicketSignal,
      InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
      
      connector.getResourceManager().removeRoleFromMember(member, roleName, resource, ResourceRoles.PROJECT, connector.getTicketString());
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
    
    Relation[] relations;
    try {
      logger.debug("Calling getMemberInfoForProject");
      relations = connector.getResourceManager().getMemberInfoForProject(projectName, true);
      for (Relation relation : relations ) {
        String memberName = relation.master;
        try {
          logger.debug("Adding " + memberName + " to resultset");
          Member temp = new Member(connector.getResourceManager().getMember(memberName));
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
    
    connector.getResourceManager().addAccount(acct.convertToIdlAccountForRscMgr(), connector.getTicketString());
  }

  public void createNewAccountUnlessExists(Account acct) throws Exception {
    try {
      this.getAccount(acct.getName());
    } catch (InvalidAccountSignal e) {
      this.createNewAccount(acct);
    }
  }
  
  public edu.utah.nanofab.coralapi.resource.Machine getMachine(String name) throws MachineRetrievalFailedSignal  {
    
    org.opencoral.idl.Machine idlMachine = connector.getEquipmentManager().findMachineNamed(name);
    logger.debug("Machine fetched: " + idlMachine.name );
    edu.utah.nanofab.coralapi.resource.Machine apiMachine = new edu.utah.nanofab.coralapi.resource.Machine();
    apiMachine.populateFromIdlMachine(idlMachine);
    return apiMachine;
  }
  
  public edu.utah.nanofab.coralapi.collections.Machines getAllMachines()  {
    
    org.opencoral.idl.Machine[] allMachines = connector.getEquipmentManager().allMachines();
    return Machines.fromIdlMachineArray(allMachines);
  }
  
  public edu.utah.nanofab.coralapi.resource.Account getAccount(String name) throws InvalidAccountSignal {
    
    org.opencoral.idl.Account idlAccount = connector.getResourceManager().getAccount(name);
    logger.debug("Account fetched: " + idlAccount.name + " with edate fields (year, month, day, hour, isnull: " + idlAccount.edate.year + "," + idlAccount.edate.month + "," + idlAccount.edate.day +"," + idlAccount.edate.hour + "," + idlAccount.edate.isNull);
    Account acct = new Account();
    acct.populateFromIdlAccount(idlAccount);
    return acct;
  }
  
  public edu.utah.nanofab.coralapi.collections.Accounts getAccounts() throws AccountNotFoundSignal {
    org.opencoral.idl.Account[] allAccounts = connector.getResourceManager().getAllAccounts();
    Accounts accountCollection = Accounts.fromIdlAccountArray(allAccounts); 
    return accountCollection;
  }
  
  public edu.utah.nanofab.coralapi.collections.Accounts getAllAccounts() throws AccountNotFoundSignal {
      return getAccounts();
  }
  
  
  public void deleteProject(String projectName) throws InvalidTicketSignal, NotAuthorizedSignal, Exception {
    
    Project p = this.getProject(projectName);
    connector.getResourceManager().removeProject(p.convertToIdlProjectForRscMgr(), connector.getTicketString());
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
    byte[] u = this.coralCrypto.encrypt(username);
    byte[] p = this.coralCrypto.encrypt(password);

    try {
      connector.getAuthManager().authenticateByUserNamePassword(u, p);
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

  public void activateMember(String memberName) throws UnknownMemberException, Exception {
	    Member member = getMember(memberName);
	    connector.getResourceManager().activateMember(
	    		memberName,
	    		member.getProject(),
	    		connector.getTicketString());
  }
	
  public void activateMemberWithProject(String memberName, String projectName) 
          throws InvalidTicketSignal, ProjectNotFoundSignal, MemberNotFoundSignal, InvalidProjectSignal, NotAuthorizedSignal {
	    connector.getResourceManager().activateMember(
	    		memberName,
	    		projectName,
	    		connector.getTicketString());
  }
	
  public void deactivateMember(String memberName) throws InvalidTicketSignal, MemberNotFoundSignal, NotAuthorizedSignal {
		    connector.getResourceManager().inactivateMember(memberName, connector.getTicketString());
  }
    
  public void activateProject(String projectName) throws InvalidTicketSignal, ProjectNotFoundSignal, InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal, Exception {
	    Project project = getProject(projectName);
	    connector.getResourceManager().activateProject(projectName, project.getAccount(), connector.getTicketString());
  }

  public void deactivateProject(String projectName) throws InvalidTicketSignal, ProjectNotFoundSignal, InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal, Exception {
	    connector.getResourceManager().inactivateProject(projectName, connector.getTicketString());
  }
  
  public void updateProject(Project project) throws InvalidTicketSignal, ProjectNotFoundSignal, InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal, Exception {
    connector.getResourceManager().updateProject(project.convertToIdlProjectForRscMgr(), connector.getTicketString());
  }

  public void updateAccount(Account account) throws InvalidTicketSignal, AccountNotFoundSignal, NotAuthorizedSignal, Exception {
    
    connector.getResourceManager().updateAccount(account.convertToIdlAccountForRscMgr(), connector.getTicketString());
  }

  public void updateMember(Member member) throws InvalidTicketSignal, MemberNotFoundSignal, InvalidProjectSignal, NotAuthorizedSignal, Exception {
    
    connector.getResourceManager().updateMember(member.convertToIDLMemberForRscMgr(), connector.getTicketString());
  }
  public void close() {
    logger.debug("Shutting down Coral Services...");
    
    try {
      connector.release();
    } catch (Exception e) {
      logger.error("Could not release the connector: " + e.getMessage());
    }
  }
  
  public void reInitialize() throws CoralConnectionException {
      this.close();
      connector = null; // allow GC
      this.setup(this.coralUser, this.configUrl);
  }

  public void addLabRoleToMember(LabRole newRole) throws Exception {
	  this.addGenericRoleToMember(newRole.getMember(),
			  newRole.getRole(), 
			  newRole.getLab(),
			  "lab"
			  );
  }
  
  public void addProjectRoleToMember(ProjectRole newRole) throws Exception {
	  this.addProjectRoleToMember(
			  newRole.getMember(),
			  newRole.getRole(), 
			  newRole.getProject()
			  );
  }
  
  private void addGenericRoleToMember(String member, 
		  String role, 
		  String target,
		  String type) throws Exception
  {
    
    try {
      connector.getResourceManager().addRoleToMember(
          member, 
          role,
          target,
          type,
          connector.getTicketString());
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
    Persona[] personas = connector.getResourceManager().getPersonas(username, "*", "*", ResourceRoles.LAB, true);
    return LabRoles.fromIdlPersonaArray(personas);
  }


  public EquipmentRoles getEquipmentRoles(String username) throws RoleNotFoundSignal {
    Persona[] personas = connector.getResourceManager().getPersonas(username, "*", "*", ResourceRoles.LAB, true);
    return EquipmentRoles.fromIdlPersonaArray(personas);
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
    
    
    Date date = new Date();
    Timestamp bdate = TimestampConverter.dateToTimestamp(date);
    Timestamp edate = TimestampConverter.dateToTimestamp(null);
    
    // Creates an active new Role with the supplied name, description, and type.
    org.opencoral.idl.Role r = new org.opencoral.idl.Role(false, name, description, type, true, bdate, edate);
    try {
      connector.getResourceManager().addRole(r, connector.getTicketString());
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
    
    try {
      org.opencoral.idl.Role idlRole = connector.getResourceManager().getRole(name, type);
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
    public void createNewReservation(Reservation r) throws RequestFailedException  {
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
        boolean encounteredError = false;
        String errorMsg = "";

        try {
            ReservationManager resMgr = connector.getReservationManager();
            resMgr.makeReservation(activity_array, connector.getTicketString());
        } catch (NotAuthorizedSignal e) {
                throw new RequestFailedException(e.reason);
        } catch (ResourceUnavailableSignal e) {
                throw new RequestFailedException(e.reason);
        } catch (ReservationDuplicateSignal e) {
                errorMsg = "Requested reservation overlaps"
                                + " a reserved slot.  Please try again.";
                throw new RequestFailedException(errorMsg);
        } catch (Exception e) {
                e.printStackTrace();
                errorMsg = "Could not make reservation:\n"
                                + e.getMessage();
                throw new RequestFailedException(errorMsg);
        }
    }

    public void createNewReservation(String agent, String member,
        String project, String item, String bdate, int lengthInMinutes) throws RequestFailedException, Exception  {
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
		connector.getReservationManager().deleteReservation(activity_array, connector.getTicketString());
	}

	public void deleteReservation(String agent, String member,
			String project, String item, String bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception {
		Reservation r = generateReservationObject(agent, member, project, item,
				bdate, lengthInMinutes);
		this.deleteReservation(r);
	}
        
	public void deleteReservation(String item, String bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception {
            Date bdateAsDate = stringToDate(bdate);
            this.deleteReservation(item, bdateAsDate, lengthInMinutes);
	}

        public void deleteReservation(String item, Date bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception {
            Date edate = new Date();
            edate.setTime(bdate.getTime() + (lengthInMinutes * 60 * 1000));
            
            Reservations reservations = getReservations(item, bdate, edate);
            if (!reservations.isEmpty()) {
                Iterator<Reservation> i = reservations.iterator();
                Activity[] activity_array = new Activity[reservations.size()];
                int index=0;
                while(i.hasNext()) {
                    Reservation r = i.next();
                    Activity a = ActivityFactory.createReservationActivity(
                            r.getAgent().getName(),
                            r.getMember().getName(),
                            r.getItem(),
                            r.getProject().getName(),
                            r.getAccount().getName(),
                            r.getLab(),
                            bdate,
                            edate);
                    activity_array[index] = a;
                    index++;
                }
                connector.getReservationManager().deleteReservation(activity_array, connector.getTicketString());
            }
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
  public Reservations getReservations(String member, String tool, Date bdate, Date edate) throws Exception {
    
    
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
      activities = this.connector.getReservationManager().findReservation(lowerBound, upperBound);
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
    
    Reservations reservations = new Reservations();
    int i = 0;
    for (Activity act : activities) {
      reservations.add(ActivityFactory.convertActivityToReservation(act));
    }
    
    return reservations;
  }
  
  /**
   * Alias for above method that parses strings as YYYY-mm-dd HH:ii:ss
   * 
   * @param member
   * @param tool
   * @param bdateAsString
   * @param numberOfMinutes
   * @return
   * @throws Exception 
   */
  public Reservations getReservations(String member, String tool, String bdateAsString, int numberOfMinutes) throws Exception {
      Date bdate = TimestampConverter.stringToDate(bdateAsString);
      Date edate = new Date(bdate.getTime() + (numberOfMinutes * 60 * 1000));
      return this.getReservations(member, tool, bdate, edate);
  }

  /**
   * Same as above, but without member filter
   * @param tool
   * @param bdate
   * @param edate
   * @return
   * @throws Exception
   */
  public Reservations getReservations(String tool, Date bdate, Date edate) throws Exception {
	  return getReservations("*", tool, bdate, edate);
  }

  public String enable(Enable enableActivity) throws InvalidTicketSignal, InvalidAgentSignal, InvalidProjectSignal, InvalidAccountSignal, InvalidMemberSignal, InvalidResourceSignal, InvalidProcessSignal, ResourceUnavailableSignal, NotAuthorizedSignal{
    
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
    return connector.getEquipmentManager().enable(activity, false, connector.getTicketString());
  }
  
  public String enable(String agent, String member, String project, String account, String machineName) throws UnknownMemberException, Exception {
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
    
    return this.enable(enableActivity);
  }
  
  public String enable(String agent, String member, String project, String machineName) throws UnknownMemberException, Exception {
    Project p = this.getProject(project);
    Account account = this.getAccount(p.getAccount());
    return this.enable(agent, member, project, account.getName(), machineName);
  }
  
  public String disable (String agent, String machine) throws InvalidTicketSignal, InvalidAgentSignal, InvalidResourceSignal, ResourceUnavailableSignal, NotAuthorizedSignal {
    return connector.getEquipmentManager().disable(agent, machine, true, connector.getTicketString());
  }
  
  /**
   * 
   * @param agent : who is disabling
   * @param machine : what to disable
   * @param rundataId : ID of rundata that has already been entered
   * @return
   * @throws InvalidTicketSignal
   * @throws InvalidAgentSignal
   * @throws InvalidResourceSignal
   * @throws ResourceUnavailableSignal
   * @throws NotAuthorizedSignal 
   */
  public String disableWithRundata(String agent, String machine, String rundataId) throws InvalidTicketSignal, InvalidAgentSignal, InvalidResourceSignal, ResourceUnavailableSignal, NotAuthorizedSignal {
      String activityId = disable(agent, machine);
      RuntimeManager runmgr = this.connector.getRuntimeManager();
    
      if (runmgr != null) {
        runmgr.addActivityToRundata(activityId, rundataId, agent);
        return activityId;
      } else {
        return "not assigned";
      }
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

    this.coralCrypto.initialize();    
    byte[] pass = this.coralCrypto.encrypt(newPassword);
    this.connector.getAuthManager().update(member, pass);
  }
  
  public boolean checkKeyIsValid() {
	  return this.coralCrypto.checkKeyIsValid();
  }
  
  public String getRundataDefinitionForProcess(String process) throws NullReturnException, ServerErrorException {
    RuntimeManager runmgr = this.connector.getRuntimeManager();
    String processDefinition = null;
    if (runmgr != null) {
        processDefinition = runmgr.getAugmentedProcessById(process);
    }
    return processDefinition;
  }
  
  public RunDataProcess[] getRundataProcesses(String tool) throws NullReturnException, ServerErrorException {
    RuntimeManager runmgr = this.connector.getRuntimeManager();
    String[] processNames = {};
    String[] processDescriptions = {};
    String version = "";
    
    if (runmgr != null) {
        processNames = runmgr.getProcessesByCoralToolId(tool);
        processDescriptions = runmgr.getProcessDescriptionsByCoralToolId(tool);
        version = runmgr.getInstanceVersion();
    }
    
    RunDataProcess[] processes = new RunDataProcess[processNames.length];
    for (int i = 0; i < processNames.length; i++) {
        RunDataProcess process = new RunDataProcess();
        process.setName(processNames[i]);
        process.setDescription(processDescriptions[i]);
        process.setVersion(version);
        processes[i] = process;
    }
    return processes;
  }
  
  public RunDataProcess[] getRundataProcessesWithDefinitions(String tool) throws NullReturnException, ServerErrorException {
    RunDataProcess[] processes = getRundataProcesses(tool);      
    for (int i = 0; i < processes.length; i++) {
        String xmlDefinition = getRundataDefinitionForProcess(processes[i].getName());
        processes[i].setXmlDefinition(xmlDefinition);
    }
    return processes;
  }
  
  public String createRunData(String rundata) throws NullReturnException, ServerErrorException {
    RuntimeManager runmgr = this.connector.getRuntimeManager();
    
    if (runmgr != null) {
        return runmgr.createRunData(rundata);
    }
    return "not assigned";
  }
  
  public void updateRunData(String rundata) throws NullReturnException, ServerErrorException {
    RuntimeManager runmgr = this.connector.getRuntimeManager();
    
    if (runmgr != null) {
        runmgr.updateRunData(rundata);
    }
  }
  
  public void commitRunData(String id) throws NullReturnException, ServerErrorException {
    RuntimeManager runmgr = this.connector.getRuntimeManager();
    
    if (runmgr != null) {
        runmgr.commitRunData(id);
    }
  }
  
  public String createAndCommitRunData(String xmlDefinition) throws NullReturnException, ServerErrorException, Exception {
      //CREATE
      String id = this.createRunData(xmlDefinition);
      
      //XMLType xmlType = new XMLType(xmlDefinition);
      //RundataAdapter ra = new RundataAdapter(xmlType);
      RmRunData rundata = (RmRunData) (RmUtil.xmlStringToObject(xmlDefinition));
      rundata.setId(id);
      rundata.setActive(true);

      //UPDATE
      String xmlWithID = RmUtil.objectToXMLString(rundata);
      this.updateRunData(xmlWithID);
      
      //COMMIT
      this.commitRunData(id);
      return id;
  }

  
  public RundataAdapter rundataFromXml(String xml) {
      RundataAdapter ra = new RundataAdapter(new XMLType(xml));
      return ra;
  }
  
  public void createAdjustmentRunData() {
        //JUST a placeholder for: runmgr.createAdjustmentRunData(...);
  }
  
  public void addActivityToRundata(String arg1, String arg2) {
      //placeholder
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


        /**
	 * Gets all the projects for all members.
	 * 
	 * @param activeOnly only return active members.
	 * @return A map of members to projects
	 */
	private HashMap<String, ArrayList<String>> convertMemberProjectsRelationToMap(Relation[] relations) {
            ArrayList<String> projectsList = new ArrayList<String>();
            HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
                
            for(Relation r : relations) {
                String memberName = r.master;
                String projectName = r.slave;
                if (map.containsKey(memberName)) {
                    projectsList = map.get(memberName);
                } else {
                    projectsList = new ArrayList<String>();
                }
                
                if (!projectsList.contains(projectName)) {
                    projectsList.add(projectName);
                }
                
                map.put(memberName, projectsList);
                
            }
            return map;
	}  

    private Relation[] filterRelationsToOnlyActiveMembers(Relation[] memberProjects) {
        ArrayList<Relation> returnSet = new ArrayList<Relation>();
        Members activeMembers;
        
        try {
            activeMembers = this.getAllActiveMembers();
        } catch (Exception ex) {
            return memberProjects;
        }
        
        for (Relation r : memberProjects) {
            if ( activeMembers.containsName(r.master) ) {
                returnSet.add(r);
            }
        }
        return returnSet.toArray(new Relation[0]);
    }
}
