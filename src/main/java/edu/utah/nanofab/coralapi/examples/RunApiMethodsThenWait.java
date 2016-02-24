package edu.utah.nanofab.coralapi.examples;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.collections.Machines;
import edu.utah.nanofab.coralapi.collections.Reservations;
import edu.utah.nanofab.coralapi.resource.Machine;
import edu.utah.nanofab.coralapi.resource.Reservation;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

public class RunApiMethodsThenWait {
    public static void main(String[] args) {
        checkParameters(args);
        String url = args[0];        
        getMachines(url);
        getReservations(url);
        deleteReservation(url);
        createReservation(url);
        System.out.println("Closed Coral Connection");
        waitForInterrupt();
    }

    private static void deleteReservation(String url) {
        CoralAPI instance = new CoralAPI("coral", url);
        try {
            instance.deleteReservation("coral", "coral", "Maintenance", "Oxford Plasmalab 80", "2016-02-25 01:00:00", 120);
        } catch (Exception ex) {
            Logger.getLogger(RunApiMethodsThenWait.class.getName()).log(Level.SEVERE, null, ex);
        }
        instance.close();
    }

    private static void createReservation(String url) {
        CoralAPI instance = new CoralAPI("coral", url);
        try {
            instance.createNewReservation("coral", "coral", "Maintenance", "Oxford Plasmalab 80", "2016-02-25 01:00:00", 120);
        } catch (Exception ex) {
            Logger.getLogger(RunApiMethodsThenWait.class.getName()).log(Level.SEVERE, null, ex);
        }
        instance.close();
    }

    private static void waitForInterrupt() {
        while(true) {
            //System.out.println("Sleeping for 5 seconds");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
            }
        }
    }

    private static void getReservations(String url) {
        CoralAPI instance2 = new CoralAPI("coral", url);
        Date bdate = new Date(2016, 1, 1);
        Date edate = new Date(2016, 3, 1);
        Reservations reservations;
        try {
            reservations = instance2.getReservations("Oxford Plasmalab 80", bdate, edate);
            for (Reservation r : reservations) {
                System.out.println(r.getMember());
            }
        } catch (Exception ex) {
            Logger.getLogger(RunApiMethodsThenWait.class.getName()).log(Level.SEVERE, null, ex);
        }
        instance2.close();
    }

    private static void getMachines(String url) {
        CoralAPI instance = new CoralAPI("coral", url);
        Machines machines = instance.getAllMachines();
        for (Machine m : machines) {
            System.out.println(m.getName());
        }
        instance.close();
    }

    private static void checkParameters(String[] args) {
        if (args.length != 1) {
            System.out.println("Please pass config url for argument, eg: http://localhost/coral/lib/config.jar");
            System.exit(1);
        }
    }
}