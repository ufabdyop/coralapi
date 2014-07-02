
package edu.utah.nanofab.coralapi;
import java.io.IOException;
import java.util.Date;

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
import org.opencoral.idl.Auth.AuthManager;
import org.opencoral.idl.Auth.AuthManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManager;
import org.opencoral.idl.Equipment.EquipmentManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManagerPackage.MachineRetrievalFailedSignal;
import org.opencoral.idl.Reservation.ReservationManager;
import org.opencoral.idl.Reservation.ReservationManagerHelper;
import org.opencoral.idl.Resource.ResourceManager;
import org.opencoral.idl.Resource.ResourceManagerHelper;
import org.opencoral.util.ResourceRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.exceptions.NotImplementedException;
import edu.utah.nanofab.coralapi.exceptions.UnknownMemberException;
import edu.utah.nanofab.coralapi.collections.Accounts;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
import edu.utah.nanofab.coralapi.resource.Enable;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.helper.CoralManagerConnector;

/**
 * The CoralAPI class provides a wrapper for the primary coral services.
 * 
 * @author University of Utah Nanofab
 * @contact nanofab-support@eng.utah.edu
 */
public class CoralAPI {
    
	private String coralUser = "coral";
    private String iorUrl = "http://vagrant-coral-dev/IOR/";
    private String configUrl = "";
    private String ticketString = "";
	private AuthManager authManager;
	public static Logger logger ;
    private CoralManagerConnector connector = null;
    private ResourceManager resourceManager = null;
    private EquipmentManager equipmentManager = null;
    private ReservationManager reservationManager = null;
	private CoralCrypto coralCrypto;
           
    public CoralAPI(String coralUser, String iorUrl, String configUrl) {
    	this.coralUser = coralUser;
    	this.iorUrl = iorUrl;
    	this.configUrl = configUrl;
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        logger = LoggerFactory.getLogger(CoralAPI.class);
        this.coralCrypto = new CoralCrypto(this.configUrl);
    }
    
    private void reconnectToCoral() {
		logger.debug("reconnectToCoral called");
		connector = new CoralManagerConnector();
		connector.setCoralUser(this.coralUser);
		connector.setIorUrl(this.iorUrl);
    }
    
    private void reconnectToResourceManager() {
		logger.debug("reconnectToResourceManager called");
		resourceManager = ResourceManagerHelper.narrow(connector.getManager(Constants.RSCMGR_NAME));
    }
    
    private void getReservationManager() {
		logger.debug("getReservationManager called");
        if (connector == null) {
            System.out.println("connector is null");
            reconnectToCoral();
	    }    	
		if (reservationManager == null){
			reservationManager = ReservationManagerHelper.narrow(connector.getManager(Constants.RESMGR_NAME));
			System.out.println("resmgr is null? " + (reservationManager == null));
		}
		this.ticketString = connector.getTicketString();
	}
    
    private void getEquipmentManager(){
    	logger.debug("Entered getEquipmentManager()");

        if (connector == null) {
                System.out.println("connector is null");
                reconnectToCoral();
        }    	
    	if (equipmentManager == null){
    		equipmentManager = EquipmentManagerHelper.narrow(connector.getManager(Constants.EQUMGR_NAME));
    	}
    	this.ticketString = connector.getTicketString();
    }
    
    private void getAuthManager(){
        System.out.println("Entered getAuthManager()");
        if (connector == null) {
                System.out.println("connector is null");
                reconnectToCoral();
        }    	
    	if (authManager == null){
    		authManager = AuthManagerHelper.narrow(connector.getManager(Constants.ATHMGR_NAME));
    	}
    	this.ticketString = connector.getTicketString();
    }
    
