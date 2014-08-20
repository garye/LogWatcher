package org.graysky.eclipse.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A fixed-size list of Objects. If an item is inserted into a full list the
 * oldest item will be removed, and the list will stay full. Insertion into the
 * list is a constant-time operation.
 */
public class BoundedList implements List
{
    public int lastIndexOf(Object o)
    {
        return m_list.lastIndexOf(o);
    }
    public Object[] toArray()
    {
        return m_list.toArray();
    }
    public Object[] toArray(Object[] a)
    {
        return m_list.toArray(a);
    }
	private LinkedList m_list = new LinkedList();
	private int m_maxItems = 0;
	private int m_count = 0;
	private int m_index = 0;

	/**
	 * Used to allocate string buffer.
	 */
	private static final int LINE_WIDTH = 80;

	public boolean isFull()
	{
		return (m_count >= m_maxItems);
	}

	public boolean isEmpty()
	{
		return (m_count == 0);
	}

	public BoundedList(int maxItems)
	{
		m_maxItems = maxItems;
	}

	public synchronized void setMaxItems(int max)
	{
		m_maxItems = max;
	}

	public synchronized Object get(int i)
	{
		return m_list.get(i);
	}

	public synchronized void clear()
	{
		m_list.clear();
		m_count = 0;
	}

	public synchronized boolean add(Object o)
	{
		if (isFull()) {
			while (isFull()) {
				m_list.removeFirst();
				m_count--;
			}
			m_list.addLast(o);
			m_count++;
		}
		else {
			m_list.add(o);
			m_count++;
		}
		
		return true;
	}

	public int size()
	{
		return m_count;
	}

	/**
	 * Return the contents of the list formatted as a string. Each item in the
	 * list contributes one line to the string by calling it's toString()
	 * method.
	 * 
	 * @return String
	 */
	public String getFormattedText()
	{
		StringBuffer sb = new StringBuffer(m_count * LINE_WIDTH);
		for (int i = 0; i < m_count; i++) {
			sb.append(m_list.get(i) + "\n");
		}
		return sb.toString();
	}

	/**
	 * Debugging method.
	 */
	public void dump()
	{
		System.out.println("List contents");
		for (int i = 0; i < m_count; i++) {
			System.out.println(i + ": " + m_list.get(i));
		}
	}
    public void add(int index, Object element)
    {
        throw new UnsupportedOperationException();
    }
    
    public boolean addAll(int index, Collection c)
    {
        throw new UnsupportedOperationException();
    }
    public boolean addAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }
    public boolean contains(Object o)
    {
        return m_list.contains(o);
    }
    public boolean containsAll(Collection c)
    {
        return m_list.containsAll(c);
    }
    public int indexOf(Object o)
    {
        return m_list.indexOf(o);
    }
    public Iterator iterator()
    {
        return m_list.iterator();
    }
    public ListIterator listIterator()
    {
        return m_list.listIterator();
    }
    public ListIterator listIterator(int index)
    {
        return m_list.listIterator(index);
    }
    public Object remove(int index)
    {
        throw new UnsupportedOperationException();
    }
    public boolean remove(Object o)
    {
        boolean result = m_list.remove(o);
        if (result) {
            m_count--;
        }
        
        return result;
    }
    public boolean removeAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }
    public boolean retainAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }
    public Object set(int index, Object element)
    {
        throw new UnsupportedOperationException();
    }
    public List subList(int fromIndex, int toIndex)
    {
        return m_list.subList(fromIndex, toIndex);
    }
}
