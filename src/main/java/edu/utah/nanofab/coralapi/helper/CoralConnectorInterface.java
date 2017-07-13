package edu.utah.nanofab.coralapi.helper;

import edu.utah.nanofab.coralapi.exceptions.CoralConnectionException;
import org.opencoral.idl.Auth.AuthManager;
import org.opencoral.idl.Equipment.EquipmentManager;
import org.opencoral.idl.Reservation.ReservationManager;
import org.opencoral.idl.Resource.ResourceManager;
import org.opencoral.idl.Runtime.RuntimeManager;

public interface CoralConnectorInterface {
    public void setup(String coralUser, String iorUrl) throws CoralConnectionException;
    public AuthManager getAuthManager();
    public EquipmentManager getEquipmentManager();
    public ReservationManager getReservationManager();
    public ResourceManager getResourceManager();
    public RuntimeManager getRuntimeManager();
    public String getTicketString();
    public void release();
}
