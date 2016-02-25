package edu.utah.nanofab.coralapi.examples;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.CoralAPIPool;
import edu.utah.nanofab.coralapi.CoralAPISynchronized;
import edu.utah.nanofab.coralapi.collections.Machines;
import edu.utah.nanofab.coralapi.collections.Reservations;
import static edu.utah.nanofab.coralapi.helper.TimestampConverter.dateToAdapterString;
import static edu.utah.nanofab.coralapi.helper.TimestampConverter.stringToDate;
import edu.utah.nanofab.coralapi.resource.Machine;
import edu.utah.nanofab.coralapi.resource.Reservation;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

public class StressTestApi {
    public static String url;
    public static String member;
    public static String project;
    public static String machine;
    public static String reservationStart;
    public static CoralAPIPool apiPool;
    public static int numberOfTests;
    public static int delay;
    public static void main(String[] args) {
        
        checkParameters(args);
        url = args[0];        
        member = args[1];
        project = args[2];
        machine = args[3];
        reservationStart = args[4];
        numberOfTests = Integer.parseInt(args[5]);
        delay = Integer.parseInt(args[6]);
        
        apiPool = CoralAPIPool.getInstance(url);

        System.out.println("\nOiaiu-=================\nOiaiu-STARTING STRESS TEST IN 3 SECONDS"
                + "\nOiaiu-=================");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
        }
        
        stressWithReservations();

        System.out.println("\nOiaiu-=================\nOiaiu-FINISHED STRESS TEST");
        waitForInterrupt();
    }
    private static void stressWithReservations() {
        try {
            for( int i =0 ; i < numberOfTests; i++) {
                createReservation(i);
                System.out.println("Oiaiu-Created Reservation");
                if (delay > 0) {
                    Thread.sleep(delay);
                }
                deleteReservation(i);
                System.out.println("Oiaiu-Deleted Reservation");
                if (delay > 0) {
                    Thread.sleep(delay);
                }
            }
        } catch (Exception ex) {
                Logger.getLogger(StressTestApi.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
        }
    }

    private static void createReservation(int i) {
        CoralAPISynchronized instance = apiPool.getConnection(member);
        try {
            Date resStartDate = new Date(stringToDate(reservationStart).getTime() + (30 * 1000 * i * 60));
            String resStart = dateToAdapterString(resStartDate);
            System.out.println("Oiaiu-Creating Reservation " + resStart);            
            instance.createNewReservation(member, member, project, machine, resStart, 30);
        } catch (Exception ex) {
            System.out.println("Oiaiu-Create Reservation FAILED");
            ex.printStackTrace();
            Logger.getLogger(StressTestApi.class.getName()).log(Level.SEVERE, "Failed to create reservation", ex);
        }
    }

    private static void deleteReservation(int i) {
        CoralAPISynchronized instance = apiPool.getConnection(member);
        try {
            Date resStartDate = new Date(stringToDate(reservationStart).getTime() + (30 * 1000 * i * 60));
            String resStart = dateToAdapterString(resStartDate);
            System.out.println("Oiaiu-Deleting Reservation " + resStart);            
            instance.deleteReservation(member, member, project, machine, resStart, 30);
        } catch (Exception ex) {
            System.out.println("Oiaiu-Deleted Reservation FAILED");
            ex.printStackTrace();
            Logger.getLogger(StressTestApi.class.getName()).log(Level.SEVERE, "Failed to delete reservation", ex);
        }
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

    private static void checkParameters(String[] args) {
        if (args.length != 7) {
            System.out.println("Please pass config url for argument, eg: \n"  + ""
                    + "\tstressTest configUrl member project machine reservationStart numberOfRequests millisecondDelayBetweenRequests"
                    + "\tstressTest http://localhost/coral/lib/config.jar 'coral' 'Maintenance' 'Oxford 80 Plasmalab' '2016-02-29 00:00:00' 10 3000");
            System.exit(1);
        }
    }
}