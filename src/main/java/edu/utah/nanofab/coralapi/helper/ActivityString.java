package edu.utah.nanofab.coralapi.helper;

import org.opencoral.idl.Activity;

public class ActivityString {
	String me = "";
	public ActivityString(Activity a) {
		me = "";
		me += "isNull: " + a.isNull + ", ";
		me += "id: " + a.id + ", ";
		me += "agent: " + a.agent + ", ";
		me += "project: " + a.project + ", ";
		me += "account: " + a.account + ", ";
		me += "member: " + a.member + ", ";
		me += "item: " + a.item + ", ";
		me += "area: " + a.area + ", ";
		me += "lab: " + a.lab + ", ";
		me += "process: " + a.process + ", ";
		me += "type: " + a.type + ", ";
		me += "amount: " + a.amount + ", ";
		me += "primeAmount: " + a.primeAmount + ", ";
		me += "bdate: "+ a.bdate.year + " " + a.bdate.month + " " + a.bdate.day + " " + a.bdate.hour + " " + a.bdate.minute + " " + a.bdate.second + ", ";
		me += "edate: "+ a.edate.year + " " + a.edate.month + " " + a.edate.day + " " + a.edate.hour + " " + a.edate.minute + " " + a.edate.second + ", ";
		me += "creationDate: " + a.creationDate + ", ";
		me += "creator: " + a.creator + ", ";
		me += "reference: " + a.reference + ", ";
		me += "stale: " + a.stale + ", ";
	}
	
	@Override
	public String toString() {
		return me;
	}
}