    private void getResourceManager() {
            System.out.println("Entered getResourceManager()");
            if (connector == null) {
                    System.out.println("connector is null");
                    reconnectToCoral();
            }
            if (resourceManager == null) {
                    System.out.println("resourceManager is null");
                    reconnectToResourceManager();
            }

            this.ticketString = connector.getTicketString();
            System.out.println("this.ticketString = " + this.ticketString );
            try {
                    resourceManager.getAllProjects();
            } catch (org.omg.CORBA.COMM_FAILURE comm) {
                    System.out.println("orb communication failure ");
                    connector = null;
                    resourceManager = null;
            } catch (org.omg.CORBA.OBJECT_NOT_EXIST corbaException ) {
                    System.out.println("Caught Corba error, probably lost coral connection, trying to reconnect");
                    reconnectToCoral() ;
                    reconnectToResourceManager() ;
            } catch (ProjectNotFoundSignal e) {
                    System.out.println("project not found signal!");
            } catch (Exception e) {
                    System.out.println("General exception found" + e.getMessage());
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
    public void createNewMember(Member member) throws Exception {
            this.getResourceManager();

            //create a new member should be active ??
            member.setActive(true);
            resourceManager.addMember(member.convertToIDLMemberForRscMgr(), this.ticketString);
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
    	 * 		addMemberToProject
    	 */
    }
    
    public void removeMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 this.getResourceManager();
		 for (String project: projects ) {
			 resourceManager.removeMemberFromProject(member, project, this.ticketString);
		 }    	
    }
	public void addEquipmentRoleToMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
   
		try {
			resourceManager.addRoleToMember(member, roleName, resource, ResourceRoles.EQUIPMENT, this.ticketString);
		} catch (InvalidRoleSignal irs ) {
			throw new InvalidRoleSignal("IOR: " + this.iorUrl + " : " + irs.getMessage());
			
		}
	}
	public void removeEquipmentRoleFromMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
        resourceManager.removeRoleFromMember(member, roleName, resource, ResourceRoles.EQUIPMENT, this.ticketString);
	}	
	public void addProjectRoleToMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
		resourceManager.addRoleToMember(member, roleName, resource, ResourceRoles.PROJECT, this.ticketString);
	}

	public void removeProjectRoleFromMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
		resourceManager.removeRoleFromMember(member, roleName, resource, ResourceRoles.PROJECT, this.ticketString);
	}
	
	public void addSafetyFlagToMember(String member ) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.addEquipmentRoleToMember(member, "safety", "Door Access");
	}
	
	public void removeSafetyFlagFromMember(String member) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
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
         * Otherwise it will return false. Note, this function will return false
         * if null parameters are supplied.
         * 
         * @param username - the username of the coral member
         * @param password - the password for the coral username
         * @return - True if the coral account exists. False otherwise.
         */
	public boolean authenticate(String username, String password) {
              
                if (username == null || password == null)
                    return false;
		
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
		System.out.println("Close CoralServices Resources");
		if (resourceManager != null) { resourceManager._release(); }
		if (authManager != null) { authManager._release(); }
		if (equipmentManager != null) { equipmentManager._release(); }
		try {
			connector.release();
		} catch (Exception e) {
			logger.error("could not call release on connector: " + e.getMessage());
		}
	}

	public void addLabRole(LabRole newRole) throws InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
		resourceManager.addRoleToMember(
				newRole.getMember(), 
				newRole.getRole(),
				newRole.getLab(),
				"lab",
				this.ticketString);
	}

	public LabRoles getLabRoles(String username) throws RoleNotFoundSignal {
		this.getResourceManager();
		Persona[] personas = resourceManager.getPersonas(username, "*", "*", ResourceRoles.LAB, true);
		return LabRoles.fromIdlPersonaArray(personas);
	}

    public void createNewReservation(Reservation r) throws Exception {
        this.getReservationManager();
        Activity a = ActivityFactory.createRunActivity(
        		r.getMember().getName(), 
        		r.getItem(), 
    			r.getProject().getName(), 
    			r.getAccount().getName(),
    			r.getLab(),
    			r.getBdate(),
    			r.getEdate());
		Activity[] activity_array = {a};
		logger.debug("Making reservation for " + r.getMember().getName() + " " + r.getItem() );
		reservationManager.makeReservation(activity_array, this.ticketString);
}

	public void enable(Enable enableActivity) throws InvalidTicketSignal, InvalidAgentSignal, InvalidProjectSignal, InvalidAccountSignal, InvalidMemberSignal, InvalidResourceSignal, InvalidProcessSignal, ResourceUnavailableSignal, NotAuthorizedSignal{
		this.getEquipmentManager();
		Activity activity = ActivityFactory.createRunActivity(
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
	
//	qualify(tool, member, role)
//	disqualify(tool, member, role)
//	reserve( tool, agent, member, project, account, begin time, end time(or length) ) 
//	deleteReservation( tool, member, time, length )
//	costRecovery (month, year)          

	public Reservation getReservation(String string, String string2,
			String string3) throws NotImplementedException {
		this.getReservationManager();
		//reservationManager.findReservation(arg0, arg1);
		throw new NotImplementedException();
	}
}
