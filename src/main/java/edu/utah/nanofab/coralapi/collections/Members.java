package edu.utah.nanofab.coralapi.collections;

import edu.utah.nanofab.coralapi.resource.Member;


public class Members extends ProxySet<Member> {
  @Override
  public boolean contains(Object memberObject) 
  {
    Member member = (Member)memberObject;
    for (Member m : this.collection ) {
      if (membersEqual(member, m)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsName(String member) 
  {
    for (Member m : this.collection ) {
      if (m.getName().equals(member)) {
        return true;
      }
    }
    return false;
  }

  private boolean membersEqual(Member member, Member m) {
    return m.equals(member);
  }

  public String[] getNames() 
  {
    String[] names = new String[this.size()];
    int i = 0;
    for (Member m : this.collection ) {
      names[i] = m.getName();
      i++;
    }
    return names;
  }

}
