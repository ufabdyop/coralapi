package edu.utah.nanofab.coralapi;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
import edu.utah.nanofab.coralapi.exceptions.UnknownMemberException;
import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.helper.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.opencoral.idl.AccountNotFoundSignal;
import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.ProjectNotFoundSignal;

public class CoralAPITest extends TestCase {
	
	String dbhost = "jdbc:postgresql://localhost/coral";
	String dbuser = "coraldba";
	String dbpass = "coraldba";
    FixtureHelper data = new FixtureHelper(dbhost, dbuser, dbpass);
    
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
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        guardAgainstRunningOnLive();
        
        String coralUser = "coral";
        String iorUrl = "http://vagrant-coral-dev/IOR/";
        String configUrl = "http://vagrant-coral-dev/coral/lib/config.jar";
    	this.instance = new CoralAPI(coralUser, iorUrl, configUrl);
    	
    	Account a = new Account();
    	a.setName("JUnit Testing Account");
    	
    	Project p = new Project();
    	p.setName("JUnit Testing Project");
    	p.setAccount("JUnit Testing Account" );
    	instance.createNewAccountUnlessExists(a);
    	instance.createNewProjectUnlessExists(p);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGetProjects() throws ProjectNotFoundSignal{
    	System.out.println("Get ALL PROJECTS");
    	int len = instance.getProjects().size();
    	System.out.println("Number of projects: "+ len);
    	assertTrue(len > 0);
    }
    
    public void testGetMemberProjects() throws Exception {
    	System.out.println("Get Member PROJECTS");
    	String memberName = "JUnit Testing Member";
    	data.deleteMember(memberName);
    	
    	Project project1 = new Project();
    	project1.setName("JUnit Testing Project 1");
    	project1.setAccount("JUnit Testing Account");
    	instance.createNewProjectUnlessExists(project1);
    	
    	Project project2 = new Project();
    	project2.setName("JUnit Testing Project 2");
    	project2.setAccount("JUnit Testing Account");
    	instance.createNewProjectUnlessExists(project2);
    	
    	Member member = new Member();
    	member.setName(memberName);
    	member.setProject(project1.getName());
    	
    	String[] projectList = {project1.getName(), project2.getName()};
    	instance.createNewMember(member);
    	System.out.println("Added member: " + memberName);
    	instance.addMemberProjects(memberName, projectList);
    	
    	Projects memberProjects = instance.getMemberProjects(memberName);
    	int numProjects = memberProjects.size();
    	System.out.println("Number of projects: "+ numProjects);
    	assertTrue(numProjects == 2);
    }

    public void testGetAccounts() throws AccountNotFoundSignal {
    	System.out.println("Get ALL accounts");
    	int len = instance.getAccounts().size();
    	System.out.println("Number of accounts: "+ len);
    	assertTrue(len > 0);
    }

    /**
     * Test of CreateNewMember method, of class CoralServices.
     */
    public void testCreateNewMemberRoundTrip() throws Exception {
        data.deleteMember("mytest02");
    	System.out.println("TESTING CREATING NEW MEMBER");
    	Member member = new Member();
    	member.setName("mytest02");
    	member.setProject( "JUnit Testing Project" );
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
        Member fetched = instance.getMember("mytest02");
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
    }

    /**
     * Test of CreateNewProject method, of class CoralServices.
     * @throws Exception 
     */
    public void testCreateNewProject() throws Exception {
        try {
			data.deleteProject("JUnit Testing Project");
		} catch (Exception e) {
			//ignore in test
		}
    	System.out.println("Test Create New Project");
    	
        Account acct = new Account();
        acct.setName("JUnit Testing Account");
        instance.createNewAccountUnlessExists(acct);
        
        Project project = new Project();
        project.setName("JUnit Testing Project");
        project.setAccount(acct.getName());
        project.setDescription("b");
        project.setDiscipline("c");
        project.setNickname("e");
        project.setPi("f");
        project.setType("g");
        instance.createNewProject(project);
        
        Project fetched = instance.getProject(project.getName());
        assertEquals(fetched.getName(), project.getName());
        assertEquals(fetched.getDescription(), project.getDescription());
        assertEquals(fetched.getDiscipline(), project.getDiscipline());
        assertEquals(fetched.getNickname(), project.getNickname());
        assertEquals(fetched.getPi(), project.getPi());
        assertEquals(fetched.getType(), project.getType());
    }
    
    public void testCreateProjectThenUpdateIt() throws Exception {
    	this.testCreateNewProject();
    	Project project = instance.getProject("JUnit Testing Project");
    	project.setNickname("new nickname");
    	instance.updateProject(project);
    	Project fetched = instance.getProject("JUnit Testing Project");
    	assertEquals(fetched.getNickname(), "new nickname");
    }
    
