package edu.utah.nanofab.coralapi.resource;

import org.opencoral.idl.Persona;

public class LabRole {
	private String lab;
	private String member;
	private String role;
	
	public LabRole() {
	}

	public LabRole(String lab, String member, String role) {
		init(lab, member, role);
	}
	
	public void init(String lab, String member, String role) {
		this.lab = lab;
		this.member = member;
		this.role = role;
	}

	public String getLab() {
		return lab;
	}
	public void setLab(String lab) {
		this.lab = lab;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	public void populateFromIdlPersona(Persona idlRole) {
		init(idlRole.target, idlRole.member, idlRole.role);
	}
	
	public boolean equals(LabRole role) {
		return this.lab.equals(role.lab) && this.member.equals(role.member)
				&& this.role.equals(role.role);
	}
	
	public String toString() {
		return String.format("<LabRole> : %s, %s, %s", lab, member, role);
	}

}
