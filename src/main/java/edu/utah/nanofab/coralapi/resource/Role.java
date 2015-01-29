package edu.utah.nanofab.coralapi.resource;

import org.opencoral.idl.Timestamp;

public class Role {

  private String name;
  private String type;
  private String description;
  private boolean active;
  private org.opencoral.idl.Timestamp bdate = null;
  private org.opencoral.idl.Timestamp edate = null;
      
  public Role() {
  }
  
  /**
   * Creates a new Role with the supplied parameters.
   * 
   * @param name The name of the role.
   * @param type The type of the role.
   * @param description A short description for the role.
   * @param active A boolean value indicating if the role should be active or not.
   * @param bdate The beginning date for the role.
   * @param edate The ending date for the role.
   */
  public Role(String name, String type, String description, boolean active,
      Timestamp bdate, Timestamp edate) {
    super();
    this.name = name;
    this.type = type;
    this.setDescription(description);
    this.active = active;
    this.bdate = bdate;
    this.edate = edate;
  }

  /**
   * Gets the name of this Role.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this Role.
   * 
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the type of role for this Role.
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type of role for this Role.
   * 
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Gets the active value of this Role.
   * 
   * @return the active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the active value for this Role.
   * 
   * @param active the active to set
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Gets the beginning date for this Role.
   * 
   * @return the bdate
   */
  public org.opencoral.idl.Timestamp getBdate() {
    return bdate;
  }

  /**
   * Sets the beginning date for this Role.
   * 
   * @param bdate the bdate to set
   */
  public void setBdate(org.opencoral.idl.Timestamp bdate) {
    this.bdate = bdate;
  }

  /**
   * Gets the ending date for this Role.
   * 
   * @return the edate
   */
  public org.opencoral.idl.Timestamp getEdate() {
    return edate;
  }

  /**
   * Sets the ending date for this Role.
   * @param edate the edate to set
   */
  public void setEdate(org.opencoral.idl.Timestamp edate) {
    this.edate = edate;
  }

  /**
   * Gets the short description for this Role.
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the short description for this Role.
   * 
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }
  
  /**
   * Generates an object of type edu.utah.nanofab.coralapi.resource.Role from an IDL Role object.
   * 
   * @param r The IDL Role used to populate this Role.
   */
  public void populateFromIdlRole(org.opencoral.idl.Role r) {
    this.setName(r.name);
    this.setType(r.type);
    this.setDescription(r.description);
    this.setActive(r.active);
    this.setBdate(r.bdate);
    this.setEdate(r.edate);
  }
}
