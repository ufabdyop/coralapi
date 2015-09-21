package edu.utah.nanofab.coralapi;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
import edu.utah.nanofab.coralapi.collections.Reservations;
import edu.utah.nanofab.coralapi.exceptions.InvalidRoleException;
import edu.utah.nanofab.coralapi.exceptions.InvalidTicketException;
import edu.utah.nanofab.coralapi.exceptions.NotAuthorizedException;
import edu.utah.nanofab.coralapi.exceptions.RoleDuplicateException;
import edu.utah.nanofab.coralapi.exceptions.UnknownMemberException;
import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.coralapi.resource.Role;
import edu.utah.nanofab.coralapi.helper.TimestampConverter;
import edu.utah.nanofab.coralapi.helper.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.ProjectNotFoundSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoralAPIProposedTestsNeedFix extends TestCase {
	
	String dbhost = "jdbc:postgresql://localhost/coral";
	String dbuser = "coraldba";
	String dbpass = "coraldba";
    FixtureHelper data = new FixtureHelper(dbhost, dbuser, dbpass);
    public static final Logger logger = LoggerFactory.getLogger(CoralAPITest.class);
    
	private CoralAPI instance;
	
	public CoralAPIProposedTestsNeedFix (String testName) {
        super(testName);
    }
  /**
   *
   * Tests in error:
   *   testMemberDateManipulationRoundTrip(edu.utah.nanofab.coralapi.CoralAPITest)
   *     testGetLabRoles(edu.utah.nanofab.coralapi.CoralAPITest): IDL:org/opencoral/idl/NotAuthorizedSignal:1.0
   *       testProjectDateManipulationRoundTrip(edu.utah.nanofab.coralapi.CoralAPITest)
   *         testCreateNewReservation(edu.utah.nanofab.coralapi.CoralAPITest): IDL:org/opencoral/idl/NotAuthorizedSignal:1.0
   *           testRemoveProjectMembers(edu.utah.nanofab.coralapi.CoralAPITest): IDL:org/opencoral/idl/NotAuthorizedSignal:1.0
   *
   * */
    
    protected void guardAgainstRunningOnLive() throws Exception {
    	String liveHost = "coral.nanofab.utah.edu";
        Process results = Runtime.getRuntime().exec("hostname");
        InputStream stdout = results.getInputStream();
        BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
        String hostname = reader.readLine();
        
        assertTrue(!hostname.equals(liveHost));
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        guardAgainstRunningOnLive();
        
        String coralUser = "coral";
        String configUrl = "http://coral-dev-box/coral/lib/config.jar";
    	this.instance = new CoralAPI(coralUser, configUrl);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * This test only passes with modified opencoral source to allow edates to be set.
     * 
     * @throws Exception
     */
    public void testProjectDateManipulationRoundTrip() throws Exception {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2099, 0, 27, 13, 59, 00);
        SimpleDateFormat format = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date testDate = cal.getTime();
        assertEquals("2099-01-27 13:59:00", format.format(testDate));
        
        String accountName = "JUnit Test Account";
        data.deleteAccount(accountName);
        Account account = new Account();
        account.setName(accountName);
        account.setEdate(testDate);
        instance.createNewAccount(account);
        
        String projectName = "JUnit Test Project";
        data.deleteProject(projectName);
        Project project = new Project();
        project.setName(projectName);
        project.setAccount(accountName);
        project.setEdate(testDate);
        instance.createNewProject(project);
        
        Project fetched = instance.getProject(projectName);
        assertEquals(projectName, fetched.getName());
        assertEquals(project.getEdate().toString(), fetched.getEdate().toString());

        // Test updateProject too
    	cal.set(2199, 1, 28, 14, 57, 01);
    	project.setEdate(cal.getTime());
    	instance.updateProject(project);
        fetched = instance.getProject(project.getName());
        assertEquals(fetched.getName(), project.getName());
        assertEquals(project.getEdate().toString(), fetched.getEdate().toString());
        
        // Clean up this tests database entries.
        data.deleteProject(projectName);
        data.deleteAccount(accountName);
    }

    public void testMemberDateManipulationRoundTrip() throws Exception {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2099, 0, 27, 13, 59, 00);
        SimpleDateFormat format = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date testDate = cal.getTime();
        assertEquals("2099-01-27 13:59:00", format.format(testDate));
        
        String memberName = "JUnit User";
        String projectName = "JUnit Test Project";
        String accountName = "JUnit Test Account";
        this.createTestAccount(accountName);
        this.createTestProject(projectName, accountName);
        
        data.deleteMember(memberName);
        Member member = new Member();
        member.setName(memberName);
        member.setProject(projectName);
        member.setEdate(testDate);;
        instance.createNewMember(member);
        
        Member fetched = instance.getMember(member.getName());
        assertEquals(memberName, fetched.getName());
        assertEquals(member.getEdate().toString(), fetched.getEdate().toString());
        
        // Clean up this tests database entries.
        data.deleteMember(memberName);
        data.deleteProject(projectName);
        data.deleteAccount(accountName);
    }    

    public void testGetLabRoles() throws Exception {
        String memberName = "JUnit User";
        String projectName = "JUnit Test Project";
        String accountName = "JUnit Test Account";
    	this.createTestAccount(accountName);
    	this.createTestProject(projectName, accountName);
    	
        data.deleteMember(memberName);
    	Member member = new Member();
    	member.setName(memberName);
    	member.setProject(projectName);
    	member.setActive(true);
        instance.createNewMember(member);
        
        String labName = "nano";
        String roleName = "JUnit Test Role"; 
        String type = "lab";
        data.deleteRole(roleName);
        instance.createNewRole(roleName, "Temporary Test Role", type);
        
        LabRole newRole = new LabRole(labName, memberName, roleName);
        instance.addLabRoleToMember(newRole);
        LabRoles roles = instance.getLabRoles(member.getName());
        assertTrue(roles.contains(newRole));
        
        // Clean up this tests database entries.
        data.deleteMember(memberName);
        data.deleteProject(projectName);
        data.deleteAccount(accountName);
        data.deleteRole(roleName);
    }
    
    /**
     * Tests the 'createNewReservation' functionality.
     * 
     * @throws Exception
     */
    public void testCreateNewReservation() throws Exception {
        String accountName = "JUnit Test Account";
        String projectName = "JUnit Test Project";
        String user = "user";
        String pass = "pass";
    	Account account = this.createTestAccount(accountName);
        Project project = this.createTestProject(projectName, accountName);
        Member member = this.createTestMember(user, pass, projectName);
    	
        String tool = "TMV Super";
        Date bdate = TimestampConverter.dateFromDateComponents(2014, 7, 18, 12, 0, 0);
        Date edate = TimestampConverter.dateFromDateComponents(2014, 7, 18, 13, 0, 0);
        data.deleteReservation(tool, bdate.toString(), edate.toString());
        Reservation r = new Reservation();
        r.setItem(tool);
        r.setBdate(2014,7,18,12,0);
        r.setEdate(2014,7,18,13,0);
        r.setMember(member);
        r.setProject(project);
        r.setLab("nano");
        r.setAccount(account);
        instance.createNewReservation(r);
        
        Reservations fetched = instance.getReservations(user, tool, bdate, edate);
        assertTrue(fetched.size() == 2); // Reservations are stored in 30 min intervals, so we would expect 2 reservations.
        assertEquals(fetched.toArray()[0].getMember().getName(), r.getMember().getName());
        
        // Clean up this tests database entries.
        data.deleteMember(user);
        data.deleteReservation(tool, bdate.toString(), edate.toString());
        data.deleteProject(projectName);
        data.deleteAccount(accountName);
    }

    /**
     * Creates a coral user with supplied user, password, and default project. This user can be used 
     * to test operations on. 
     * @return 
     * 
     * @throws Exception
     */
    public Member createTestMember(String username, String password, String project) throws Exception {
    	data.deleteMember(username);
    	Member member = new Member();
    	member.setName(username);
    	member.setPassword(password);
    	member.setProject(project);
    	member.setActive(true);
        instance.createNewMember(member);
        return member;
    }

    public void testRemoveProjectMembers() throws Exception {
    	String memberName1 = "JUnit User 1";
    	String memberName2 = "JUnit User 2";
    	String projectName1 = "JUnit Test Project 1";
    	String projectName2 = "JUnit Test Project 2";
    	String accountName = "JUnit Test Account";
    	this.createTestAccount(accountName);
    	this.createTestProject(projectName1, accountName);
    	this.createTestProject(projectName2, accountName);
    	
    	data.deleteMember(memberName1);
    	Member member1 = new Member();
    	member1.setName(memberName1);
    	member1.setProject(projectName1);
    	member1.setActive(true);
    	instance.createNewMember(member1);
    	
    	data.deleteMember(memberName2);
    	Member member2 = new Member();
    	member2.setName(memberName2);
    	member2.setProject(projectName1);
    	member2.setActive(true);
    	instance.createNewMember(member2);
    	
    	String[] members = {memberName1, memberName2};
    	instance.addProjectMembers(projectName2, members);
    	int sizeBefore = instance.getProjectMembers(projectName2).size();
    	assertTrue(sizeBefore >= 2);

    	instance.removeProjectMembers(projectName2, members);
    	int sizeAfter = instance.getProjectMembers(projectName2).size();
    	int difference = sizeBefore - sizeAfter; 
    	assertEquals(2, difference);
    	
    	// Clean up this tests database entries.
    	data.deleteMember(memberName1);
    	data.deleteMember(memberName2);
    	data.deleteProject(projectName1);
    	data.deleteAccount(accountName);
    }
    
    /**
     * Create a project with the supplied project name and account. This project can be used to 
     * test operations on.
     * @return 
     * 
     * @throws Exception
     */
    public Project createTestProject(String projectName, String account) throws Exception {
    	data.deleteProject(projectName);
    	Project project = new Project();
    	project.setName(projectName);
    	project.setAccount(account);
    	project.setActive(true);
    	instance.createNewProject(project);
    	return project;
    }
    
    /**
     * Create an account with the supplied name.This account can be used to test operations on.
     * 
     * @throws Exception
     */
    public Account createTestAccount(String accountName) throws Exception {
    	data.deleteAccount(accountName);
    	Account account = new Account();
    	account.setName(accountName);
    	account.setDescription("A JUnit testing account.");
    	account.setActive(true);
    	instance.createNewAccount(account);
    	return account;
    }
    
}

