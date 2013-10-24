package edu.nanofab.coralapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencoral.idl.Member;
import org.opencoral.idl.Project;

public class FixtureHelper {

	public FixtureHelper() {
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
            Logger lgr = Logger.getLogger(FixtureHelper.class.getName());
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
                Logger lgr = Logger.getLogger(FixtureHelper.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

	public void deleteAccount(String name) {
    	String query_ ="DELETE FROM rscmgr.account WHERE name='"+name+"'";
    	System.out.println(query_);
    	query(query_);
	}
}