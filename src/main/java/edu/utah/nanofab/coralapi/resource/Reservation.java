package edu.utah.nanofab.coralapi.resource;

import java.util.Date;

import edu.utah.nanofab.coralapi.helper.TimestampConverter;

public class Reservation {
  
  private Member member;
  private Project project;
  private Account account;
  private Date bdate;
  private Date edate;
  private String item;
  private String lab;
  
  /**
   * Creates a new Reservation instance with the supplied information.
   * 
   * @param member The member this reservation is for.
   * @param project The project this reservation is associated with.
   * @param account The account this reservation should be billed to.
   * @param bdate The beginning date of this reservation.
   * @param edate The ending date of this reservation.
   * @param item The tool that this reservation is made for.
   * @param lab The lab that the supplied item exists in.
   */
  public Reservation(Member member, Project project, Account account,
      java.util.Date bdate, java.util.Date edate, String item, String lab) {
    super();
    this.member = member;
    this.project = project;
    this.account = account;
    this.bdate = bdate;
    this.edate = edate;
    this.item = item;
    this.lab = lab;
  }
  
  public Reservation() {
    
  }

  public Date getBdate() {
    return bdate;
  }
  public void Date(Date bdate) {
    this.bdate = bdate;
  }
  public Date getEdate() {
    return edate;
  }
  public void setEdate(Date edate) {
    this.edate = edate;
  }
  public Member getMember() {
    return member;
  }
  public void setMember(Member member) {
    this.member = member;
  }
  public Project getProject() {
    return project;
  }
  public void setProject(Project project) {
    this.project = project;
  }
  public Account getAccount() {
    return account;
  }
  public void setAccount(Account account) {
    this.account = account;
  }
  public String getItem() {
    return item;
  }
  public void setItem(String item) {
    this.item = item;   
  }
  public void setBdate(int y, int m, int d, int h, int min) {
    this.bdate = TimestampConverter.dateFromDateComponents(y,m,d,h,min,0);
  }
  public void setEdate(int y, int m, int d, int h, int min) {
    this.edate = TimestampConverter.dateFromDateComponents(y,m,d,h,min,0);
  }
  public String getLab() {
    return lab;
  }
  public void setLab(String lab) {
    this.lab = lab;
  } 
}
