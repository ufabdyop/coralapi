package edu.utah.nanofab.coralapi.resource;

import org.opencoral.idl.Persona;

public class ProjectRole {
  private String project;
  private String member;
  private String role;
  
  public ProjectRole() {
  }

  public ProjectRole(String project, String member, String role) {
    init(project, member, role);
  }
  
  public void init(String project, String member, String role) {
    this.project = project;
    this.member = member;
    this.role = role;
  }

  public String getProject() {
    return project;
  }
  public void setProject(String project) {
    this.project = project;
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
  
  public boolean equals(ProjectRole role) {
    return this.project.equals(role.project) && this.member.equals(role.member)
        && this.role.equals(role.role);
  }
  
  public String toString() {
    return String.format("<ProjectRole> : %s, %s, %s", project, member, role);
  }

}
