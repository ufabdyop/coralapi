/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nanofab.coralapi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.opencoral.constants.Constants;
import org.opencoral.corba.AccountAdapter;
import org.opencoral.corba.MemberAdapter;
import org.opencoral.corba.ProjectAdapter;
import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidAgentSignal;
import org.opencoral.idl.InvalidMemberSignal;
import org.opencoral.idl.InvalidProcessSignal;
import org.opencoral.idl.InvalidProjectSignal;
import org.opencoral.idl.InvalidResourceSignal;
import org.opencoral.idl.InvalidRoleSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.MemberDuplicateSignal;
import org.opencoral.idl.MemberNotFoundSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.Project;
import org.opencoral.idl.ProjectNotFoundSignal;
import org.opencoral.idl.Relation;
import org.opencoral.idl.ResourceUnavailableSignal;
import org.opencoral.idl.Equipment.EquipmentManager;
import org.opencoral.idl.Equipment.EquipmentManagerHelper;
import org.opencoral.idl.Resource.ResourceManager;
import org.opencoral.idl.Resource.ResourceManagerHelper;
import org.opencoral.util.ResourceRoles;
import org.opencoral.util.Tstamp;

import edu.nanofab.coralapi.collections.Members;
import edu.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.CoralManagerConnector;

/**
 *
 * @author neil
 */
public class CoralServices {
    private String coralUser="coral";
    private String iorUrl="http://vagrant-coral-dev/IOR/";
    private String ticketString = "";
	public static Logger logger = Logger.getLogger(CoralServices.class.getName()) ;
    public static CoralManagerConnector connector = null;
    public static ResourceManager resourceManager = null;
    public static EquipmentManager equipmentManager = null;
           
    public CoralServices() {
        BasicConfigurator.configure();
    }
    private void reconnectToCoral() {
            connector = new CoralManagerConnector();
            connector.setCoralUser(this.coralUser);
            connector.setIorUrl(this.iorUrl);
    }
    private void reconnectToResourceManager() {
            resourceManager = ResourceManagerHelper.narrow(connector.getManager(Constants.RSCMGR_NAME));
    }
    private EquipmentManager getEquipmentManager(){
        System.out.println("Entered getEquipmentManager()");
        if (connector == null) {
                System.out.println("connector is null");
                reconnectToCoral();
        }    	
    	if (equipmentManager == null){
    		equipmentManager = EquipmentManagerHelper.narrow(connector.getManager(Constants.EQUMGR_NAME));
    	}
    	this.ticketString = connector.getTicketString();
    	return equipmentManager;
    }
    private ResourceManager getResourceManager() {
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
            Project[] projects; 

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

            return resourceManager;
    }
    private ProjectAdapter projectToProjectAdapter(Project project) throws Exception{
    	ProjectAdapter proAdapter = new ProjectAdapter();
    	proAdapter.setValue("account", project.account);
    	if (project.description != null) proAdapter.setValue("description", project.description);
    	if (project.discipline != null) proAdapter.setValue("discipline", project.discipline);
    	if (project.name != null) proAdapter.setValue("name", project.name);
    	if (project.nickname != null) proAdapter.setValue("nickname", project.nickname);
    	if (project.pi != null) proAdapter.setValue("pi", project.pi);
    	if (project.type != null) proAdapter.setValue("type", project.type);
    	proAdapter.setValue("active", (project.active)?"true":"false");
    	return proAdapter;
    }
    private AccountAdapter accountToAccountAdapter(org.opencoral.idl.Account account) throws Exception {
    	AccountAdapter adapter = new AccountAdapter();

		if (account.edate != null) adapter.setEdate(new Tstamp(account.edate));
		if (account.bdate != null) adapter.setBdate(new Tstamp(account.bdate));
		if (account.type != null) adapter.setValue("type", account.type);
		if (account.organization != null) adapter.setValue("organization", account.organization);
		if (account.description != null) adapter.setValue("description", account.description);
		if (account.name != null) adapter.setValue("name", account.name);
		adapter.setValue("active", (account.active ? "true" : "false"));

    	return adapter;
    }
    public Project[] getProjects() throws ProjectNotFoundSignal{
    	ResourceManager rscmgr = this.getResourceManager();
		return rscmgr.getAllProjects();
    }
    public Project getProject(String name) throws ProjectNotFoundSignal {
    	ResourceManager rscmgr = this.getResourceManager();
		try {
			return rscmgr.getProject(name);
		} catch (InvalidProjectSignal e) {
			ProjectNotFoundSignal notfound = new ProjectNotFoundSignal(name);
			throw notfound;
		} 
    }
    public void CreateNewMember(Member member) throws Exception {
            ResourceManager rscmgr = this.getResourceManager();

            //create a new member should be active ??
            member.setActive(true);
            rscmgr.addMember(member.convertToIDLMemberForRscMgr(), this.ticketString);
    }

