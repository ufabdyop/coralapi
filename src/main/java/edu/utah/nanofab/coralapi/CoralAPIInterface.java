/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package edu.utah.nanofab.coralapi;

import edu.utah.nanofab.coralapi.collections.EquipmentRoles;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
import edu.utah.nanofab.coralapi.collections.Reservations;
import edu.utah.nanofab.coralapi.exceptions.InvalidCallOrderException;
import edu.utah.nanofab.coralapi.exceptions.InvalidRoleException;
import edu.utah.nanofab.coralapi.exceptions.RequestFailedException;
import edu.utah.nanofab.coralapi.exceptions.UnknownMemberException;
import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.resource.Enable;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.ProjectRole;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.coralapi.resource.RunDataProcess;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.opencoral.corba.RundataAdapter;
import org.opencoral.idl.AccountNotFoundSignal;
import org.opencoral.idl.Equipment.EquipmentManagerPackage.MachineRetrievalFailedSignal;
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
import org.opencoral.idl.Reservation.ReservationManagerPackage.ReservationNotFoundSignal;
import org.opencoral.idl.ResourceUnavailableSignal;
import org.opencoral.idl.RoleNotFoundSignal;
import org.opencoral.idl.Runtime.NullReturnException;
import org.opencoral.idl.Runtime.ServerErrorException;

