/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nanofab.coralapi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencoral.constants.Constants;
import org.opencoral.corba.AccountAdapter;
import org.opencoral.corba.MemberAdapter;
import org.opencoral.corba.ProjectAdapter;
import org.opencoral.gui.LabFrame;
import org.opencoral.idl.AccountNotFoundSignal;
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
import org.opencoral.idl.ProjectNotFoundSignal;
import org.opencoral.idl.Relation;
import org.opencoral.idl.ResourceUnavailableSignal;
import org.opencoral.idl.Auth.AuthManager;
import org.opencoral.idl.Auth.AuthManagerHelper;
import org.opencoral.idl.Equipment.EquipmentManager;
import org.opencoral.idl.Equipment.EquipmentManagerHelper;
import org.opencoral.idl.Resource.ResourceManager;
import org.opencoral.idl.Resource.ResourceManagerHelper;
import org.opencoral.util.Encryption;
import org.opencoral.util.ResourceRoles;
import org.opencoral.util.Tstamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.nanofab.coralapi.collections.Accounts;
import edu.nanofab.coralapi.collections.Members;
import edu.nanofab.coralapi.collections.MembersProxySet;
import edu.nanofab.coralapi.collections.Projects;
import edu.nanofab.coralapi.resource.Account;
import edu.nanofab.coralapi.resource.Member;
import edu.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.CoralManagerConnector;

/**
 *
 * @author neil
 */
public class CoralServices {
    private String coralUser="coral";
    private String iorUrl="http://vagrant-coral-dev/IOR/";
    private String configUrl = "";
    private String ticketString = "";
	private AuthManager authManager;
	public static Logger logger ;
    private CoralManagerConnector connector = null;
    private ResourceManager resourceManager = null;
    private EquipmentManager equipmentManager = null;
	private CoralCrypto coralCrypto;
           
    public CoralServices(String coralUser, String iorUrl, String configUrl) {
    	this.coralUser = coralUser;
    	this.iorUrl = iorUrl;
    	this.configUrl = configUrl;
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        logger = LoggerFactory.getLogger(CoralServices.class);
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
            org.opencoral.idl.Project[] projects; 

            try {
                    projects = resourceManager.getAllProjects();
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
    public void CreateNewMember(Member member) throws Exception {
            this.getResourceManager();

            //create a new member should be active ??
            member.setActive(true);
            resourceManager.addMember(member.convertToIDLMemberForRscMgr(), this.ticketString);
    }

    public void CreateNewProject(Project project) throws Exception {
            this.getResourceManager();
            project.setActive(true);
            resourceManager.addProject(project.convertToIdlProjectForRscMgr(), this.ticketString);
    }
    
    public void CreateNewProjectUnlessExists(Project project) throws Exception {
		try {
			this.getProject(project.getName());
		} catch (ProjectNotFoundSignal e) {
			this.CreateNewProject(project);
		}
    }
 
    public void DeleteMemberFromProject(String memberName, String projectName) throws InvalidTicketSignal, MemberDuplicateSignal, InvalidProjectSignal, NotAuthorizedSignal, InvalidMemberSignal {
        this.getResourceManager();
        resourceManager.removeMemberFromProject(memberName, projectName, this.ticketString);
    }
    public void AddProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 this.getResourceManager();
		 for (String member: members ) {
			 resourceManager.addMemberToProject(member, project, this.ticketString);
		 }
    }
    public void RemoveProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 this.getResourceManager();
		 for (String member: members ) {
			 resourceManager.removeMemberFromProject(member, project, this.ticketString);
		 }    	
    }
    public Member getMember(String member) throws Exception{
    	this.getResourceManager();
    	Member mem = new Member(resourceManager.getMember(member));
    	return mem;
    }
    
