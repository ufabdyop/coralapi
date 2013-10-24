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
import org.opencoral.idl.InvalidAccountSignal;
import org.opencoral.idl.InvalidAgentSignal;
import org.opencoral.idl.InvalidMemberSignal;
import org.opencoral.idl.InvalidProcessSignal;
import org.opencoral.idl.InvalidProjectSignal;
import org.opencoral.idl.InvalidResourceSignal;
import org.opencoral.idl.InvalidTicketSignal;
import org.opencoral.idl.Member;
import org.opencoral.idl.NotAuthorizedSignal;
import org.opencoral.idl.Project;
import org.opencoral.idl.ProjectNotFoundSignal;
import org.opencoral.idl.ResourceUnavailableSignal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    	int len = instance.getProjects().length;
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
    	member.name = "testuser";
    	member.project = "Bootstrap project";
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
        project.name = "Bootstrap project";
        project.account = "Bootstrap account";
        CoralServices instance = new CoralServices();
        instance.CreateNewProject(project);
    }
    
    public void testAddProjectMembers() throws Exception {
    	data.deleteMember("testmem_18");
    	System.out.println("Test Add Project Members");
    	CoralServices instance = new CoralServices();
    	Member member1 = new Member();
    	member1.name = "testmem_18";
    	member1.project = "Bootstrap project";
    	String[] members = {"testmem_18"};
        instance.CreateNewMember(member1);
    	instance.AddProjectMembers("Maintenance", members);
    	instance.RemoveProjectMembers("Maintenance", members);
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
    	member.name = "testuser";
    	member.project = "Bootstrap project";
        CoralServices instance = new CoralServices();
        instance.CreateNewMember(member);  
        Member mem = instance.getMember("testuser");
        Assert.assertEquals("testuser", mem.name);
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