/**
*
* @author ryant
*/
public interface CoralAPIInterface {
	public Projects getProjects() throws ProjectNotFoundSignal;
	public Projects getAllProjects() throws ProjectNotFoundSignal;
	public Project getProject(String name) throws ProjectNotFoundSignal ;
	public void createNewMember(Member member) throws MemberDuplicateSignal, InvalidProjectSignal, Exception ;
	public void createNewProject(Project project) throws Exception ;
	public void createNewProjectUnlessExists(Project project) throws Exception ;
	public void deleteMemberFromProject(String memberName, String projectName) throws InvalidTicketSignal, MemberDuplicateSignal, InvalidProjectSignal, NotAuthorizedSignal, InvalidMemberSignal ;
	public void addProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal;
	public void removeProjectMembers(String project, String[] members) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal;
	public Member getMember(String member) throws UnknownMemberException, Exception ;
	public Members getAllMembers() throws UnknownMemberException, Exception ;
	public Members getAllActiveMembers() throws UnknownMemberException, Exception ;
	public Member getQualifications(String member) throws UnknownMemberException, Exception ;
	public Projects getMemberProjects(String member) ;
	public HashMap<String, ArrayList<String>> getAllMemberProjects(boolean activeOnly) ;
	public void addMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal;
	public void addMemberProjectCollection(String member, Projects projects) ;
	public void removeMemberProjects(String member, String[] projects) throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal;
	public void addEquipmentRoleToMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal ;
	public void removeEquipmentRoleFromMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal     ;
	public void removeLabRoleFromMember(String member, String roleName, String lab) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal ;
	public void addProjectRoleToMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal ;
	public void removeGenericRoleFromMember(String member, String roleName, String resource, String roleType) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal ;
	public void addSafetyFlagToMember(String member ) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal ;
	public void removeSafetyFlagFromMember(String member) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal ;
	public Members getProjectMembers(String projectName) ;
	public void createNewAccount(Account acct) throws Exception ;
	public void createNewAccountUnlessExists(Account acct) throws Exception ;
	public edu.utah.nanofab.coralapi.resource.Machine getMachine(String name) throws MachineRetrievalFailedSignal  ;
	public edu.utah.nanofab.coralapi.collections.Machines getAllMachines()  ;
	public edu.utah.nanofab.coralapi.resource.Account getAccount(String name) throws InvalidAccountSignal ;
	public edu.utah.nanofab.coralapi.collections.Accounts getAccounts() throws AccountNotFoundSignal ;
	public edu.utah.nanofab.coralapi.collections.Accounts getAllAccounts() throws AccountNotFoundSignal ;
	public void deleteProject(String projectName) throws InvalidTicketSignal, NotAuthorizedSignal, Exception ;
	public boolean authenticate(String username, String password) ;
	public void activateMember(String memberName) throws UnknownMemberException, Exception ;
        public void activateMemberWithProject(String memberName, String projectName) throws InvalidTicketSignal, ProjectNotFoundSignal, MemberNotFoundSignal, InvalidProjectSignal, NotAuthorizedSignal ;        
	public void deactivateMember(String memberName) throws InvalidTicketSignal, MemberNotFoundSignal, NotAuthorizedSignal ;
	public void activateProject(String projectName) throws InvalidTicketSignal, ProjectNotFoundSignal, InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal, Exception ;
	public void deactivateProject(String projectName) throws InvalidTicketSignal, ProjectNotFoundSignal, InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal, Exception ;
	public void updateProject(Project project) throws InvalidTicketSignal, ProjectNotFoundSignal, InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal, Exception ;
	public void updateAccount(Account account) throws InvalidTicketSignal, AccountNotFoundSignal, NotAuthorizedSignal, Exception ;
	public void updateMember(Member member) throws InvalidTicketSignal, MemberNotFoundSignal, InvalidProjectSignal, NotAuthorizedSignal, Exception ;
	public void close() ;
	public void addLabRoleToMember(LabRole newRole) throws Exception ;
	public void addProjectRoleToMember(ProjectRole newRole) throws Exception ;
	public LabRoles getLabRoles(String username) throws RoleNotFoundSignal ;
	public EquipmentRoles getEquipmentRoles(String username) throws RoleNotFoundSignal ;
	public void createNewRole(String name, String description, String type) throws Exception ;
	public edu.utah.nanofab.coralapi.resource.Role getRole(String name, String type) throws InvalidRoleException ;
	public void createNewReservation(Reservation r) throws RequestFailedException  ;
	public void createNewReservation(String agent, String member, String project, String item, String bdate, int lengthInMinutes) throws RequestFailedException, Exception  ;
	public void deleteReservation(Reservation r) throws InvalidTicketSignal, NotAuthorizedSignal, ReservationNotFoundSignal ;
	public void deleteReservation(String agent, String member, String project, String item, String bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception ;
	public void deleteReservation(String item, String bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception ;
	public void deleteReservation(String item, Date bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception ;
	public Reservations getReservations(String member, String tool, Date bdate, Date edate) throws Exception ;
	public Reservations getReservations(String member, String tool, String bdateAsString, int numberOfMinutes) throws Exception ;
	public Reservations getReservations(String tool, Date bdate, Date edate) throws Exception ;
	public String enable(Enable enableActivity) throws InvalidTicketSignal, InvalidAgentSignal, InvalidProjectSignal, InvalidAccountSignal, InvalidMemberSignal, InvalidResourceSignal, InvalidProcessSignal, ResourceUnavailableSignal, NotAuthorizedSignal;
	public String enable(String agent, String member, String project, String account, String machineName) throws UnknownMemberException, Exception ;
	public String enable(String agent, String member, String project, String machineName) throws UnknownMemberException, Exception ;
	public String disable (String agent, String machine) throws InvalidTicketSignal, InvalidAgentSignal, InvalidResourceSignal, ResourceUnavailableSignal, NotAuthorizedSignal ;
	public String disableWithRundata(String agent, String machine, String rundataId) throws InvalidTicketSignal, InvalidAgentSignal, InvalidResourceSignal, ResourceUnavailableSignal, NotAuthorizedSignal ;
	public void updatePassword(String member, String newPassword)      throws InvalidMemberSignal, NotAuthorizedSignal, InvalidTicketSignal ;
	public boolean checkKeyIsValid() ;
	public String getRundataDefinitionForProcess(String process) throws NullReturnException, ServerErrorException ;
	public RunDataProcess[] getRundataProcesses(String tool) throws NullReturnException, ServerErrorException ;
	public RunDataProcess[] getRundataProcessesWithDefinitions(String tool) throws NullReturnException, ServerErrorException ;
	public String createRunData(String rundata) throws NullReturnException, ServerErrorException ;
	public void updateRunData(String rundata) throws NullReturnException, ServerErrorException ;
	public void commitRunData(String id) throws NullReturnException, ServerErrorException ;
	public String createAndCommitRunData(String xmlDefinition) throws NullReturnException, ServerErrorException, Exception ;
	public RundataAdapter rundataFromXml(String xml) ;
	public void createAdjustmentRunData() ;
	public void addActivityToRundata(String arg1, String arg2) ;
	public String getLogLevel() ;
	public void setLogLevel(String logLevel) ;
}
