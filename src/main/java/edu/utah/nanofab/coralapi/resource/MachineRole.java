package edu.utah.nanofab.coralapi.resource;

public class MachineRole extends GenericRole {
	  public String getMachine() {
	    return super.getTarget();
	  }
	  public void setMachine(String machine) {
		  super.setTarget(machine);
	  }
}
