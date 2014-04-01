package edu.utah.nanofab.coralapi.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.utah.nanofab.coralapi.resource.Project;

public class ProjectsProxySet implements Set {
	protected HashSet<Project> projects = new HashSet<Project>();

	public boolean add(Object arg0) {
		return projects.add((Project) arg0); 
	}

	public boolean addAll(Collection arg0) {
		return projects.addAll(arg0); 
	}

	public void clear() {
		projects.clear();
	}

	public boolean contains(Object arg0) {
		return projects.contains(arg0); 
	}

	public boolean containsAll(Collection arg0) {
		return projects.containsAll(arg0); 
	}

	public boolean isEmpty() {
		return projects.isEmpty(); 
	}

	public Iterator iterator() {
		return projects.iterator();
	}

	public boolean remove(Object arg0) {
		return projects.remove(arg0); 
	}

	public boolean removeAll(Collection arg0) {
		return projects.removeAll(arg0); 
	}

	public boolean retainAll(Collection arg0) {
		return projects.retainAll(arg0); 
	}

	public int size() {
		return projects.size();
	}

	public Object[] toArray() {
		return projects.toArray();
	}

	public Object[] toArray(Object[] arg0) {
		return projects.toArray();
	}

}
