package edu.utah.nanofab.coralapi;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
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
import java.net.InetAddress


import junit.framework.TestCase;

import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.ProjectNotFoundSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoralAPITest extends TestCase {
	
	String dbhost = "jdbc:postgresql://localhost/coral";
	String dbuser = "coraldba";
	String dbpass = "coraldba";
    FixtureHelper data = new FixtureHelper(dbhost, dbuser, dbpass);
    public static final Logger logger = LoggerFactory.getLogger(CoralAPITest.class);
    
	private CoralAPI instance;
	
	public CoralAPITest(String testName) {
        super(testName);
    }
    
    protected void guardAgainstRunningOnLive() throws Exception {
        String liveHost = "coral.nanofab.utah.edu";
        Process results = Runtime.getRuntime().exec("hostname");
        InputStream stdout = results.getInputStream();
        BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
        String hostname = reader.readLine();
        
        assertTrue(!hostname.equals(liveHost));

        InetAddress address = InetAddress.getByName("coral-dev-box"); 
        assertTrue(address.getHostAddress().equals("127.0.0.1")); 
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        guardAgainstRunningOnLive();
        
        String coralUser = "coral";
        String iorUrl = "http://coral-dev-box/IOR/";
        String configUrl = "http://coral-dev-box/coral/lib/config.jar";
    	this.instance = new CoralAPI(coralUser, iorUrl, configUrl);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Tests the 'getProjects' functionality.
     * 
     * @throws Exception 
     */
    public void testGetProjects() throws Exception{
    	String projectName = "JUnit Test Project";
    	String accountName = "JUnit Test Account";
    	this.createTestAccount(accountName);
    	this.createTestProject(projectName, accountName);
    	
    	int len = instance.getProjects().size();
    	assertTrue(len >= 1);
    }
    
    /**
     * Tests the 'getMemberProjects' functionality.
     * 
     * @throws Exception
     */
    public void testGetMemberProjects() throws Exception {
    	String memberName = "JUnit User";
    	String projectName1 = "JUnit Test Project 1";
    	String accountName = "JUnit Test Account 1";
    	this.createTestAccount(accountName);
    	this.createTestProject(projectName1, accountName);
    	
    	String projectName2 = "JUnit Test Project 2";
    	this.createTestProject(projectName2, accountName);
    	
    	data.deleteMember(memberName);
    	Member member = new Member();
    	member.setName(memberName);
    	member.setProject(projectName1);
    	member.setActive(true);
    	instance.createNewMember(member);
    	
    	String[] projectList = {projectName1, projectName2};
    	instance.addMemberProjects(memberName, projectList);
    	
    	Projects memberProjects = instance.getMemberProjects(memberName);
    	int numProjects = memberProjects.size();
    	assertTrue(numProjects >= 2);
    	
    	// Clean up this tests database entries.
    	this.data.deleteMember(memberName);
    	this.data.deleteProject(projectName1);
    	this.data.deleteProject(projectName2);
    	this.data.deleteAccount(accountName);
    }

    /**
     * Tests the 'getAccounts' functionality.
     * 
     * @throws Exception 
     */
    public void testGetAccounts() throws Exception {
    	String accountName1 = "JUnit Test Account 1";
    	this.createTestAccount(accountName1);
    	
    	String accountName2 = "JUnit Test Account 2";
    	this.createTestAccount(accountName2);
    	
    	int len = instance.getAccounts().size();
    	assertTrue(len >= 2);
    	
    	// Clean up this tests database entries.
    	this.data.deleteAccount(accountName1);
    	this.data.deleteAccount(accountName2);
    }

    /**
     * Tests the 'createNewMember' functionality.
     * 
     * @throws Exception
     */
    public void testCreateNewMemberRoundTrip() throws Exception {
    	String memberName = "JUnit User";
    	String projectName = "JUnit Test Project";
    	String accountName = "JUnit Test Account";
        this.createTestAccount(accountName);
        this.createTestProject(projectName, accountName);
        
        data.deleteMember(memberName);
    	Member member = new Member();
    	member.setName(memberName);
    	member.setProject(projectName);
    	member.setAddress1("a");
    	member.setAddress2("b");
    	member.setAdvisor("c");
    	member.setAltFax("d");
    	member.setAltOffice("e");
    	member.setAltPhone("f");
    	member.setCity("g");
    	member.setDisability("h");
    	member.setEmail("i");
    	member.setEthnicity("j");
    	member.setFax("k");
    	member.setFirstName("l");
    	member.setLastName("m");
    	member.setMailCode("n");
    	member.setPassword("o");
    	member.setPhone("p");
    	member.setRace("q");
    	member.setState("r");
    	member.setType("s");
    	member.setUnivid("t");
    	member.setUrl("u");
    	member.setZipcode("v");
    	member.setActive(true);    	
        instance.createNewMember(member);
        
        Member fetched = instance.getMember(memberName);
        assertEquals(member.getAddress1(), fetched.getAddress1());
        assertEquals(member.getAddress2(), fetched.getAddress2());
        assertEquals(member.getAdvisor(), fetched.getAdvisor());
        assertEquals(member.getAltFax(), fetched.getAltFax());
        assertEquals(member.getAltOffice(), fetched.getAltOffice());
        assertEquals(member.getAltPhone(), fetched.getAltPhone());
        assertEquals(member.getCity(), fetched.getCity());
        assertEquals(member.getDisability(), fetched.getDisability());
        assertEquals(member.getEmail(), fetched.getEmail());
        assertEquals(member.getEthnicity(), fetched.getEthnicity());
        assertEquals(member.getFax(), fetched.getFax());
        assertEquals(member.getFirstName(), fetched.getFirstName());
        assertEquals(member.getLastName(), fetched.getLastName());
        assertEquals(member.getMailCode(), fetched.getMailCode());
        assertEquals(member.getPhone(), fetched.getPhone());
        assertEquals(member.getRace(), fetched.getRace());
        assertEquals(member.getState(), fetched.getState());
        assertEquals(member.getType(), fetched.getType());
        assertEquals(member.getUnivid(), fetched.getUnivid());
        assertEquals(member.getUrl(), fetched.getUrl());
        assertEquals(member.getZipcode(), fetched.getZipcode());
        assertEquals(member.isActive(), fetched.isActive());
        
        // Clean up this tests database entries.
        data.deleteMember(memberName);
        data.deleteProject(projectName);
        data.deleteAccount(accountName);
    }

    /**
     * Tests the 'createNewProject' functionality.
     * 
     * @throws Exception 
     */
    public void testCreateNewProject() throws Exception {
    	String accountName = "JUnit Test Account";
    	String projectName = "JUnit Test Project";
        this.createTestAccount(accountName);
        
        data.deleteProject(projectName);
        Project project = new Project();
        project.setName(projectName);
        project.setAccount(accountName);
        project.setDescription("Short Description");
        project.setDiscipline("Discipline");
        project.setNickname("Nickname");
        project.setPi("PI");
        project.setType("Type");
        project.setActive(true);
        instance.createNewProject(project);
        
        Project fetched = instance.getProject(projectName);
        assertEquals(fetched.getName(), projectName);
        assertEquals(fetched.getAccount(), project.getAccount());
        assertEquals(fetched.getDescription(), project.getDescription());
        assertEquals(fetched.getDiscipline(), project.getDiscipline());
        assertEquals(fetched.getNickname(), project.getNickname());
        assertEquals(fetched.getPi(), project.getPi());
        assertEquals(fetched.getType(), project.getType());
        
        // Clean up this tests database entries.
        this.data.deleteProject(projectName);
        this.data.deleteAccount(accountName);
    }
    
    public void testCreateProjectThenUpdateIt() throws Exception {
    	String accountName = "JUnit Test Account";
    	String projectName = "JUnit Test Project";
    	this.createTestAccount(accountName);
    	
    	data.deleteProject(projectName);
    	Project p = new Project();
    	p.setName(projectName);
    	p.setAccount(accountName);
    	p.setActive(true);
    	instance.createNewProject(p);
    	
    	Project project = instance.getProject(projectName);
    	project.setNickname("New Nickname");
    	instance.updateProject(project);
    	
    	Project fetched = instance.getProject(projectName);
    	assertEquals(fetched.getNickname(), "New Nickname");
    	
    	// Clean up this tests database entries.
    	data.deleteProject(projectName);
    	data.deleteAccount(accountName);
    }
    
    public void testCreateAccountThenUpdateIt() throws Exception {
    	String accountName = "JUnit Test Account";
    	data.deleteAccount(accountName);
    	
    	Account acct = new Account();
    	acct.setName(accountName);
    	acct.setActive(true);
    	instance.createNewAccount(acct);
    	
    	Account a = instance.getAccount(accountName);
    	String description = "Added Description";
    	a.setDescription(description);
    	instance.updateAccount(a);
    	
    	Account fetched = instance.getAccount(accountName);
    	assertEquals(fetched.getDescription(), description);
    	
    	// Clean up this tests database entries.
    	data.deleteAccount(accountName);
    }
    
    public void testCreateMemberThenUpdateIt() throws Exception {
    	String memberName = "JUnit User";
    	String projectName = "JUnit Test Project";
    	String accountName = "JUnit Test Account";
    	this.createTestAccount(accountName);
    	this.createTestProject(projectName, accountName);
    	
    	data.deleteMember(memberName);
    	Member mem = new Member();
    	mem.setName(memberName);
    	mem.setProject(projectName);
    	mem.setAddress1("Old Address");
    	mem.setUnivid("1234");
    	mem.setActive(true);
    	instance.createNewMember(mem);

    	Member m = instance.getMember(memberName);
    	m.setAddress1("New Address");
    	instance.updateMember(m);
    	
    	Member fetched = instance.getMember(memberName);
    	assertEquals(fetched.getAddress1(), "New Address");
    	
    	// Clean up this tests database entries.
    	data.deleteMember(memberName);
    	data.deleteProject(projectName);
    	data.deleteAccount(accountName);
    }
        
    public void testGetProjectThrowsExceptionForMissingProject() throws InvalidTicketSignal, NotAuthorizedSignal, Exception {
    	String projectName = "JUnit Test Project";
    	data.deleteProject(projectName);
    	
    	boolean exceptionThrown = false;
    	try {	
	    	instance.getProject(projectName);
    	} catch (ProjectNotFoundSignal e) {
    		exceptionThrown = true;
    	}
    	assertTrue(exceptionThrown);
    }

    public void testGetAccountThrowsExceptionForMissingProject() {
    	String accountName = "JUnit Test Account";
    	data.deleteAccount(accountName);
    	
    	boolean exceptionThrown = false;
    	try {
	    	instance.getAccount(accountName);
    	} catch (InvalidAccountSignal e) {
    		exceptionThrown = true;
		}
    	assertTrue(exceptionThrown);
    }
    
    public void testAuthentication() throws Exception {
    	String accountName = "JUnit Test Account";
    	String projectName = "JUnit Test Project";
    	this.createTestAccount(accountName);
    	this.createTestProject(projectName, accountName);
    	
    	// Create a new member for this test.
    	String username = "user";
    	String password = "pass";
    	this.createTestMember(username, password, projectName);

    	boolean result1 = instance.authenticate("user", "pass");
    	assertTrue(result1);
        boolean result2 = instance.authenticate("invalid-user", "invalid-pass");
    	assertFalse(result2);
    	boolean result3 = instance.authenticate(null, "pass");
    	assertFalse(result3);
    	boolean result4 = instance.authenticate("user", null);
    	assertFalse(result4);
        boolean result5 = instance.authenticate(null, null);
        assertFalse(result5);
        
        // Clean up this tests databases entries.
        data.deleteMember(username);
        data.deleteProject(projectName);
        data.deleteAccount(accountName);
    }
    
    /**
     * Tests the createNewAccount functionality.
     * 
     * @throws Exception
     */
    public void testCreateNewAccount() throws Exception {
    	String accountName = "JUnit Test Account";
        data.deleteAccount(accountName);
        Account account = new Account();
        account.setName(accountName);
        instance.createNewAccount(account);
        Account fetched = instance.getAccount(accountName);
        assertEquals(fetched.getName(), accountName);
    }
    
    
    public void testCreateNewRole() throws InvalidRoleException {
    	String roleName = "JUnit Test Role";
    	String description = "Short Description";
    	String type = "lab";
    	data.deleteRole(roleName);
    	
    	boolean exceptionThrown = false;
    	
		// Create a new lab role.
    	try {
			instance.createNewRole(roleName, description, type);
		} catch (InvalidTicketException e) {
			exceptionThrown = true;
		} catch (RoleDuplicateException e) {
			exceptionThrown = true;
		} catch (NotAuthorizedException e) {
			exceptionThrown = true;
		} catch (Exception e) {
			exceptionThrown = true;
		}
    	assertFalse(exceptionThrown);
    	
    	Role r = instance.getRole(roleName, type);
    	assertEquals(roleName, r.getName());
    	assertEquals(type, r.getType());
    	
    	// Clean up this tests database entries.
    	data.deleteRole(roleName);
    }
    
    public void testAccountDateManipulation() throws Exception {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2013, 0, 27, 13, 59, 00);
        SimpleDateFormat format = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date testDate = cal.getTime();
        assertEquals("2013-01-27 13:59:00", format.format(testDate));
        
        Account account = new Account();
        account.setBdate(testDate);
        org.opencoral.idl.Account idlAccount = account.convertToIdlAccountForRscMgr();
        String bdate = idlAccount.bdate.year + "-0" +
        		idlAccount.bdate.month + "-" +
        		idlAccount.bdate.day + " " +
        		idlAccount.bdate.hour + ":" +
        		idlAccount.bdate.minute + ":" +
        		idlAccount.bdate.second + "0" ;
        assertEquals( bdate, format.format(testDate));
    }
    

    public void testAccountEDateIsNullRoundTrip() throws Exception {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2013, 0, 27, 13, 59, 00);
        SimpleDateFormat format = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date testDate = cal.getTime();
        assertEquals("2013-01-27 13:59:00", format.format(testDate));
        
        String accountName = "JUnit Test Account";
        data.deleteAccount(accountName);
        Account account = new Account();
        account.setEdate(testDate);
        account.setName(accountName);
        instance.createNewAccount(account);
        Account fetched = instance.getAccount(accountName);
        assertEquals(fetched.getName(), accountName);
        
        // This tests coral's expected behavior (not ideal) that the edate is always set to null.
        assertEquals(null, fetched.getEdate());
        
        // Clean up this tests database entries.
        data.deleteAccount(accountName);
    }
    
    public void testAddProjectMembers() throws Exception {
    	String memberName = "JUnit User";
    	String projectName1 = "JUnit Test Project 1";
    	String projectName2 = "JUnit Test Project 2";
    	String accountName = "JUnit Test Account";
    	this.createTestAccount(accountName);
    	this.createTestProject(projectName1, accountName);
    	this.createTestProject(projectName2, accountName);

    	data.deleteMember(memberName);
    	Member member1 = new Member();
    	member1.setName(memberName);
    	member1.setProject(projectName1);
        instance.createNewMember(member1);
    	
    	String[] members = {memberName};
    	instance.addProjectMembers(projectName1, members);
    	instance.addProjectMembers(projectName2, members);
    	
    	Members fetchedMembers = instance.getProjectMembers(projectName1);
    	assertTrue(fetchedMembers.contains(member1));
    	
    	fetchedMembers = instance.getProjectMembers(projectName2);
    	assertTrue(fetchedMembers.contains(member1));
    	
    	// Clean up this tests database entries.
    	data.deleteMember(memberName);
    	data.deleteProject(projectName1);
    	data.deleteProject(projectName2);
    	data.deleteAccount(accountName);
    }
    
    /**
     * Tests the 'getMember' functionality.
     * 
     * @throws Exception
     */
    public void testGetMember() throws Exception {
        String memberName = "JUnit User";
        String projectName = "JUnit Test Project";
        String accountName = "JUnit Test Account";
    	this.createTestAccount(accountName);
    	this.createTestProject(projectName, accountName);
        
    	data.deleteMember(memberName);
    	Member member = new Member();
    	member.setName(memberName);
    	member.setProject(projectName);
        instance.createNewMember(member);
        
        Member fetched = instance.getMember(memberName);
        assertEquals(memberName, fetched.getName());
        
        // Clean up this tests database entries.
        data.deleteMember(memberName);
        data.deleteProject(projectName);
        data.deleteAccount(accountName);
    }
    
    public void testUnknownMember() throws Exception {
        String memberName = "unknown-member";
        data.deleteMember(memberName);
        
    	boolean exceptionThrown = false;
        try {
            instance.getMember(memberName);
        } catch (UnknownMemberException ex) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
    
    
    /**
     * Tests the 'isValidEmail' functionality for various valid email addresses.
     */
    public void testValidEmails() {
    	String email1 = "firstname@example.com";
    	boolean expected1 = true;
    	boolean actual1 = Utils.isValidEmailAddress(email1);
    	assertEquals(expected1, actual1);
    	
    	String email2 = "firstname.lastname@example.com";
    	boolean expected2 = true;
    	boolean actual2 = Utils.isValidEmailAddress(email2);
    	assertEquals(expected2, actual2);
    	
    	String email3 = "me@me.co.uk";
    	boolean expected3 = true;
    	boolean actual3 = Utils.isValidEmailAddress(email3);
    	assertEquals(expected3, actual3);
    	
    	String email4 = "me@1.com";
    	boolean expected4 = true;
    	boolean actual4 = Utils.isValidEmailAddress(email4);
    	assertEquals(expected4, actual4);    	
    }
    
    /**
     * Tests the 'isValidEmail' functionality for various invalid email addresses.
     */
    public void testInvalidEmails() {
    	String email1 = "me@.com";
    	boolean expected1 = false;
    	boolean actual1 = Utils.isValidEmailAddress(email1);
    	assertEquals(expected1, actual1);
    	
    	String email2 = "me@.com.my";
    	boolean expected2 = false;
    	boolean actual2 = Utils.isValidEmailAddress(email2);
    	assertEquals(expected2, actual2);
    	
    	String email3 = "me@me@example.com";
    	boolean expected3 = false;
    	boolean actual3 = Utils.isValidEmailAddress(email3);
    	assertEquals(expected3, actual3);  
    }

    /**
     * Tests the 'updatePassword' functionality.
     * 
     * @throws Exception 
     */
    public void testUpdatePassword() throws Exception {
    	String username = "JUnit User";
    	String password = "pass";
    	String project = "JUnit Test Project";
    	String account = "JUnit Test Account";
    	this.createTestAccount(account);
    	this.createTestProject(project, account);
    	this.createTestMember(username, password, project);
    	assertTrue(instance.authenticate(username, password));
    	
    	String newPassword = "new-pass";
    	instance.updatePassword(username, newPassword);
    	assertTrue(instance.authenticate(username, newPassword));	
    	assertFalse(instance.authenticate(username, password));
    	
    	// Clean up this tests database entries.
    	data.deleteMember(username);
    	data.deleteProject(project);
    	data.deleteAccount(account);
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
    
    
    /*
    public void testAddMemberProjects() throws Exception {
    	this.deleteMember("testmem_1");
    	this.deleteProject("testproject_1");
    	this.deleteProject("testproject_2");
    	System.out.println("Test Add Member Projects");
    	Member member = newMember("testmem_1");
    	CoralServices instance = new CoralServices();
    	instance.CreateNewMember(member);
    	
    	String[] projectNames = {"testproject_1", "testproject_2"};
    	Project project1 = newProject(projectNames[0]);
    	Project project2 = newProject(projectNames[1]);
    	instance.CreateNewProject(project1);
    	instance.CreateNewProject(project2);
    	
    	instance.AddMemberProjects("testmem_1", projectNames);
    }
    public void testAddEquipmentRoleToMember() throws Exception{
    	this.deleteMember("testmem_1");
    	System.out.println("Test Add Member Projects");
    	Member member = newMember("testmem_1");
    	CoralServices instance = new CoralServices();
    	instance.CreateNewMember(member);    	
    	instance.AddEquipmentRoleToMember("testmem_1", "user", "TMV Super");
    	instance.RemoveEquipmentRoleFromMember("testmem_1", "user", "TMP Super");
    }
    public void testAddProjectRoleToMember() throws Exception{
    	this.deleteMember("testmem_1");
    	System.out.println("Test Add Project Role To Member");
    	Member member = newMember("testmem_1");
    	CoralServices instance = new CoralServices();
    	instance.CreateNewMember(member);
    	instance.AddProjectRoleToMember("testmem_1", "pi", "Inventory");
    	instance.RemoveProjectRoleFromMember("testmem_1", "pi", "Inventory");
    }
    public void testEnable() {
    	CoralServices instance = new CoralServices();
    	instance.enable("TMV Super");
    }
    */
}

