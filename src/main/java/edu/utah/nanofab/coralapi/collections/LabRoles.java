package edu.utah.nanofab.coralapi.collections;

import edu.utah.nanofab.coralapi.resource.LabRole;

public class LabRoles extends ProxySet<LabRole> {

	public static LabRoles fromIdlPersonaArray(org.opencoral.idl.Persona[] allRoles) {
		LabRoles roleCollection = new LabRoles();
		System.out.println("converting lab roles");
		for(org.opencoral.idl.Persona idlRole : allRoles) {
			System.out.println("role found: " + idlRole.member + ", " + idlRole.target);
			LabRole role = new LabRole();
			role.populateFromIdlPersona(idlRole);
			roleCollection.add(role);
		}
    	return roleCollection;
	}
	
	public boolean contains(LabRole roleObject) 
	{
		LabRole role = roleObject;
		for (LabRole r : this.collection ) {
			if (r.equals(role)) {
				return true;
			}
		}
		return false;
	}


}
