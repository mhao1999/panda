package net.jforum.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PostComments implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Comment> lst;
	
	public PostComments()
	{
		lst = new LinkedList<Comment>();
	}
	
	public PostComments(PostComments pc)
	{
		lst = new LinkedList<Comment>();
		for (Comment cmt : pc.getList())
		{
			lst.add(new Comment(cmt));
		}
	}
	
	public List<Comment> getList()
	{
		return this.lst;
	}
	
	public void setList(List<Comment> l)
	{
		this.lst = l;
	}
	
	public void add(Comment cmt)
	{
		this.lst.add(cmt);
	}
}
