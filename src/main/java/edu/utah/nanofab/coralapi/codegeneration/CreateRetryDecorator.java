/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utah.nanofab.coralapi.codegeneration;

import edu.utah.nanofab.coralapi.CoralAPI;
import edu.utah.nanofab.coralapi.collections.Machines;
import edu.utah.nanofab.coralapi.exceptions.CoralConnectionException;
import edu.utah.nanofab.coralapi.resource.Machine;
import java.io.File;
import java.io.FileNotFoundException;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
//import org.jboss.forge.roaster.model.util.DesignPatterns;

/**
 *
 * @author ryant
 */
public class CreateRetryDecorator {
    public static void main(String[] args) throws CoralConnectionException, FileNotFoundException {
        String defaultFile = "src/main/java/edu/utah/nanofab/coralapi/CoralAPI.java";
        String filename = "";
        if (args.length < 1) {
            System.err.println("Please pass file name, using default");
            filename = defaultFile;
        } else {
            filename = args[0];
        }
        System.err.println("Decorating: " + filename);
        File file = new File(filename);
        if (!file.exists()) {
            System.err.println("No such file");
            System.exit(1);
        }
        JavaType jt = Roaster.parse(file);
        JavaClassSource newClass = CoralAPIGenerateRetryDecorator.createDecorator(jt);
        System.out.println(newClass.toString());
    }
    
}
