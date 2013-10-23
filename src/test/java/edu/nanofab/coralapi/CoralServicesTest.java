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
    String projectTestName = "testproject_1";
    String memberTestName = "testmem_1";
    String allowedHostname = "vagrant-centos63-32";
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
        assertTrue("only allow tests to run on vm called" + allowedHostname, hostname.equals(allowedHostname));
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

    public Member newMember(String name) throws Exception{
        Member newMember = new Member();
        newMember.name = name;
        newMember.project = "Bootstrap project";
        return newMember;
    }
    public Project newProject(String name) throws Exception{
    	Project newProject = new Project();
    	newProject.account = "Bootstrap account";
    	newProject.name = name;
    	return newProject;
    }
    public void deleteMember(String name){
    	String query_ ="DELETE FROM rscmgr.member WHERE name='"+name+"'";
    	System.out.println(query_);
    	query(query_);
    }
    public void deleteProject(String name){
    	String query_ ="DELETE FROM rscmgr.project WHERE name='"+name+"'";
    	System.out.println(query_);
    	query(query_);
    }    
    public void query(String query_){
        Connection con = null;
        java.sql.Statement st = null;
    
        String url = "jdbc:postgresql://localhost/coral";
        String user = "coraldba";
        String password = "coraldba";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            st.execute(query_);

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(CoralServicesTest.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(CoralServicesTest.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
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
        this.deleteMember(this.memberTestName);
    	System.out.println("TESTING CREATING NEW MEMBER");
    	Member member = newMember(this.memberTestName);
        CoralServices instance = new CoralServices();
        instance.CreateNewMember(member);   
    }

    /**
     * Test of CreateNewProject method, of class CoralServices.
     */
    public void testCreateNewProject() throws Exception {
        this.deleteProject(this.projectTestName);
    	System.out.println("Test Create New Project");
        Project project = this.newProject(this.projectTestName);
        CoralServices instance = new CoralServices();
        instance.CreateNewProject(project);
    }
    public void testAddProjectMembers() throws Exception {
    	this.deleteMember("testmem_18");
    	this.deleteMember("testmem_19");    	
    	System.out.println("Test Add Project Members");
    	CoralServices instance = new CoralServices();
    	Member member1 = newMember("testmem_18");
    	Member member2 = newMember("testmem_19");
    	String[] members = {"testmem_18", "testmem_19"};
    	
    	instance.CreateNewMember(member1);
    	instance.CreateNewMember(member2);
    	
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
        this.deleteMember(this.memberTestName);
    	System.out.println("Test Get Member");
    	Member member = newMember(this.memberTestName);
        CoralServices instance = new CoralServices();
        instance.CreateNewMember(member);  
        Member mem = instance.getMember(this.memberTestName);
        Assert.assertEquals(this.memberTestName, mem.name);
    }
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
}

