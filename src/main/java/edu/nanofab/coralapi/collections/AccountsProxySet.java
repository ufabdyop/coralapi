package edu.nanofab.coralapi.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.nanofab.coralapi.resource.Account;

public class AccountsProxySet implements Set {
	protected HashSet<Account> accounts = new HashSet<Account>();

	public boolean add(Object arg0) {
		return accounts.add((Account) arg0); 
	}

	public boolean addAll(Collection arg0) {
		return accounts.addAll(arg0); 
	}

	public void clear() {
		accounts.clear();
	}

	public boolean contains(Object arg0) {
		return accounts.contains(arg0); 
	}

	public boolean containsAll(Collection arg0) {
		return accounts.containsAll(arg0); 
	}

	public boolean isEmpty() {
		return accounts.isEmpty(); 
	}

	public Iterator iterator() {
		return accounts.iterator();
	}

	public boolean remove(Object arg0) {
		return accounts.remove(arg0); 
	}

	public boolean removeAll(Collection arg0) {
		return accounts.removeAll(arg0); 
	}

	public boolean retainAll(Collection arg0) {
		return accounts.retainAll(arg0); 
	}

	public int size() {
		return accounts.size();
	}

	public Object[] toArray() {
		return accounts.toArray();
	}

	public Object[] toArray(Object[] arg0) {
		return accounts.toArray();
	}

}
