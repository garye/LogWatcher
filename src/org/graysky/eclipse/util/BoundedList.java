package org.graysky.eclipse.util;

import java.util.LinkedList;
import java.util.Vector;

public class BoundedList 
{
	private LinkedList	m_list		= new LinkedList();
	private int			m_maxItems	= 0;
	private int			m_count		= 0;
	private int 		m_index		= 0;
	
	public boolean isFull()
	{
		return (m_count == m_maxItems);
	}
	
	public boolean isEmpty()
	{	
		return (m_count == 0);
	}
	
	public BoundedList(int maxItems)
	{
		m_maxItems = maxItems;
	}
	
	public Object get(int i)
	{
		if (isEmpty()) {
			return null;	
		}
		
		return m_list.get(i);
	}
	
	public void put(Object o)
	{	
		if (isFull()) {
			m_list.removeFirst();
			m_list.addLast(o);
		}
		else {
			m_list.add(o);
			m_count++;
		}
	}

	public int size()
	{
		return m_count;
	}

	public String getFormattedText()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < m_count; i++) {
			sb.append(m_list.get(i) + "\n");	
		}	
		
		return sb.toString();
	}

	public void dump()
	{
		System.out.println("List contents");
		for (int i = 0; i < m_count; i++) {
			System.out.println(i + ": " + m_list.get(i));
		}
	}
}
