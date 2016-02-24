package edu.utah.nanofab.coralapi.examples;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.collections.Machines;
import edu.utah.nanofab.coralapi.resource.Machine;

public class GetMachines {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please pass config url for argument, eg: http://localhost/coral/lib/config.jar");
            System.exit(1);
        }
        String url = args[0];
        CoralAPI instance = new CoralAPI("coral", url);
        Machines machines = instance.getAllMachines();
        for (Machine m : machines) {
            System.out.println(m.getName());
        }
        instance.close();
        System.out.println("Closed Coral Connection");
        System.exit(0);
    }
}