    public void CreateNewProject(Project project) throws Exception {
            ResourceManager rscmgr = this.getResourceManager();
            ProjectAdapter proAdapter = this.projectToProjectAdapter(project);
            proAdapter.setValue("active","true");
            rscmgr.addProject((Project)proAdapter.getObject(), this.ticketString);
    }
 
    public void DeleteMemberFromProject(String memberName, String projectName) throws InvalidTicketSignal, MemberDuplicateSignal, InvalidProjectSignal, NotAuthorizedSignal, InvalidMemberSignal {
        ResourceManager rscmgr = this.getResourceManager();
        rscmgr.removeMemberFromProject(memberName, projectName, this.ticketString);
    }
    public void AddProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 ResourceManager rscmgr = this.getResourceManager();
		 for (String member: members ) {
			 rscmgr.addMemberToProject(member, project, this.ticketString);
		 }
    }
    public void RemoveProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 ResourceManager rscmgr = this.getResourceManager();
		 for (String member: members ) {
			 rscmgr.removeMemberFromProject(member, project, this.ticketString);
		 }    	
    }
    public Member getMember(String member) throws Exception{
    	ResourceManager rscmgr = this.getResourceManager();
    	Member mem = new Member(rscmgr.getMember(member));
    	return mem;
    }
    
    public void AddMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 ResourceManager rscmgr = this.getResourceManager();
		 for (String project: projects ) {
			 rscmgr.addMemberToProject(member, project, this.ticketString);
		 }    	
    }
    public void RemoveMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal{
		 ResourceManager rscmgr = this.getResourceManager();
		 for (String project: projects ) {
			 rscmgr.removeMemberFromProject(member, project, this.ticketString);
		 }    	
    }
	public void AddEquipmentRoleToMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		ResourceManager rscmgr = this.getResourceManager();
   
		System.out.println("AddEquipmentRoleToMember called: Is ticket null?" + (this.ticketString == null));
		System.out.println("Member: " + member);
		System.out.println("roleName: " + roleName);
		System.out.println("resource: " + resource);
		System.out.println("IOR: " + this.iorUrl );
		System.out.println("coralUser: " + this.coralUser );
		try {
			rscmgr.addRoleToMember(member, roleName, resource, ResourceRoles.EQUIPMENT, this.ticketString);
		} catch (InvalidRoleSignal irs ) {
			throw new InvalidRoleSignal("IOR: " + this.iorUrl + " : " + irs.getMessage());
			
		}
	}
	public void RemoveEquipmentRoleFromMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		ResourceManager rscmgr = this.getResourceManager();
        System.out.println("RemoveEquipmentRoleFromMember: Is ticket null?" + (this.ticketString == null));
        rscmgr.removeRoleFromMember(member, roleName, resource, ResourceRoles.EQUIPMENT, this.ticketString);
	}	
	public void AddProjectRoleToMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		ResourceManager rscmgr = this.getResourceManager();
		System.out.println("Adding project role for member:" + member + " role: " + roleName + " project:" + resource);
		rscmgr.addRoleToMember(member, roleName, resource, ResourceRoles.PROJECT, this.ticketString);
	}

	public void RemoveProjectRoleFromMember(String member, String roleName,
			String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		ResourceManager rscmgr = this.getResourceManager();
		System.out.println("Removing project role for member:" + member + " role: " + roleName + " project:" + resource);
		rscmgr.removeRoleFromMember(member, roleName, resource, ResourceRoles.PROJECT, this.ticketString);
	}
	public void AddSafetyFlagToMember(String member ) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.AddEquipmentRoleToMember(member, "safety", "Door Access");
	}//need to write test for this
	
	public void RemoveSafetyFlagFromMember(String member) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal {
		this.RemoveEquipmentRoleFromMember(member, "safety", "Door Access");
	}//need to write test for this
	
	public void enable(String item){
		EquipmentManager equipmentManager = this.getEquipmentManager();
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
    	ResourceManager rscmgr = this.getResourceManager();
		Relation[] relations;
		try {
			logger.debug("Calling getMemberInfoForProject");
			relations = rscmgr.getMemberInfoForProject(projectName, true);
			for (Relation relation : relations ) {
				String memberName = relation.master;
				try {
					logger.debug("Adding " + memberName + " to resultset");
					Member temp = new Member(rscmgr.getMember(memberName));
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
	public void CreateNewAccount(org.opencoral.idl.Account account) throws Exception {
		ResourceManager rscmgr = this.getResourceManager();
		AccountAdapter adapter = this.accountToAccountAdapter(account);
		rscmgr.addAccount((org.opencoral.idl.Account)adapter.getObject(), this.ticketString);
	}

	public org.opencoral.idl.Account getAccount(String name) throws InvalidAccountSignal {
		ResourceManager rscmgr = this.getResourceManager();
		return rscmgr.getAccount(name);
	}
	

}
