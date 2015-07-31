package edu.utah.nanofab.coralapi.resource;

import org.opencoral.idl.Persona;

public class GenericRole {
  private String target;
  private String member;
  private String role;
  
  public GenericRole() {
  }

  public GenericRole(String target, String member, String role) {
    init(target, member, role);
  }
  
  public void init(String target, String member, String role) {
    this.target = target;
    this.member = member;
    this.role = role;
  }

  public String getTarget() {
    return target;
  }
  public void setTarget(String target) {
    this.target = target;
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
  
  public boolean equals(GenericRole role) {
    return this.target.equals(role.target) && this.member.equals(role.member)
        && this.role.equals(role.role);
  }
  
  public String toString() {
    return String.format("<GenericRole> : %s, %s, %s", target, member, role);
  }

}