    public void AddMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 this.getResourceManager();
		 for (String project: projects ) {
			 resourceManager.addMemberToProject(member, project, this.ticketString);
		 }    	
    }
    public void RemoveMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 this.getResourceManager();
		 for (String project: projects ) {
			 resourceManager.removeMemberFromProject(member, project, this.ticketString);
		 }    	
    }
	public void AddEquipmentRoleToMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
   
		System.out.println("AddEquipmentRoleToMember called: Is ticket null?" + (this.ticketString == null));
		System.out.println("Member: " + member);
		System.out.println("roleName: " + roleName);
		System.out.println("resource: " + resource);
		System.out.println("IOR: " + this.iorUrl );
		System.out.println("coralUser: " + this.coralUser );
		try {
			resourceManager.addRoleToMember(member, roleName, resource, ResourceRoles.EQUIPMENT, this.ticketString);
		} catch (InvalidRoleSignal irs ) {
			throw new InvalidRoleSignal("IOR: " + this.iorUrl + " : " + irs.getMessage());
			
		}
	}
	public void RemoveEquipmentRoleFromMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
        System.out.println("RemoveEquipmentRoleFromMember: Is ticket null?" + (this.ticketString == null));
        resourceManager.removeRoleFromMember(member, roleName, resource, ResourceRoles.EQUIPMENT, this.ticketString);
	}	
	public void AddProjectRoleToMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
		System.out.println("Adding project role for member:" + member + " role: " + roleName + " project:" + resource);
		resourceManager.addRoleToMember(member, roleName, resource, ResourceRoles.PROJECT, this.ticketString);
	}

	public void RemoveProjectRoleFromMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.getResourceManager();
		System.out.println("Removing project role for member:" + member + " role: " + roleName + " project:" + resource);
		resourceManager.removeRoleFromMember(member, roleName, resource, ResourceRoles.PROJECT, this.ticketString);
	}
	public void AddSafetyFlagToMember(String member ) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.AddEquipmentRoleToMember(member, "safety", "Door Access");
	}//need to write test for this
	
	public void RemoveSafetyFlagFromMember(String member) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.RemoveEquipmentRoleFromMember(member, "safety", "Door Access");
	}//need to write test for this
	
	public void enable(String item){
		this.getEquipmentManager();
		ActivityFactory fac = new ActivityFactory();
		org.opencoral.idl.Activity activity = fac.createDefaultActivity(item);
		try{
			equipmentManager.enable(activity, false, this.ticketString);
		}
		catch (InvalidTicketSignal e){
		
		}
		catch (InvalidAgentSignal e1){
			
		}
		catch (InvalidProjectSignal e2){
			
		}
		catch (InvalidAccountSignal e3){
			
		}
		catch (InvalidMemberSignal e4){
			
		}
		catch (InvalidResourceSignal e5){
			
		}
		catch (InvalidProcessSignal e6){
			
		}
		catch (ResourceUnavailableSignal e7){
			
		}
		catch (NotAuthorizedSignal e8){
			
		}
	}
//	disable(tool)
//	qualify(tool, member, role)
//	disqualify(tool, member, role)
//	reserve( tool, agent, member, project, account, begin time, end time(or length) ) 
//	deleteReservation( tool, member, time, length )
//	costRecovery (month, year)          
	public Members GetProjectMembers(String projectName) {
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
	public void CreateNewAccount(Account acct) throws Exception {
		this.getResourceManager();
		resourceManager.addAccount(acct.convertToIdlAccountForRscMgr(), this.ticketString);
	}
	public void CreateNewAccountUnlessExists(Account acct) throws Exception {
		try {
			this.getAccount(acct.getName());
		} catch (InvalidAccountSignal e) {
			this.CreateNewAccount(acct);
		}
	}
	
	public edu.nanofab.coralapi.resource.Account getAccount(String name) throws InvalidAccountSignal {
		this.getResourceManager();
		org.opencoral.idl.Account idlAccount = resourceManager.getAccount(name);
		logger.debug("Account fetched: " + idlAccount.name + " with edate fields (year, month, day, hour, isnull: " + idlAccount.edate.year + "," + idlAccount.edate.month + "," + idlAccount.edate.day +"," + idlAccount.edate.hour + "," + idlAccount.edate.isNull);
		Account acct = new Account();
		acct.populateFromIdlAccount(idlAccount);
		return acct;
	}
	
	public edu.nanofab.coralapi.collections.Accounts getAccounts() throws AccountNotFoundSignal {
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
	public boolean authenticate(String username, String password) {
		boolean result = false;
		this.getAuthManager();
		byte[] u = this.coralCrypto.encrypt(username);
		byte[] p = this.coralCrypto.encrypt(password);
		
		try {
			String ticket = authManager.authenticateByUserNamePassword(u, p);
			result = true;
		} catch (InvalidMemberSignal e) {
			logger.debug("authenticate: Invalid member " + username);
			e.printStackTrace();
		} catch (NotAuthorizedSignal e) {
			logger.debug("authenticate: Not authorized " + username);
			e.printStackTrace();
		} catch (InvalidTicketSignal e) {
			logger.debug("authenticate: Invalid ticket " + username);
			e.printStackTrace();
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
}
