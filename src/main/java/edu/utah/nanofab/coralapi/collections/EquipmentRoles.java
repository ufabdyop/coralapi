package edu.utah.nanofab.coralapi.collections;

import edu.utah.nanofab.coralapi.resource.GenericRole;

public class EquipmentRoles extends ProxySet<GenericRole> {

  public static EquipmentRoles fromIdlPersonaArray(org.opencoral.idl.Persona[] allRoles) {
    EquipmentRoles roleCollection = new EquipmentRoles();
    System.out.println("converting lab roles");
    for(org.opencoral.idl.Persona idlRole : allRoles) {
      System.out.println("role found: " + idlRole.member + ", " + idlRole.target);
      GenericRole role = new GenericRole();
      role.populateFromIdlPersona(idlRole);
      roleCollection.add(role);
    }
    return roleCollection;
  }
  
  public boolean contains(GenericRole roleObject) 
  {
    GenericRole role = roleObject;
    for (GenericRole r : this.collection ) {
      if (r.equals(role)) {
        return true;
      }
    }
    return false;
  }


}
