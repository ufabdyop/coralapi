package edu.utah.nanofab.coralapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.opencoral.idl.AccountNotFoundSignal;
import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidMemberSignal;
import org.opencoral.idl.InvalidRoleSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.ProjectNotFoundSignal;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CoralServicesTest extends TestCase {
    FixtureHelper data = new FixtureHelper();
    protected String allowedHostname = "vagrant-centos63-32";
	private CoralAPI instance;
	public CoralServicesTest(String testName) {
        super(testName);
    }
    
    protected void guardAgainstRunningOnLive() throws Exception {
    	this._guardAgainstRunningOnLive();
    }
    
    protected void _guardAgainstRunningOnLive() throws Exception {
        Process results = Runtime.getRuntime().exec("hostname");
        InputStream stdout = results.getInputStream();
        BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
        String hostname = reader.readLine();
        if (hostname.equals(allowedHostname)) {
        } else {
        	System.err.println("Hostname should be " + allowedHostname + "!");
        }
        assertTrue("only allow tests to run on vm called " + allowedHostname, hostname.equals(allowedHostname));
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        guardAgainstRunningOnLive();
    	this.instance = new CoralAPI("coral", "http://vagrant-coral-dev/IOR/", "http://vagrant-coral-dev/coral/lib/config.jar");
    	Account a = new Account();
    	a.setName("JUnit Testing Account" );
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
        assertEquals(member.getPassword(), fetched.getPassword());
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
    
    //how to use this test???
    public void testAuthentication() {
    	boolean password_check = instance.authenticate("coral", "coral");
    	assertTrue(password_check);
    	
        boolean failed_check = instance.authenticate("testuser", "fakepass");
    	assertFalse(failed_check);
        
        boolean invalid_auth = instance.authenticate(null, null);
        assertFalse(invalid_auth);
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
    	Member member = new Member();
    	member.setName("testuser");
    	member.setProject("JUnit Testing Project");
        instance.createNewMember(member);  
        Member mem = instance.getMember("testuser");
        Assert.assertEquals("testuser", mem.getName());
    }
    
    @Test(expected=UnknownMemberException.class)
    public void testUnknownMember() throws Exception {
        instance.getMember("unknown_member");
    }
    
    public void testGetLabRoles() throws Exception {
        data.deleteMember("testuser");
    	System.out.println("Test Get Member");
    	Member member = new Member();
    	member.setName("testuser");
    	member.setProject("JUnit Testing Project");
        instance.createNewMember(member);
        LabRole newRole = new LabRole("nano", member.getName(), "staff" );
        instance.addLabRole( newRole );
        LabRoles roles = instance.getLabRoles(member.getName());
        assertTrue( roles.contains(newRole) );
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

