package edu.utah.nanofab.coralapi.collections;

import edu.utah.nanofab.coralapi.resource.ProjectRole;

public class ProjectRoles extends ProxySet<ProjectRole> {

  public static ProjectRoles fromIdlPersonaArray(org.opencoral.idl.Persona[] allRoles) {
    ProjectRoles roleCollection = new ProjectRoles();
    System.out.println("converting project roles");
    for(org.opencoral.idl.Persona idlRole : allRoles) {
      System.out.println("role found: " + idlRole.member + ", " + idlRole.target);
      ProjectRole role = new ProjectRole();
      role.populateFromIdlPersona(idlRole);
      roleCollection.add(role);
    }
    return roleCollection;
  }
  
  public boolean contains(ProjectRole roleObject) 
  {
    ProjectRole role = roleObject;
    for (ProjectRole r : this.collection ) {
      if (r.equals(role)) {
        return true;
      }
    }
    return false;
  }

}
