package edu.utah.nanofab.coralapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.opencoral.idl.Member;
import org.opencoral.idl.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixtureHelper {

	private String dbhost = "";
	private String dbuser = "";
	private String dbpass = "";
	private static final Logger logger = LoggerFactory.getLogger(FixtureHelper.class);
	
	/**
	 * Constructs a FixtureHelper with the supplied connection information.
	 * 
	 * @param dbhost The host where the SQL server exists.
	 * @param dbuser The username for the SQL server.
	 * @param dbpass The password for the supplied username.
	 */
	public FixtureHelper(String dbhost, String dbuser, String dbpass) {
		this.setDbhost(dbhost);
		this.setDbuser(dbuser);
		this.setDbpass(dbpass);
	}
	
	/**
	 * Constructs a FixtureHelper without any default connection information.
	 */
	public FixtureHelper() {
		
	}

    public Member newMember(String name) throws Exception{
        Member newMember = new Member();
        newMember.name = name;
        newMember.project = "JUnit Testing Project";
        return newMember;
    }
    public Project newProject(String name) throws Exception{
    	Project newProject = new Project();
    	newProject.account = "JUnit Testing Account";
    	newProject.name = name;
    	return newProject;
    }
    public void deleteMember(String name){
    	String query_ ="DELETE FROM rscmgr.member WHERE name='"+name+"'";
    	query(query_);
    }
    public void deleteProject(String name){
    	String query_ ="DELETE FROM rscmgr.project WHERE name='"+name+"'";
    	query(query_);
    }
    
    public void query(String query_){
        Connection con = null;
        java.sql.Statement st = null;

        try {
            con = DriverManager.getConnection(this.dbhost, this.dbuser, this.dbpass);
            st = con.createStatement();
            st.execute(query_);

        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);

        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
    }

	public void deleteAccount(String name) {
    	String query_ ="DELETE FROM rscmgr.account WHERE name='"+name+"'";
    	query(query_);
	}
	
	public void deleteRole(String name) {
		String query_ = "DELETE FROM rscmgr.role WHERE name='" + name + "'";
		query(query_);
	}

	public String getDbhost() {
		return dbhost;
	}

	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}

	public String getDbuser() {
		return dbuser;
	}

	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}

	public String getDbpass() {
		return dbpass;
	}

	public void setDbpass(String dbpass) {
		this.dbpass = dbpass;
	}

	public void deleteReservation(String item, String bdate, String edate) {
		String query_ ="DELETE FROM resmgr.reservation WHERE item='"+item+"' "
				+ "AND (bdate, edate) OVERLAPS ('" + bdate +"'::timestamp, '"
						+ edate + "'::timestamp)";
		query(query_);		
	}
}
