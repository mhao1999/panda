package net.jforum.dao;

import net.jforum.entities.Comment;

public interface CommentDAO {
	/**
	 * Adds a new comment.
	 */
	int addNew(Comment comment);
}
