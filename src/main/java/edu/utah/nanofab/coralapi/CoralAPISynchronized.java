/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utah.nanofab.coralapi;

import edu.utah.nanofab.coralapi.collections.Accounts;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Machines;
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
import edu.utah.nanofab.coralapi.resource.Machine;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.coralapi.resource.Role;
import edu.utah.nanofab.coralapi.resource.RunDataProcess;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
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

public class CoralAPISynchronized {
    private CoralAPI api;
    public CoralAPISynchronized(String coralUser, String configUrl) {
        api = new CoralAPI(coralUser, configUrl);
    }
    public synchronized void addEquipmentRoleToMember(String member, String roleName, String resource) throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
	this.api.addEquipmentRoleToMember( member,  roleName,  resource);
    }
    public synchronized void addLabRoleToMember (LabRole newRole)  throws Exception {
	this.api.addLabRoleToMember( newRole);
    }
    public synchronized void addMemberProjectCollection (String member, Projects projects)  {
	this.api.addMemberProjectCollection( member,  projects);
    }
    public synchronized void addMemberProjects (String member, String[] projects)  throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
	this.api.addMemberProjects( member,  projects);
    }
    public synchronized void addProjectMembers (String project, String[] members)  throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
	this.api.addProjectMembers( project,  members);
    }
    public synchronized void addProjectRoleToMember (String member, String roleName, String resource)  throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
	this.api.addProjectRoleToMember( member,  roleName,  resource);
    }
    public synchronized void addSafetyFlagToMember (String member)  throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
	this.api.addSafetyFlagToMember( member);
    }
    public synchronized boolean authenticate (String username, String password)  {
	return this.api.authenticate( username,  password);
    }
    public synchronized boolean checkKeyIsValid ()  {
	return this.api.checkKeyIsValid();
    }
    public synchronized void close ()  {
	this.api.close();
    }
    public synchronized void createNewAccount (Account acct)  throws Exception {
	this.api.createNewAccount( acct);
    }
    public synchronized void createNewAccountUnlessExists (Account acct)  throws Exception {
	this.api.createNewAccountUnlessExists( acct);
    }
    public synchronized void createNewMember (Member member)  throws MemberDuplicateSignal, InvalidProjectSignal, Exception {
	this.api.createNewMember( member);
    }
    public synchronized void createNewProject (Project project)  throws Exception {
	this.api.createNewProject( project);
    }
    public synchronized void createNewProjectUnlessExists (Project project)  throws Exception {
	this.api.createNewProjectUnlessExists( project);
    }
    public synchronized void createNewReservation (Reservation r) throws RequestFailedException  {
	this.api.createNewReservation( r);
    }
    public synchronized void createNewReservation (String agent, String member, String project, String item, String bdate, int lengthInMinutes) throws RequestFailedException, Exception {
	this.api.createNewReservation( agent,  member,  project,  item,  bdate,  lengthInMinutes);
    }
    public synchronized void createNewRole (String name, String description, String type)  throws Exception {
	this.api.createNewRole( name,  description,  type);
    }
    public synchronized void deleteMemberFromProject (String memberName, String projectName)  throws InvalidTicketSignal, MemberDuplicateSignal, InvalidProjectSignal, NotAuthorizedSignal, InvalidMemberSignal {
	this.api.deleteMemberFromProject( memberName,  projectName);
    }
    public synchronized void deleteProject (String projectName)  throws InvalidTicketSignal, NotAuthorizedSignal, Exception {
	this.api.deleteProject( projectName);
    }
    public synchronized void deleteReservation (Reservation r)  throws InvalidTicketSignal, NotAuthorizedSignal, ReservationNotFoundSignal {
	this.api.deleteReservation( r);
    }
    public synchronized void deleteReservation (String agent, String member, String project, String item, String bdate, int lengthInMinutes)  throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception {
	this.api.deleteReservation( agent,  member,  project,  item,  bdate,  lengthInMinutes);
    }
    public synchronized void deleteReservation(String item, String bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception {
        this.api.deleteReservation( item,  bdate,  lengthInMinutes);
    }
    public void deleteReservation(String item, Date bdate, int lengthInMinutes) throws ProjectNotFoundSignal, InvalidAccountSignal, MachineRetrievalFailedSignal, UnknownMemberException, ParseException, InvalidCallOrderException, Exception {
        this.api.deleteReservation( item,  bdate,  lengthInMinutes);
    }
    public synchronized String disable (String agent, String machine)  throws InvalidTicketSignal, InvalidAgentSignal, InvalidResourceSignal, ResourceUnavailableSignal, NotAuthorizedSignal {
	return this.api.disable( agent,  machine);
    }
    public synchronized String enable (Enable enableActivity)  throws InvalidTicketSignal, InvalidAgentSignal, InvalidProjectSignal, InvalidAccountSignal, InvalidMemberSignal, InvalidResourceSignal, InvalidProcessSignal, ResourceUnavailableSignal, NotAuthorizedSignal {
	return this.api.enable( enableActivity);
    }
    public synchronized String enable (String agent, String member, String project, String account, String machineName)  throws UnknownMemberException, Exception {
	return this.api.enable( agent,  member,  project,  account,  machineName);
    }
    public synchronized String enable (String agent, String member, String project, String machineName)  throws UnknownMemberException, Exception {
	return this.api.enable( agent,  member,  project,  machineName);
    }
    public synchronized Account getAccount (String name)  throws InvalidAccountSignal {
	return this.api.getAccount( name);
    }
    public synchronized Accounts getAccounts ()  throws AccountNotFoundSignal {
	return this.api.getAccounts();
    }
    public synchronized Machines getAllMachines ()  {
	return this.api.getAllMachines();
    }
    public synchronized LabRoles getLabRoles (String username)  throws RoleNotFoundSignal {
	return this.api.getLabRoles( username);
    }
    public synchronized String getLogLevel ()  {
	return this.api.getLogLevel();
    }
    public synchronized Machine getMachine (String name)  throws MachineRetrievalFailedSignal {
	return this.api.getMachine( name);
    }
    public synchronized Member getMember (String member)  throws UnknownMemberException, Exception {
	return this.api.getMember( member);
    }
    public synchronized Projects getMemberProjects (String member)  {
	return this.api.getMemberProjects( member);
    }
    public synchronized Project getProject (String name)  throws ProjectNotFoundSignal {
	return this.api.getProject( name);
    }
    public synchronized Members getProjectMembers (String projectName)  {
	return this.api.getProjectMembers( projectName);
    }
    public synchronized Projects getProjects ()  throws ProjectNotFoundSignal {
	return this.api.getProjects();
    }
    public synchronized Member getQualifications (String member)  throws UnknownMemberException, Exception {
	return this.api.getQualifications( member);
    }
    public synchronized Reservations getReservations (String member, String tool, Date bdate, Date edate)  throws Exception {
	return this.api.getReservations( member,  tool,  bdate,  edate);
    }
    public synchronized Reservations getReservations (String member, String tool, String bdateAsString, int numberOfMinutes)  throws Exception {
	return this.api.getReservations( member,  tool,  bdateAsString,  numberOfMinutes);
    }
    public synchronized Reservations getReservations (String tool, Date bdate, Date edate)  throws Exception {
	return this.api.getReservations( tool,  bdate,  edate);
    }
    public synchronized Role getRole (String name, String type)  throws InvalidRoleException {
	return this.api.getRole( name,  type);
    }
    public synchronized void removeEquipmentRoleFromMember (String member, String roleName, String resource)  throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
	this.api.removeEquipmentRoleFromMember( member,  roleName,  resource);
    }
    public synchronized void removeMemberProjects (String member, String[] projects)  throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
	this.api.removeMemberProjects( member, projects);
    }
    public synchronized void removeProjectMembers (String project, String[] members)  throws InvalidTicketSignal, InvalidMemberSignal, InvalidProjectSignal, NotAuthorizedSignal {
	this.api.removeProjectMembers( project, members);
    }
    public synchronized void removeProjectRoleFromMember (String member, String roleName, String resource)  throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
	this.api.removeProjectRoleFromMember( member,  roleName,  resource);
    }
    public synchronized void removeSafetyFlagFromMember (String member)  throws IOException, InvalidTicketSignal, InvalidRoleSignal, InvalidMemberSignal, NotAuthorizedSignal, InvalidResourceSignal {
	this.api.removeSafetyFlagFromMember( member);
    }
    public synchronized void setLogLevel (String logLevel)  {
	this.api.setLogLevel( logLevel);
    }
    public synchronized void updateAccount (Account account)  throws InvalidTicketSignal, AccountNotFoundSignal, NotAuthorizedSignal, Exception {
	this.api.updateAccount( account);
    }
    public synchronized void updateMember (Member member)  throws InvalidTicketSignal, MemberNotFoundSignal, InvalidProjectSignal, NotAuthorizedSignal, Exception {
	this.api.updateMember( member);
    }
    public synchronized void updatePassword (String member, String newPassword)  throws InvalidMemberSignal, NotAuthorizedSignal, InvalidTicketSignal {
	this.api.updatePassword( member,  newPassword);
    }
    public synchronized void updateProject (Project project)  throws InvalidTicketSignal, ProjectNotFoundSignal, InvalidNicknameSignal, InvalidAccountSignal, NotAuthorizedSignal, Exception {
	this.api.updateProject( project);
    }
   
    
    
    
  public synchronized RunDataProcess[] getRundataProcessesWithDefinitions(String tool) throws NullReturnException, ServerErrorException {
    return this.api.getRundataProcessesWithDefinitions(tool);
  }
  
  public String createRunData(String rundata) throws NullReturnException, ServerErrorException {
    return this.api.createRunData(rundata);
  }
  
  public void updateRunData(String rundata) throws NullReturnException, ServerErrorException {
    this.api.updateRunData(rundata);
  }
  
  public void commitRunData(String id) throws NullReturnException, ServerErrorException {
    this.api.commitRunData(id);
  }
  
  public RundataAdapter rundataFromXml(String xml) {
      return this.api.rundataFromXml(xml);
  }
  
  public void createAdjustmentRunData() {
        //JUST a placeholder for: runmgr.createAdjustmentRunData(...);
  }

  public String disableWithRundata(String agent, String machine, String rundataId) throws InvalidTicketSignal, InvalidAgentSignal, InvalidResourceSignal, ResourceUnavailableSignal, NotAuthorizedSignal {
      return this.api.disableWithRundata(agent, machine, rundataId);
  }

  public String createAndCommitRunData(String xmlDefinition) throws NullReturnException, ServerErrorException, Exception {
      return this.api.createAndCommitRunData(xmlDefinition);
  }
    
}
