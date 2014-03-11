package edu.nanofab.utah.coralapi.resource;

import java.util.Date;

import org.opencoral.corba.AccountAdapter;
import org.opencoral.corba.ProjectAdapter;
import org.opencoral.idl.Timestamp;

import edu.nanofab.utah.coralapi.helper.TimestampConverter;

public class Account {
		String name;
		String description;
		String organization;
		String type;
		boolean active;
		Date bdate;
		Date edate;
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
		public String getOrganization() {
			return organization;
		}
		public void setOrganization(String organization) {
			this.organization = organization;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
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
		public void populateFromIdlAccount(org.opencoral.idl.Account acct) {
			this.setName(acct.name);
			this.setDescription(acct.description);
			this.setOrganization(acct.organization);
			this.setType(acct.type);
			this.setActive(acct.active);
			this.setBdate(TimestampConverter.timestampToDate(acct.bdate));
			this.setEdate(TimestampConverter.timestampToDate(acct.edate));
		}
		private Timestamp dateToTimestamp(Date bdate2) {
			return TimestampConverter.dateToTimestamp(bdate2);
		}
		private Date timestampToDate(Timestamp tstamp) {
			return TimestampConverter.timestampToDate(tstamp);
		}
		private org.opencoral.corba.AccountAdapter toAccountAdapter() throws Exception {
			AccountAdapter proAdapter = new AccountAdapter();
			if (this.name != null) proAdapter.setValue("name", this.name);
			if (this.description != null) proAdapter.setValue("description", this.description);
			if (this.organization != null) proAdapter.setValue("organization", this.organization);
			if (this.type != null) proAdapter.setValue("type", this.type);
			if (this.bdate != null) proAdapter.setValue("bdate", TimestampConverter.dateToAdapterString(this.bdate));
			if (this.edate != null) proAdapter.setValue("edate", TimestampConverter.dateToAdapterString(this.edate));
			
			proAdapter.setValue("active", (this.active)?"true":"false");
			return proAdapter;
		}
		public org.opencoral.idl.Account convertToIdlAccountForRscMgr() throws Exception {
			org.opencoral.corba.AccountAdapter accountAdapter = this.toAccountAdapter();
			org.opencoral.idl.Account idlAccount = (org.opencoral.idl.Account)accountAdapter.getObject(); 
			return idlAccount;
		}
}
