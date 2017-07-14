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
import edu.utah.nanofab.coralapi.resource.Role;

/**
 * Decorator of the CoralAPI class.  Each method is synchronized
 * 
 * Automatically generated using:
 * mvn exec:java -Dexec.mainClass=edu.utah.nanofab.coralapi.codegeneration.CreateSynchronizedDecorator \
 *     -Dexec.args="src/main/java/edu/utah/nanofab/coralapi/CoralAPI.java" \
 *    | grep -v '^\[INFO\]' > src/main/java/edu/utah/nanofab/coralapi/CoralAPISynchronizedDecorator.java
 * 
 * @author ryant
 */
public class CoralAPISynchronizedDecorator implements CoralAPIInterface {

	private final CoralAPI delegate;

	public CoralAPISynchronizedDecorator(
			edu.utah.nanofab.coralapi.CoralAPI delegate) {
		this.delegate = delegate;
	}

	public synchronized void setup(java.lang.String coralUser,
			java.lang.String configUrl) throws CoralConnectionException {
		delegate.setup(coralUser, configUrl);
	}

	public synchronized Projects getProjects() throws ProjectNotFoundSignal {
		return delegate.getProjects();
	}

	public synchronized Projects getAllProjects() throws ProjectNotFoundSignal {
		return delegate.getAllProjects();
	}

	public synchronized Project getProject(java.lang.String name)
			throws ProjectNotFoundSignal {
		return delegate.getProject(name);
	}

	public synchronized void createNewMember(
			edu.utah.nanofab.coralapi.resource.Member member)
			throws MemberDuplicateSignal, InvalidProjectSignal, Exception {
		delegate.createNewMember(member);
	}

	public synchronized void createNewProject(
			edu.utah.nanofab.coralapi.resource.Project project)
			throws Exception {
		delegate.createNewProject(project);
	}

	public synchronized void createNewProjectUnlessExists(
			edu.utah.nanofab.coralapi.resource.Project project)
			throws Exception {
		delegate.createNewProjectUnlessExists(project);
	}

	public synchronized void deleteMemberFromProject(
			java.lang.String memberName, java.lang.String projectName)
			throws InvalidTicketSignal, MemberDuplicateSignal,
			InvalidProjectSignal, NotAuthorizedSignal, InvalidMemberSignal {
		delegate.deleteMemberFromProject(memberName, projectName);
	}

