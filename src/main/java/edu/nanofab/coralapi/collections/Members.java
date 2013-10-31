package edu.nanofab.coralapi.collections;

import edu.nanofab.coralapi.resource.Member;


public class Members extends MembersProxySet {
	@Override
	public boolean contains(Object memberObject) 
	{
		Member member = (Member)memberObject;
		for (Member m : this.members ) {
			if (membersEqual(member, m)) {
				return true;
			}
		}
		return false;
	}

	public boolean membersEqual(Member member, Member m) {
		System.out.println("checking if " + member.getName() + " is equal to " + m.getName());
		return m.equals(member);
	}
}
