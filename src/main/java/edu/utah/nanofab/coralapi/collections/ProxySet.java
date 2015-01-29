package edu.utah.nanofab.coralapi.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ProxySet<T> implements Set<T> {
  protected HashSet<T> collection = new HashSet<T>();

  public boolean add(Object arg0) {
    return collection.add((T) arg0);
  }

  public boolean addAll(Collection arg0) {
    return collection.addAll(arg0); 
  }

  public void clear() {
    collection.clear();
  }

  public boolean contains(Object arg0) {
    return collection.contains(arg0); 
  }

  public boolean containsAll(Collection arg0) {
    return collection.containsAll(arg0); 
  }

  public boolean isEmpty() {
    return collection.isEmpty(); 
  }

  public Iterator<T> iterator() {
    return collection.iterator();
  }

  public boolean remove(Object arg0) {
    return collection.remove(arg0); 
  }

  public boolean removeAll(Collection arg0) {
    return collection.removeAll(arg0); 
  }

  public boolean retainAll(Collection arg0) {
    return collection.retainAll(arg0); 
  }

  public int size() {
    return collection.size();
  }

  public T[] toArray() {
    return (T[]) collection.toArray();
  }

  public Object[] toArray(Object[] arg0) {
    return collection.toArray();
  }

}
