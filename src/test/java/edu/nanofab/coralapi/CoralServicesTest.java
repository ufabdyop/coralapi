/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nanofab.coralapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.opencoral.corba.MemberAdapter;
import org.opencoral.corba.ProjectAdapter;
import org.opencoral.idl.Account;
import org.opencoral.idl.AccountNotFoundSignal;
import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidAgentSignal;
import org.opencoral.idl.InvalidMemberSignal;
import org.opencoral.idl.InvalidProcessSignal;
import org.opencoral.idl.InvalidProjectSignal;
import org.opencoral.idl.InvalidResourceSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.ProjectNotFoundSignal;
import org.opencoral.idl.ResourceUnavailableSignal;

import edu.nanofab.coralapi.collections.Members;
import edu.nanofab.coralapi.resource.Member;
import edu.nanofab.coralapi.resource.Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Quota.Resource;
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
    /**
     * Test of CreateNewMember method, of class CoralServices.
     */
    public void testCreateNewMember() throws Exception {
        data.deleteMember("testuser");
    	System.out.println("TESTING CREATING NEW MEMBER");
    	Member member = new Member();
    	member.setName("testuser");
    	member.setProject( "Bootstrap project" );
        CoralServices instance = new CoralServices();
        instance.CreateNewMember(member);
    }

    /**
     * Test of CreateNewProject method, of class CoralServices.
     */
    public void testCreateNewProject() throws Exception {
        data.deleteProject("Bootstrap project");
    	System.out.println("Test Create New Project");
        Project project = new Project();
        project.setName("Bootstrap project");
        project.setAccount("Bootstrap account");
        CoralServices instance = new CoralServices();
        instance.CreateNewProject(project);
        Project fetched = instance.getProject(project.getName());
        assertEquals(fetched.getName(), project.getName());
    }
    
    public void testGetProjectThrowsExceptionForMissingProject() {
    	boolean exceptionThrown = false;
    	try {
	    	data.deleteProject("Bootstrap project");
	    	CoralServices instance = new CoralServices();
	    	instance.getProject("Bootstrap project");
    	} catch (ProjectNotFoundSignal e) {
    		exceptionThrown = true;
    	}
    	assertTrue(exceptionThrown);
    }

    public void testGetAccountThrowsExceptionForMissingProject() {
    	boolean exceptionThrown = false;
    	try {
	    	data.deleteAccount("test unit account");
	    	CoralServices instance = new CoralServices();
	    	instance.getAccount("test unit account");
    	} catch (InvalidAccountSignal e) {
    		exceptionThrown = true;
		}
    	assertTrue(exceptionThrown);
    }

    
    /**
     * Test of CreateNewAccount method, of class CoralServices.
     */
    public void testCreateNewAccount() throws Exception {
        data.deleteAccount("unit test account");
        Account account = new Account();
        account.name = "unit test account";
        CoralServices instance = new CoralServices();
        instance.CreateNewAccount(account);
        Account fetched = instance.getAccount(account.name);
        assertEquals(fetched.name, account.name);
    }
    
    
    public void testAddProjectMembers() throws Exception {
    	data.deleteMember("testmem_18");
    	data.deleteProject("Bootstrap project");
    	CoralServices instance = new CoralServices();
    	Member member1 = new Member();
    	member1.setName("testmem_18");
    	member1.setProject("Bootstrap project");
    	String[] members = {"testmem_18"};
        Project p = new Project();
        p.setName("Bootstrap project");
        p.setAccount("Bootstrap account");
        instance.CreateNewProject(p);
        instance.CreateNewMember(member1);
    	instance.AddProjectMembers("Bootstrap project", members);
    	Members fetchedMembers = instance.GetProjectMembers("Bootstrap project");
    	assertTrue(fetchedMembers.contains(member1));
    	//instance.RemoveProjectMembers("Bootstrap project", members);
    }
    
    public void testRemoveProjectMembers() throws Exception {
    	System.out.println("Test Remove Project Members");
    	String[] members = {"testmem_10"};
    	CoralServices instance = new CoralServices();
    	try {
    		instance.RemoveProjectMembers("Bootstrap project", members);
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
    
    public void testGetMember() throws Exception {
        data.deleteMember("testuser");
    	System.out.println("Test Get Member");
    	Member member = new Member();
    	member.setName("testuser");
    	member.setProject("Bootstrap project");
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

