package edu.utah.nanofab.coralapi.examples;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.exceptions.CoralConnectionException;

public class PasswordChange {
    private CoralAPI instance;

    public PasswordChange() throws CoralConnectionException {
        this.instance = new CoralAPI("coral", "http://localhost/coral/lib/config.jar");
    }
    public void change(String user, String pass) {
        try {
                instance.updatePassword(user, pass);
        } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error.  Usage is PasswordChange username password");
                System.exit(1);
        }
    }
    public void close() {
        instance.close();
    }
    public static void main(String[] args) throws CoralConnectionException {
        PasswordChange changer = new PasswordChange();
        try {
                System.out.println("Changing password");
                changer.change(args[0], args[1]);
                System.out.println("Changed password");
        } catch (Exception e) {
                System.err.println("Error.  Usage is PasswordChange username password");
                System.exit(1);
        }
        changer.close();
        System.out.println("Closed Coral Connection");
        System.exit(0);
    }
}
