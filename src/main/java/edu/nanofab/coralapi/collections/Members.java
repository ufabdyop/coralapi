package edu.nanofab.coralapi.collections;

import org.opencoral.idl.Member;

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
		return m.name.equals(member.name);
	}
}
