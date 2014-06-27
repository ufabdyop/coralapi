package edu.utah.nanofab.coralapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencoral.idl.Member;
import org.opencoral.idl.Project;

public class FixtureHelper {

	private String dbhost = "";
	private String dbuser = "";
	private String dbpass = "";
	
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

        try {
            con = DriverManager.getConnection(this.dbhost, this.dbuser, this.dbpass);
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

	public void deleteReservation(String item, String bdate, String edate) {
		String query_ ="DELETE FROM resmgr.reservation WHERE item='"+item+"' "
				+ "AND (bdate, edate) OVERLAPS ('" + bdate +"'::timestamp, '"
						+ edate + "'::timestamp)";
		System.out.println(query_);
		query(query_);		
	}
}
