package net.jforum.entities;

import java.io.Serializable;
import java.util.Date;

import net.jforum.view.forum.common.ViewCommon;

public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private int postId;
	private int topicId;
	private int userId;
	private String formattedTime;
	private String text;
	private Date time;
	
	public Comment() { }
	
	public Comment(int commentId)
	{
		this.id = commentId;
	}
	
	/**
	 * Copy constructor
	 */
	public Comment(Comment comment)
	{
		this.id = comment.getId();
		this.postId = comment.getPostId();
		this.topicId = comment.getTopicId();
		this.userId = comment.getUserId();
		this.text = comment.getText();
		this.time = comment.getTime();
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getPostId() {
		return this.postId;
	}
	
	public int getTopicId() {
		return this.topicId;
	}
	
	public int getUserId() {
		return this.userId;
	}
	
	public String getText() {
		return this.text;
	}
	
	public Date getTime() {
		return this.time;
	}
	
	public void setFormattedTime(String formattedTime)
	{
		this.formattedTime = formattedTime;
	}
	
	public String getFormattedTime()
	{
		if (this.formattedTime == null && this.time != null) {
			this.formattedTime = ViewCommon.formatDate(this.time);
		}
		
		return this.formattedTime;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setPostId(int postId)
	{
		this.postId = postId;
	}
	
	public void setTopicId(int topicId)
	{
		this.topicId = topicId;
	}
	
	public void setUserId(int userId)
	{
		this.userId = userId;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void setTime(Date time)
	{
		this.time = time;
	}
}