    public void testCreateAccountThenUpdateIt() throws Exception {
    	this.testCreateNewAccount();
    	Account a = instance.getAccount("JUnit Testing Account");
    	a.setDescription("here is a description");
    	instance.updateAccount(a);
    	Account fetched = instance.getAccount("JUnit Testing Account");
    	assertEquals(fetched.getDescription(), "here is a description");
    }
    
    public void testCreateMemberThenUpdateIt() throws Exception {
    	this.testCreateNewMemberRoundTrip();
    	Member m = instance.getMember("mytest02");
    	m.setAddress1("here is an address");
    	instance.updateMember(m);
    	Member fetched = instance.getMember("mytest02");
    	assertEquals(fetched.getAddress1(), "here is an address");
    }
        
    public void testGetProjectThrowsExceptionForMissingProject() throws InvalidTicketSignal, NotAuthorizedSignal, Exception {
    	boolean exceptionThrown = false;
    	try {
	    	data.deleteProject("JUnit Testing Project");
	    	instance.getProject("JUnit Testing Project");
    	} catch (ProjectNotFoundSignal e) {
    		exceptionThrown = true;
    	}
    	assertTrue(exceptionThrown);
    }

    public void testGetAccountThrowsExceptionForMissingProject() {
    	boolean exceptionThrown = false;
    	try {
	    	data.deleteAccount("JUnit Testing Account");
	    	instance.getAccount("JUnit Testing Account");
    	} catch (InvalidAccountSignal e) {
    		exceptionThrown = true;
		}
    	assertTrue(exceptionThrown);
    }
    
    public void testAuthentication() throws Exception {
    	// Create a new member for this test.
    	data.deleteMember("user");
    	Member member = new Member();
    	member.setName("user");
    	member.setPassword("pass");
    	member.setProject("JUnit Testing Project");
    	member.setActive(true);
    	instance.createNewMember(member);
    	
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
        
        // Delete the created member for cleanup purposes.
        data.deleteMember("user");
    }
    
    /**
     * Test of CreateNewAccount method, of class CoralServices.
     */
    public void testCreateNewAccount() throws Exception {
        data.deleteAccount("JUnit Testing Account");
        Account account = new Account();
        account.setName("JUnit Testing Account");
        instance.createNewAccount(account);
        Account fetched = instance.getAccount(account.getName());
        assertEquals(fetched.getName(), account.getName());
    }
    
    /**
     * Test of createNewReservation method, of class CoralServices.
     */
    public void testCreateNewReservation() throws Exception {
        String accountName = "JUnit Test Account";
        String projectName = "JUnit Test Project";
        String user = "user";
        String pass = "pass";
    	Account account = this.createTestAccount(accountName);
        Project project = this.createTestProject(projectName, accountName);
        Member member = this.createTestMember(user, pass, projectName);
    	
        data.deleteReservation("TMV Super", "2099-01-01 12:00:00", "2099-01-01 13:00:00");

        Reservation r = new Reservation();
        r.setItem("TMV Super");
        r.setBdate(2099,1,1,12,0);
        r.setEdate(2099,1,1,13,0);
        r.setMember(member);
        r.setProject(project);
        r.setLab("nano");
        r.setAccount(account);
        instance.createNewReservation(r);
        
        Reservation fetched = instance.getReservation("TMV Super", "2099-01-01 12:00:00", "2099-01-01 13:00:00");
        assertEquals(fetched.getMember().getName(), r.getMember().getName());
    }
    
    public void testAccountDateManipulation() throws Exception {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2013, 0, 27, 13, 59, 00);
        SimpleDateFormat format = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date testDate = cal.getTime();
        assertEquals("2013-01-27 13:59:00", format.format(testDate));
        data.deleteAccount("JUnit Testing Account");
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
    
    /**
     * This test only passes with modified opencoral source to allow edates to be set.
     * @throws Exception
     */
    public void testProjectDateManipulationRoundTrip() throws Exception {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2099, 0, 27, 13, 59, 00);
        SimpleDateFormat format = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date testDate = cal.getTime();
        assertEquals("2099-01-27 13:59:00", format.format(testDate));
        data.deleteAccount("JUnit Testing Account2");
        Account account = new Account();
        account.setEdate(testDate);
        account.setName("JUnit Testing Account2");
        instance.createNewAccount(account);
        
        data.deleteProject("test project edates");
        Project project = new Project();
        project.setAccount("JUnit Testing Account2");
        project.setName("test project edates");
        project.setEdate(testDate);
        instance.createNewProject(project);
        
