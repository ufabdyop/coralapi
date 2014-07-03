package edu.utah.nanofab.coralapi.collections;


import edu.utah.nanofab.coralapi.resource.Machine;

public class Machines extends ProxySet<Machine> {

	public static Machines fromIdlMachineArray(org.opencoral.idl.Machine[] allMachines) {
		Machines theCollection = new Machines();
		for(org.opencoral.idl.Machine idlMachine : allMachines) {
			Machine machine = new Machine();
			machine.populateFromIdlMachine(idlMachine);
			theCollection.add(machine);
		}
    	return theCollection;
	}

}