/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nanofab.coralapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.opencoral.idl.AccountNotFoundSignal;
import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.ProjectNotFoundSignal;

import edu.nanofab.coralapi.collections.Members;
import edu.nanofab.coralapi.resource.Account;
import edu.nanofab.coralapi.resource.Member;
import edu.nanofab.coralapi.resource.Project;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author neil
 */
public class CoralServicesTest extends TestCase {
    FixtureHelper data = new FixtureHelper();
    protected String allowedHostname = "vagrant-centos63-32";
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
    	CoralServices instance = new CoralServices();
    	Account a = new Account();
    	a.setName("JUnit Testing Account" );
    	Project p = new Project();
    	p.setName("JUnit Testing Project");
    	instance.CreateNewAccountUnlessExists(a);
    	instance.CreateNewProjectUnlessExists(p);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void testGetProjects() throws ProjectNotFoundSignal{
    	System.out.println("Get ALL PROJECTS");
    	CoralServices instance = new CoralServices();
    	int len = instance.getProjects().size();
    	System.out.println("Number of projects: "+ len);
    	assertTrue(len > 0);
    }

    public void testGetAccounts() throws AccountNotFoundSignal {
    	System.out.println("Get ALL accounts");
    	CoralServices instance = new CoralServices();
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
        CoralServices instance = new CoralServices();
        instance.CreateNewMember(member);
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
        CoralServices instance = new CoralServices();
        try {
			data.deleteProject("JUnit Testing Project");
		} catch (Exception e) {
			//ignore in test
		}
    	System.out.println("Test Create New Project");
    	
        Account acct = new Account();
        acct.setName("JUnit Testing Account");
        instance.CreateNewAccountUnlessExists(acct);
        
        Project project = new Project();
        project.setName("JUnit Testing Project");
        project.setAccount(acct.getName());
        project.setDescription("b");
        project.setDiscipline("c");
        project.setNickname("e");
        project.setPi("f");
        project.setType("g");
        instance.CreateNewProject(project);
        
        Project fetched = instance.getProject(project.getName());
        assertEquals(fetched.getName(), project.getName());
        assertEquals(fetched.getDescription(), project.getDescription());
        assertEquals(fetched.getDiscipline(), project.getDiscipline());
        assertEquals(fetched.getNickname(), project.getNickname());
        assertEquals(fetched.getPi(), project.getPi());
        assertEquals(fetched.getType(), project.getType());
    }
    
    public void testGetProjectThrowsExceptionForMissingProject() throws InvalidTicketSignal, NotAuthorizedSignal, Exception {
    	boolean exceptionThrown = false;
    	try {
	    	CoralServices instance = new CoralServices();
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
	    	CoralServices instance = new CoralServices();
	    	instance.getAccount("JUnit Testing Account");
    	} catch (InvalidAccountSignal e) {
    		exceptionThrown = true;
		}
    	assertTrue(exceptionThrown);
    }

    
    /**
     * Test of CreateNewAccount method, of class CoralServices.
     */
    public void testCreateNewAccount() throws Exception {
        data.deleteAccount("JUnit Testing Account");
        Account account = new Account();
        account.setName("JUnit Testing Account");
        CoralServices instance = new CoralServices();
        instance.CreateNewAccount(account);
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
     * This test would be nice, but the coral ResourceManagerImpl class
     * doesn't allow you to set edate.
     * @throws Exception
     */
    public void _testAccountDateManipulationRoundTrip() throws Exception {
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
        CoralServices instance = new CoralServices();
        instance.CreateNewAccount(account);
        Account fetched = instance.getAccount(account.getName());
        assertEquals(fetched.getName(), account.getName());
        assertEquals(fetched.getEdate(), account.getEdate());
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
        CoralServices instance = new CoralServices();
        instance.CreateNewAccount(account);
        Account fetched = instance.getAccount(account.getName());
        assertEquals(fetched.getName(), account.getName());
        assertEquals("This tests coral's expected behavior (not ideal) that the edate is always set to null", 
        		null, fetched.getEdate());
    }
    
    public void testAddProjectMembers() throws Exception {
    	data.deleteMember("testmem_18");
    	data.deleteProject("JUnit Testing Project");
    	CoralServices instance = new CoralServices();
    	Member member1 = new Member();
    	member1.setName("testmem_18");
    	member1.setProject("JUnit Testing Project");
    	String[] members = {"testmem_18"};
        Project p = new Project();
        p.setName("JUnit Testing Project");
        p.setAccount("JUnit Testing Account");
        instance.CreateNewProject(p);
        instance.CreateNewMember(member1);
    	instance.AddProjectMembers("JUnit Testing Project", members);
    	Members fetchedMembers = instance.GetProjectMembers("JUnit Testing Project");
    	assertTrue(fetchedMembers.contains(member1));
    	//instance.RemoveProjectMembers("JUnit Testing Project", members);
    }
    
    public void testRemoveProjectMembers() throws Exception {
    	System.out.println("Test Remove Project Members");
    	String[] members = {"testmem_10"};
    	CoralServices instance = new CoralServices();
    	try {
    		instance.RemoveProjectMembers("JUnit Testing Project", members);
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
        CoralServices instance = new CoralServices();
        instance.CreateNewMember(member);  
        Member mem = instance.getMember("testuser");
        Assert.assertEquals("testuser", mem.getName());
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

