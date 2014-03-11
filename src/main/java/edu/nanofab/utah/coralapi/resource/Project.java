package edu.nanofab.utah.coralapi.resource;

import java.util.Calendar;
import java.util.Date;

import org.opencoral.corba.ProjectAdapter;
import org.opencoral.idl.Timestamp;

import edu.nanofab.utah.coralapi.helper.TimestampConverter;

public class Project {
    boolean isNull;
    String name;
    String description;
    String nickname;
    String type;
    String discipline;
    String pi;
    String account;
    boolean active;
    Date bdate;
    Date edate;
    
	public boolean isNull() {
		return isNull;
	}
	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDiscipline() {
		return discipline;
	}
	public void setDiscipline(String discipline) {
		this.discipline = discipline;
	}
	public String getPi() {
		return pi;
	}
	public void setPi(String pi) {
		this.pi = pi;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Date getBdate() {
		return bdate;
	}
	public void setBdate(Date bdate) {
		this.bdate = bdate;
	}
	public Date getEdate() {
		return edate;
	}
	public void setEdate(Date edate) {
		this.edate = edate;
	}
    private org.opencoral.corba.ProjectAdapter toProjectAdapter() throws Exception {
    	ProjectAdapter proAdapter = new ProjectAdapter();
    	//proAdapter.setValue("account", this.account);
    	if (this.account != null) proAdapter.setValue("account", this.account);
    	if (this.description != null) proAdapter.setValue("description", this.description);
    	if (this.discipline != null) proAdapter.setValue("discipline", this.discipline);
    	if (this.name != null) proAdapter.setValue("name", this.name);
    	if (this.nickname != null) proAdapter.setValue("nickname", this.nickname);
    	if (this.pi != null) proAdapter.setValue("pi", this.pi);
    	if (this.type != null) proAdapter.setValue("type", this.type);
		if (this.bdate != null) proAdapter.setValue("bdate", TimestampConverter.dateToAdapterString(this.bdate));
		if (this.edate != null) proAdapter.setValue("edate", TimestampConverter.dateToAdapterString(this.edate));
    	proAdapter.setValue("active", (this.active)?"true":"false");
    	return proAdapter;
    }
	public org.opencoral.idl.Project convertToIdlProjectForRscMgr() throws Exception {
		org.opencoral.corba.ProjectAdapter projectAdapter = this.toProjectAdapter();
		org.opencoral.idl.Project idlProject = (org.opencoral.idl.Project)projectAdapter.getObject(); 
		return idlProject;
	}
	public void populateFromIdlProject(org.opencoral.idl.Project idlProject) {
		this.setName(idlProject.name);
	    this.setDescription(idlProject.description);
	    this.setNickname(idlProject.nickname);
	    this.setType(idlProject.type);
	    this.setDiscipline(idlProject.discipline);
	    this.setPi(idlProject.pi);
	    this.setAccount(idlProject.account);
	    this.setActive(idlProject.active);
	    this.setBdate(this.timestampToDate(idlProject.bdate));
	    this.setEdate(this.timestampToDate(idlProject.edate));
	}
	private Timestamp dateToTimestamp(Date bdate2) {
		return TimestampConverter.dateToTimestamp(bdate2);
	}
	private Date timestampToDate(Timestamp tstamp) {
		return TimestampConverter.timestampToDate(tstamp);
	}
}
