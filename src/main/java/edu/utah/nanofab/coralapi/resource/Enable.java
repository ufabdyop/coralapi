package edu.utah.nanofab.coralapi.resource;

import java.util.Date;

import edu.utah.nanofab.coralapi.helper.TimestampConverter;

public class Enable {
  private Date bdate;
  private Date edate;
  private String item;
  private String lab;

  private Member member;
  private Member agent;
  private Project project;
  public Member getAgent() {
    return agent;
  }
  public void setAgent(Member agent) {
    this.agent = agent;
  }
  private Account account;

  public Date getBdate() {
    return bdate;
  }
  public void setBdate(Date bdate) {
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
