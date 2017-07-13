/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utah.nanofab.coralapi;

import edu.utah.nanofab.coralapi.exceptions.CoralConnectionException;
import edu.utah.nanofab.coralapi.exceptions.UnknownMemberException;

/**
 *
 * @author ryant
 */
public class CoralAPIWithRetries {
    private CoralAPI api;
    private int numberOfRetries = 1;
    
    public CoralAPIWithRetries(String coralUser, String configUrl) throws CoralConnectionException {
        api = new CoralAPI(coralUser, configUrl);
    }
    
    public void activateMember(String memberName) throws UnknownMemberException, Exception {
        int count = 0;
        boolean success = false;
        while (count < numberOfRetries && !success) {
            count++;
            try {
                this.api.activateMember(memberName);
                success = true;
            } catch (Exception ex) {
                //log this
                try {
                    this.api.reInitialize();
                } catch (Exception ex2) {
                    //ignore
                }
            }
        }
    }

}
