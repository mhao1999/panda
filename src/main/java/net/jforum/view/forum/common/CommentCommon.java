package net.jforum.view.forum.common;

import java.util.Date;

import net.jforum.JForumExecutionContext;
import net.jforum.SessionFacade;
import net.jforum.context.RequestContext;
import net.jforum.entities.Comment;

public class CommentCommon 
{
	public static Comment fillCommentFromRequest()
	{
		Comment comment = new Comment();
		comment.setTime(new Date());
		
		comment.setUserId(SessionFacade.getUserSession().getUserId());
		
		RequestContext request = JForumExecutionContext.getRequest();
		comment.setText(request.getParameter("message"));
		
		return comment;
	}
}