        Project fetched = instance.getProject(project.getName());
        assertEquals(fetched.getName(), project.getName());
        assertEquals(project.getEdate().toString(), fetched.getEdate().toString());

        //test updateProject too
    	cal.set(2199, 1, 28, 14, 57, 01);
    	project.setEdate(cal.getTime());
    	instance.updateProject(project);
        fetched = instance.getProject(project.getName());
        assertEquals(fetched.getName(), project.getName());
        assertEquals(project.getEdate().toString(), fetched.getEdate().toString());
    }

    public void testMemberDateManipulationRoundTrip() throws Exception {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2099, 0, 27, 13, 59, 00);
        SimpleDateFormat format = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date testDate = cal.getTime();
        assertEquals("2099-01-27 13:59:00", format.format(testDate));
        Member member = new Member();
        member.setProject("JUnit Testing Project");
        member.setName("testmm01");
        member.setEdate(testDate);
        data.deleteMember("testmm01");
        instance.createNewMember(member);
        
        Member fetched = instance.getMember(member.getName());
        assertEquals(fetched.getName(), member.getName());
        assertEquals(member.getEdate().toString(), fetched.getEdate().toString());
    }    
    
    public void testAccountEDateIsNullRoundTrip() throws Exception {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2013, 0, 27, 13, 59, 00);
        SimpleDateFormat format = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date testDate = cal.getTime();
        assertEquals("2013-01-27 13:59:00", format.format(testDate));
        data.deleteAccount("JUnit Testing Account2");
        Account account = new Account();
        account.setEdate(testDate);
        account.setName("JUnit Testing Account2");
        instance.createNewAccount(account);
        Account fetched = instance.getAccount(account.getName());
        assertEquals(fetched.getName(), account.getName());
        assertEquals("This tests coral's expected behavior (not ideal) that the edate is always set to null", 
        		null, fetched.getEdate());
    }
    
    public void testAddProjectMembers() throws Exception {
    	data.deleteMember("testmem_18");
    	data.deleteProject("JUnit Testing Project");
    	data.deleteProject("JUnit Testing Project2");

        Project p = new Project();
        p.setName("JUnit Testing Project");
        p.setAccount("JUnit Testing Account");    	
        instance.createNewProject(p);

        Project p2 = new Project();
        p2.setName("JUnit Testing Project2");
        p2.setAccount("JUnit Testing Account");    	
        instance.createNewProject(p2);
    	
    	Member member1 = new Member();
    	member1.setName("testmem_18");
    	member1.setProject("JUnit Testing Project");
        instance.createNewMember(member1);
    	
    	String[] members = {"testmem_18"};
    	instance.addProjectMembers("JUnit Testing Project", members);
    	instance.addProjectMembers("JUnit Testing Project2", members);
    	
    	Members fetchedMembers = instance.getProjectMembers("JUnit Testing Project");
    	assertTrue(fetchedMembers.contains(member1));
    	
    	fetchedMembers = instance.getProjectMembers("JUnit Testing Project2");
    	assertTrue(fetchedMembers.contains(member1));
    }
    
    public void testRemoveProjectMembers() throws Exception {
    	System.out.println("Test Remove Project Members");
    	String[] members = {"testmem_10"};
    	try {
    		instance.removeProjectMembers("JUnit Testing Project", members);
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
    
    public void testGetMember() throws Exception {
        data.deleteMember("testuser");
    	System.out.println("Test Get Member");
    	data.deleteMember("testuser");
    	
    	Member member = new Member();
    	member.setName("testuser");
    	member.setProject("JUnit Testing Project");
        instance.createNewMember(member);  
        Member mem = instance.getMember("testuser");
        assertEquals("testuser", mem.getName());
    }
    
    public void testUnknownMember() throws Exception {
        boolean exceptionCaught = false;
        try {
            instance.getMember("unknown_member");
        } catch (UnknownMemberException ex) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }
    
    public void testGetLabRoles() throws Exception {
        // Create a member for this test.
    	data.deleteMember("user");
    	System.out.println("Test Get Member");
    	Member member = new Member();
    	member.setName("user");
    	member.setProject("JUnit Testing Project");
    	member.setActive(true);
        instance.createNewMember(member);
        
        LabRole newRole = new LabRole("nano", member.getName(), "staff" );
        instance.addLabRole(newRole);
        LabRoles roles = instance.getLabRoles(member.getName());
        assertTrue(roles.contains(newRole));
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
    	String username = "user";
    	String password = "pass";
    	String project = "JUnit Testing Project";
    	this.createTestMember(username, password, project);
    	
    	String newPassword = "new-pass";
    	instance.updatePassword(username, newPassword);
    	assertTrue(instance.authenticate(username, newPassword));	
    	assertFalse(instance.authenticate(username, password));
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

