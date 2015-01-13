package com.esuta.fidm.gui.page.org.component.data;

import org.apache.commons.lang3.NotImplementedException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  @author shood
 *
 *  implementation by lazyman, see
 *  (https://github.com/Evolveum/midpoint/blob/a6c023945dbea34db69a8ff17c9a61b7184c42cc/gui/admin-gui/src/main/java/com/evolveum/midpoint/web/page/admin/users/dto/TreeStateSet.java)
 */
public class TreeStateSet<T extends Serializable> implements Set<T>, Serializable {

    private Set<T> set = new HashSet<>();
    private boolean inverse;

    public void expandAll() {
        set.clear();
        inverse = true;
    }

    public void collapseAll() {
        set.clear();
        inverse = false;
    }

    @Override
    public boolean add(T t) {
        return inverse ? set.remove(t) : set.add(t);
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        T t = (T) o;
        return inverse ? !set.contains(t) : set.contains(t);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        T t = (T) o;
        return inverse ? set.add(t) : set.remove(t);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return inverse ? !set.containsAll(c) : set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return inverse ? set.removeAll(c) : set.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new NotImplementedException("Not yet implemented.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return inverse ? set.addAll((Collection<? extends T>) c) : set.removeAll(c);
    }

    @Override
    public void clear() {
        set.clear();
    }
}
