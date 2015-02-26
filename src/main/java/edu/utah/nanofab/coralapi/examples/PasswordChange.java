package edu.utah.nanofab.coralapi.examples;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.collections.LabRoles;
import edu.utah.nanofab.coralapi.collections.Members;
import edu.utah.nanofab.coralapi.collections.Projects;
import edu.utah.nanofab.coralapi.exceptions.InvalidRoleException;
import edu.utah.nanofab.coralapi.exceptions.InvalidTicketException;
import edu.utah.nanofab.coralapi.exceptions.NotAuthorizedException;
import edu.utah.nanofab.coralapi.exceptions.RoleDuplicateException;
import edu.utah.nanofab.coralapi.exceptions.UnknownMemberException;
import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.resource.LabRole;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.Reservation;
import edu.utah.nanofab.coralapi.resource.Role;
import edu.utah.nanofab.coralapi.helper.TimestampConverter;
import edu.utah.nanofab.coralapi.helper.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.net.InetAddress;


public class PasswordChange {
    private CoralAPI instance;

    public PasswordChange() {
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
    public static void main(String[] args) {
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