	public synchronized void addProjectMembers(java.lang.String project,
			java.lang.String[] members) throws InvalidTicketSignal,
			InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
		delegate.addProjectMembers(project, members);
	}

	public synchronized void removeProjectMembers(java.lang.String project,
			java.lang.String[] members) throws InvalidTicketSignal,
			InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
		delegate.removeProjectMembers(project, members);
	}

	public synchronized Member getMember(java.lang.String member)
			throws UnknownMemberException, Exception {
		return delegate.getMember(member);
	}

	public synchronized Members getAllMembers() throws UnknownMemberException,
			Exception {
		return delegate.getAllMembers();
	}

	public synchronized Members getAllActiveMembers()
			throws UnknownMemberException, Exception {
		return delegate.getAllActiveMembers();
	}

	public synchronized Member getQualifications(java.lang.String member)
			throws UnknownMemberException, Exception {
		return delegate.getQualifications(member);
	}

	public synchronized Projects getMemberProjects(java.lang.String member) {
		return delegate.getMemberProjects(member);
	}

	public synchronized HashMap<String, ArrayList<String>> getAllMemberProjects(
			boolean activeOnly) {
		return delegate.getAllMemberProjects(activeOnly);
	}

	public synchronized void addMemberProjects(java.lang.String member,
			java.lang.String[] projects) throws InvalidTicketSignal,
			InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
		delegate.addMemberProjects(member, projects);
	}

	public synchronized void addMemberProjectCollection(
			java.lang.String member,
			edu.utah.nanofab.coralapi.collections.Projects projects) {
		delegate.addMemberProjectCollection(member, projects);
	}

	public synchronized void removeMemberProjects(java.lang.String member,
			java.lang.String[] projects) throws InvalidTicketSignal,
			InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
		delegate.removeMemberProjects(member, projects);
	}

	public synchronized void addEquipmentRoleToMember(java.lang.String member,
			java.lang.String roleName, java.lang.String resource)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		delegate.addEquipmentRoleToMember(member, roleName, resource);
	}

	public synchronized void removeEquipmentRoleFromMember(
			java.lang.String member, java.lang.String roleName,
			java.lang.String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal,
			InvalidResourceSignal {
		delegate.removeEquipmentRoleFromMember(member, roleName, resource);
	}

	public synchronized void removeLabRoleFromMember(java.lang.String member,
			java.lang.String roleName, java.lang.String lab)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		delegate.removeLabRoleFromMember(member, roleName, lab);
	}

	public synchronized void removeGenericRoleFromMember(
			java.lang.String member, java.lang.String roleName,
			java.lang.String resource, java.lang.String roleType)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		delegate.removeGenericRoleFromMember(member, roleName, resource,
				roleType);
	}

	public synchronized void addProjectRoleToMember(java.lang.String member,
			java.lang.String roleName, java.lang.String resource)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		delegate.addProjectRoleToMember(member, roleName, resource);
	}

	public synchronized void removeProjectRoleFromMember(
			java.lang.String member, java.lang.String roleName,
			java.lang.String resource) throws IOException, InvalidTicketSignal,
			InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal,
			InvalidResourceSignal {
		delegate.removeProjectRoleFromMember(member, roleName, resource);
	}

	public synchronized void addSafetyFlagToMember(java.lang.String member)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		delegate.addSafetyFlagToMember(member);
	}

	public synchronized void removeSafetyFlagFromMember(java.lang.String member)
			throws IOException, InvalidTicketSignal, InvalidRoleSignal,
			InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
		delegate.removeSafetyFlagFromMember(member);
	}

	public synchronized Members getProjectMembers(java.lang.String projectName) {
		return delegate.getProjectMembers(projectName);
	}

	public synchronized void createNewAccount(
			edu.utah.nanofab.coralapi.resource.Account acct) throws Exception {
		delegate.createNewAccount(acct);
	}

	public synchronized void createNewAccountUnlessExists(
			edu.utah.nanofab.coralapi.resource.Account acct) throws Exception {
		delegate.createNewAccountUnlessExists(acct);
	}

	public synchronized Machine getMachine(java.lang.String name)
			throws MachineRetrievalFailedSignal {
		return delegate.getMachine(name);
	}

	public synchronized Machines getAllMachines() {
		return delegate.getAllMachines();
	}

	public synchronized Account getAccount(java.lang.String name)
			throws InvalidAccountSignal {
		return delegate.getAccount(name);
	}

	public synchronized Accounts getAccounts() throws AccountNotFoundSignal {
		return delegate.getAccounts();
	}

	public synchronized Accounts getAllAccounts() throws AccountNotFoundSignal {
		return delegate.getAllAccounts();
	}

	public synchronized void deleteProject(java.lang.String projectName)
			throws InvalidTicketSignal, NotAuthorizedSignal, Exception {
		delegate.deleteProject(projectName);
	}

	public synchronized boolean authenticate(java.lang.String username,
			java.lang.String password) {
		return delegate.authenticate(username, password);
	}

	public synchronized void activateMember(java.lang.String memberName)
			throws UnknownMemberException, Exception {
		delegate.activateMember(memberName);
	}

	public synchronized void activateMemberWithProject(
			java.lang.String memberName, java.lang.String projectName)
			throws InvalidTicketSignal, ProjectNotFoundSignal,
			MemberNotFoundSignal, InvalidProjectSignal, NotAuthorizedSignal {
		delegate.activateMemberWithProject(memberName, projectName);
	}

	public synchronized void deactivateMember(java.lang.String memberName)
			throws InvalidTicketSignal, MemberNotFoundSignal,
			NotAuthorizedSignal {
		delegate.deactivateMember(memberName);
	}

	public synchronized void activateProject(java.lang.String projectName)
			throws InvalidTicketSignal, ProjectNotFoundSignal,
			InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal,
			Exception {
		delegate.activateProject(projectName);
	}

	public synchronized void deactivateProject(java.lang.String projectName)
			throws InvalidTicketSignal, ProjectNotFoundSignal,
			InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal,
			Exception {
		delegate.deactivateProject(projectName);
	}

	public synchronized void updateProject(
			edu.utah.nanofab.coralapi.resource.Project project)
			throws InvalidTicketSignal, ProjectNotFoundSignal,
			InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal,
			Exception {
		delegate.updateProject(project);
	}

	public synchronized void updateAccount(
			edu.utah.nanofab.coralapi.resource.Account account)
			throws InvalidTicketSignal, AccountNotFoundSignal,
			NotAuthorizedSignal, Exception {
		delegate.updateAccount(account);
	}

	public synchronized void updateMember(
			edu.utah.nanofab.coralapi.resource.Member member)
			throws InvalidTicketSignal, MemberNotFoundSignal,
			InvalidProjectSignal, NotAuthorizedSignal, Exception {
		delegate.updateMember(member);
	}

	public synchronized void close() {
		delegate.close();
	}

	public synchronized void reInitialize() throws CoralConnectionException {
		delegate.reInitialize();
	}

	public synchronized void addLabRoleToMember(
			edu.utah.nanofab.coralapi.resource.LabRole newRole)
			throws Exception {
		delegate.addLabRoleToMember(newRole);
	}

	public synchronized void addProjectRoleToMember(
			edu.utah.nanofab.coralapi.resource.ProjectRole newRole)
			throws Exception {
		delegate.addProjectRoleToMember(newRole);
	}

	public synchronized LabRoles getLabRoles(java.lang.String username)
			throws RoleNotFoundSignal {
		return delegate.getLabRoles(username);
	}

	public synchronized EquipmentRoles getEquipmentRoles(
			java.lang.String username) throws RoleNotFoundSignal {
		return delegate.getEquipmentRoles(username);
	}

	public synchronized void createNewRole(java.lang.String name,
			java.lang.String description, java.lang.String type)
			throws Exception {
		delegate.createNewRole(name, description, type);
	}

	public synchronized Role getRole(java.lang.String name,
			java.lang.String type) throws InvalidRoleException {
		return delegate.getRole(name, type);
	}

	public synchronized void createNewReservation(
			edu.utah.nanofab.coralapi.resource.Reservation r)
			throws RequestFailedException {
		delegate.createNewReservation(r);
	}

	public synchronized void createNewReservation(java.lang.String agent,
			java.lang.String member, java.lang.String project,
			java.lang.String item, java.lang.String bdate, int lengthInMinutes)
			throws RequestFailedException, Exception {
		delegate.createNewReservation(agent, member, project, item, bdate,
				lengthInMinutes);
	}

	public synchronized void deleteReservation(
			edu.utah.nanofab.coralapi.resource.Reservation r)
			throws InvalidTicketSignal, NotAuthorizedSignal,
			ReservationNotFoundSignal {
		delegate.deleteReservation(r);
	}

	public synchronized void deleteReservation(java.lang.String agent,
			java.lang.String member, java.lang.String project,
			java.lang.String item, java.lang.String bdate, int lengthInMinutes)
			throws ProjectNotFoundSignal, InvalidAccountSignal,
			MachineRetrievalFailedSignal, UnknownMemberException,
			ParseException, InvalidCallOrderException, Exception {
		delegate.deleteReservation(agent, member, project, item, bdate,
				lengthInMinutes);
	}

	public synchronized void deleteReservation(java.lang.String item,
			java.lang.String bdate, int lengthInMinutes)
			throws ProjectNotFoundSignal, InvalidAccountSignal,
			MachineRetrievalFailedSignal, UnknownMemberException,
			ParseException, InvalidCallOrderException, Exception {
		delegate.deleteReservation(item, bdate, lengthInMinutes);
	}

	public synchronized void deleteReservation(java.lang.String item,
			java.util.Date bdate, int lengthInMinutes)
			throws ProjectNotFoundSignal, InvalidAccountSignal,
			MachineRetrievalFailedSignal, UnknownMemberException,
			ParseException, InvalidCallOrderException, Exception {
		delegate.deleteReservation(item, bdate, lengthInMinutes);
	}

	public synchronized Reservations getReservations(java.lang.String member,
			java.lang.String tool, java.util.Date bdate, java.util.Date edate)
			throws Exception {
		return delegate.getReservations(member, tool, bdate, edate);
	}

	public synchronized Reservations getReservations(java.lang.String member,
			java.lang.String tool, java.lang.String bdateAsString,
			int numberOfMinutes) throws Exception {
		return delegate.getReservations(member, tool, bdateAsString,
				numberOfMinutes);
	}

	public synchronized Reservations getReservations(java.lang.String tool,
			java.util.Date bdate, java.util.Date edate) throws Exception {
		return delegate.getReservations(tool, bdate, edate);
	}

	public synchronized String enable(
			edu.utah.nanofab.coralapi.resource.Enable enableActivity)
			throws InvalidTicketSignal, InvalidAgentSignal,
			InvalidProjectSignal, InvalidAccountSignal, InvalidMemberSignal,
			InvalidResourceSignal, InvalidProcessSignal,
			ResourceUnavailableSignal, NotAuthorizedSignal {
		return delegate.enable(enableActivity);
	}

	public synchronized String enable(java.lang.String agent,
			java.lang.String member, java.lang.String project,
			java.lang.String account, java.lang.String machineName)
			throws UnknownMemberException, Exception {
		return delegate.enable(agent, member, project, account, machineName);
	}

	public synchronized String enable(java.lang.String agent,
			java.lang.String member, java.lang.String project,
			java.lang.String machineName) throws UnknownMemberException,
			Exception {
		return delegate.enable(agent, member, project, machineName);
	}

	public synchronized String disable(java.lang.String agent,
			java.lang.String machine) throws InvalidTicketSignal,
			InvalidAgentSignal, InvalidResourceSignal,
			ResourceUnavailableSignal, NotAuthorizedSignal {
		return delegate.disable(agent, machine);
	}

	public synchronized String disableWithRundata(java.lang.String agent,
			java.lang.String machine, java.lang.String rundataId)
			throws InvalidTicketSignal, InvalidAgentSignal,
			InvalidResourceSignal, ResourceUnavailableSignal,
			NotAuthorizedSignal {
		return delegate.disableWithRundata(agent, machine, rundataId);
	}

	public synchronized void updatePassword(java.lang.String member,
			java.lang.String newPassword) throws InvalidMemberSignal,
			NotAuthorizedSignal, InvalidTicketSignal {
		delegate.updatePassword(member, newPassword);
	}

	public synchronized boolean checkKeyIsValid() {
		return delegate.checkKeyIsValid();
	}

	public synchronized String getRundataDefinitionForProcess(
			java.lang.String process) throws NullReturnException,
			ServerErrorException {
		return delegate.getRundataDefinitionForProcess(process);
	}

	public synchronized RunDataProcess[] getRundataProcesses(
			java.lang.String tool) throws NullReturnException,
			ServerErrorException {
		return delegate.getRundataProcesses(tool);
	}

	public synchronized RunDataProcess[] getRundataProcessesWithDefinitions(
			java.lang.String tool) throws NullReturnException,
			ServerErrorException {
		return delegate.getRundataProcessesWithDefinitions(tool);
	}

	public synchronized String createRunData(java.lang.String rundata)
			throws NullReturnException, ServerErrorException {
		return delegate.createRunData(rundata);
	}

	public synchronized void updateRunData(java.lang.String rundata)
			throws NullReturnException, ServerErrorException {
		delegate.updateRunData(rundata);
	}

	public synchronized void commitRunData(java.lang.String id)
			throws NullReturnException, ServerErrorException {
		delegate.commitRunData(id);
	}

	public synchronized String createAndCommitRunData(
			java.lang.String xmlDefinition) throws NullReturnException,
			ServerErrorException, Exception {
		return delegate.createAndCommitRunData(xmlDefinition);
	}

	public synchronized RundataAdapter rundataFromXml(java.lang.String xml) {
		return delegate.rundataFromXml(xml);
	}

	public synchronized void createAdjustmentRunData() {
		delegate.createAdjustmentRunData();
	}

	public synchronized void addActivityToRundata(java.lang.String arg1,
			java.lang.String arg2) {
		delegate.addActivityToRundata(arg1, arg2);
	}

	public synchronized String getLogLevel() {
		return delegate.getLogLevel();
	}

	public synchronized void setLogLevel(java.lang.String logLevel) {
		delegate.setLogLevel(logLevel);
	}
}
