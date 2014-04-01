package edu.utah.nanofab.coralapi.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import edu.utah.nanofab.coralapi.resource.Member;

public class MembersProxySet implements Set {
	protected HashSet<Member> members = new HashSet<Member>();

	public boolean add(Object arg0) {
		return members.add((Member) arg0); 
	}

	public boolean addAll(Collection arg0) {
		return members.addAll(arg0); 
	}

	public void clear() {
		members.clear();
	}

	public boolean contains(Object arg0) {
		return members.contains(arg0); 
	}

	public boolean containsAll(Collection arg0) {
		return members.containsAll(arg0); 
	}

	public boolean isEmpty() {
		return members.isEmpty(); 
	}

	public Iterator iterator() {
		return members.iterator();
	}

	public boolean remove(Object arg0) {
		return members.remove(arg0); 
	}

	public boolean removeAll(Collection arg0) {
		return members.removeAll(arg0); 
	}

	public boolean retainAll(Collection arg0) {
		return members.retainAll(arg0); 
	}

	public int size() {
		return members.size();
	}

	public Object[] toArray() {
		return members.toArray();
	}

	public Object[] toArray(Object[] arg0) {
		return members.toArray();
	}

}
