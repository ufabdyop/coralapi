package edu.nanofab.utah.coralapi.resource;

import java.util.Date;

import org.opencoral.corba.MemberAdapter;
import org.opencoral.idl.Timestamp;

import edu.nanofab.utah.coralapi.helper.TimestampConverter;

public class Member {
	private String name;
	private String address1;
	private String address2;
	private String advisor;
	private String altFax;
	private String altOffice;
	private String altPhone;
	private String city;
	private String disability;
	private String email;
	private String ethnicity;
	private String fax;
	private String firstName;
	private String lastName;
	private String mailCode;
	private String password;
	private String phone;
	private String project;
	private String race;
	private String state;
	private String type;
	private String univid;
	private String url;
	private String zipcode;
	private boolean active;
	private Date edate;
	
	public Member(org.opencoral.idl.Member idlMember) throws Exception {
		super();
		this.populateFromIdlMember(idlMember);
	}

	public Member() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAdvisor() {
		return advisor;
	}

	public void setAdvisor(String advisor) {
		this.advisor = advisor;
	}

	public String getAltFax() {
		return altFax;
	}

	public void setAltFax(String altFax) {
		this.altFax = altFax;
	}

	public String getAltOffice() {
		return altOffice;
	}

	public void setAltOffice(String altOffice) {
		this.altOffice = altOffice;
	}

	public String getAltPhone() {
		return altPhone;
	}

	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDisability() {
		return disability;
	}

	public void setDisability(String disability) {
		this.disability = disability;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMailCode() {
		return mailCode;
	}

	public void setMailCode(String mailCode) {
		this.mailCode = mailCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnivid() {
		return univid;
	}

	public void setUnivid(String univid) {
		this.univid = univid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setEdate(Date date) {
		this.edate = date;
	}

	public Date getEdate() {
		return this.edate;
	}	

	public void populateFromIdlMember(org.opencoral.idl.Member idlMember) throws Exception {
		this.setName(idlMember.name);
		this.setAddress1(idlMember.address1);
		this.setAddress2(idlMember.address2);
		this.setAdvisor(idlMember.advisor);
		this.setAltFax(idlMember.altFax);
		this.setAltOffice(idlMember.altOffice);
		this.setAltPhone(idlMember.altPhone);
		this.setCity(idlMember.city);
		this.setDisability(idlMember.disability);
		this.setEmail(idlMember.email);
		this.setEthnicity(idlMember.ethnicity);
		this.setFax(idlMember.fax);
		this.setFirstName(idlMember.firstName);
		this.setLastName(idlMember.lastName);
		this.setMailCode(idlMember.mailCode);
		this.setPassword(idlMember.password);
		this.setPhone(idlMember.phone);
		this.setProject(idlMember.project);
		this.setRace(idlMember.race);
		this.setState(idlMember.state);
		this.setType(idlMember.type);
		this.setUnivid(idlMember.univid);
		this.setUrl(idlMember.url);
		this.setZipcode(idlMember.zipcode);
		this.setActive(idlMember.active);
	    this.setEdate(this.timestampToDate(idlMember.edate));
	}
	
	private Date timestampToDate(Timestamp tstamp) {
		return TimestampConverter.timestampToDate(tstamp);
	}

	public boolean equals(Member m) {
		return m.name.equals(this.name);
	}
	
	/**
	 * rscmgr in coral expects an org.opencoral.idl object.  For some reason,
	 * this needs to come from a MemberAdapter that gets cast to idl object
	 * @return org.opencoral.idl.Member that can be used with rscmgr
	 * @throws Exception
	 */
	public org.opencoral.idl.Member convertToIDLMemberForRscMgr() throws Exception {
		 MemberAdapter memAP = this.convertToMemberAdapter();
         return (org.opencoral.idl.Member)memAP.getObject();
	}
	
	/**
	 * 
	 * Converts this Member object to a MemberAdapter
	 * @return org.opencoral.corba.MemberAdapter
	 * @throws Exception
	 */
	private MemberAdapter convertToMemberAdapter() throws Exception {
		MemberAdapter memAP = new MemberAdapter();
    	memAP.setValue("name", this.name);
    	if (this.address1 != null) memAP.setValue("address1", this.address1);
    	if (this.address2 != null) memAP.setValue("address2", this.address2);
    	if (this.advisor != null) memAP.setValue("advisor", this.advisor);
    	if (this.altFax != null) memAP.setValue("altFax", this.altFax);
    	if (this.altOffice != null) memAP.setValue("altOffice", this.altOffice);
    	if (this.altPhone != null) memAP.setValue("altPhone", this.altPhone);
    	if (this.city != null) memAP.setValue("city", this.city);
    	if (this.disability != null) memAP.setValue("disability", this.disability);
    	if (this.email != null) memAP.setValue("email", this.email);
    	if (this.ethnicity != null) memAP.setValue("ethnicity", this.ethnicity);
    	if (this.fax != null) memAP.setValue("fax", this.fax);
    	if (this.firstName != null) memAP.setValue("firstName", this.firstName);
    	if (this.lastName != null) memAP.setValue("lastName", this.lastName);
    	if (this.mailCode != null) memAP.setValue("mailCode", this.mailCode);
    	if (this.password != null) memAP.setValue("password", this.password);
    	if (this.phone != null) memAP.setValue("phone", this.phone);
    	if (this.project != null) memAP.setValue("project", this.project);
    	if (this.race != null) memAP.setValue("race", this.race);
    	if (this.state != null) memAP.setValue("state", this.state);
    	if (this.type != null) memAP.setValue("type", this.type);
    	if (this.univid != null) memAP.setValue("univid", this.univid);
    	if (this.url != null) memAP.setValue("url", this.url);
    	if (this.zipcode != null) memAP.setValue("zipcode", this.zipcode);  
    	memAP.setValue("active", (this.active == true)?"true":"false");
		if (this.edate != null) memAP.setValue("edate", TimestampConverter.dateToAdapterString(this.edate));
		return memAP;
	}
}
