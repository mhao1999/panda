/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer.
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on 24/05/2004 12:25:35
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.generic.GenericTopicDAO;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.exceptions.DatabaseException;
import net.jforum.repository.ForumRepository;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Andre de Andrade da Silva (<a href="mailto:andre.de.andrade@gmail.com">andre.de.andrade@gmail.com</a>)
 * @author Dirk Rasmussen (<a href="mailto:d.rasmussen@bevis.de">d.rasmussen@bevis.de</a>)
 * @author Andowson Chang
 * @version $Id$
 */
public class SqlServer2000TopicDAO extends GenericTopicDAO
{
	/**
	 * @see net.jforum.dao.TopicDAO#selectAllByForumByLimit(int, int, int)
	 */
	public List<Topic> selectAllByForumByLimit(int forumId, int startFrom, int count)
    {
        String sql = SystemGlobals.getSql("TopicModel.selectAllByForumByLimit");
        sql = sql.replaceAll("%d", String.valueOf(startFrom + count));
        
        PreparedStatement pstmt = null;

        try {
            pstmt = JForumExecutionContext.getConnection().prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pstmt.setInt(1, forumId);
            pstmt.setInt(2, forumId);

            return this.fillTopicsDataByLimit(pstmt, startFrom);
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
        finally {
            DbUtils.close(pstmt);
        }
    }

	/**
	 * @see net.jforum.dao.TopicDAO#selectByUserByLimit(int, int, int)
	 */
	public List<Topic> selectByUserByLimit(int userId, int startFrom, int count)
	{
        String sql = SystemGlobals.getSql("TopicModel.selectByUserByLimit");        
        sql = sql.replaceAll("%d", String.valueOf(startFrom + count));
        
        PreparedStatement pstmt = null;
        try {
            pstmt = JForumExecutionContext.getConnection().prepareStatement(
                    sql.replaceAll(":fids:",
                            ForumRepository.getListAllowedForums()), 
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);

            pstmt.setInt(1, userId);

            return this.fillTopicsDataByLimit(pstmt, startFrom);            
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
        finally {
            DbUtils.close(pstmt);
        }
    }

    /**
     * @see net.jforum.dao.TopicDAO#selectRecentTopics(int)
     */
    public List<Topic> selectRecentTopics(int limit)
    {
        String sql = SystemGlobals.getSql("TopicModel.selectRecentTopicsByLimit");
        sql = sql.replaceAll("%d", String.valueOf(limit));
        
        PreparedStatement pstmt = null;
        try {
            pstmt = JForumExecutionContext.getConnection().prepareStatement(sql);

            List<Topic> list = this.fillTopicsData(pstmt);
            return list;
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
        finally {
            DbUtils.close(pstmt);
        }
    }
    
    /**
     * @see net.jforum.dao.TopicDAO#selectHottestTopics(int)
     */
    public List<Topic> selectHottestTopics(int limit)
    {
        String sql = SystemGlobals.getSql("TopicModel.selectHottestTopicsByLimit");
        sql = sql.replaceAll("%d", String.valueOf(limit));
        
        PreparedStatement pstmt = null;
        try {
            pstmt = JForumExecutionContext.getConnection().prepareStatement(sql);
      
            return this.fillTopicsData(pstmt);            
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
        finally {
            DbUtils.close(pstmt);
        }    
    }
    
	/**
		 * Fills all topic data. The method will try to get all fields from the
		 * topics table, as well information about the user who made the first
		 * and the last post in the topic. <br>
		 * <b>The method <i>will</i> close the <i>PreparedStatement</i></b>
		 * 
		 * @param pstmt
		 *            the PreparedStatement to execute
		 * @return A list with all topics found
		 * @throws SQLException
		 */
	private List<Topic> fillTopicsDataByLimit(PreparedStatement pstmt, int startFrom) {
		List<Topic> l = new ArrayList<Topic>();
		PreparedStatement pstmt2 = null;

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
			rs.absolute(startFrom);

			SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT), Locale.getDefault());

			StringBuffer sbFirst = new StringBuffer(128);
			StringBuffer sbLast = new StringBuffer(128);

			while (rs.next()) {
				Topic topic = this.getBaseTopicData(rs);

				// Posted by
				User user = new User();
				user.setId(rs.getInt("user_id"));
				topic.setPostedBy(user);

				// Last post by
				user = new User();
				user.setId(rs.getInt("last_user_id"));
				topic.setLastPostBy(user);

				topic.setHasAttach(rs.getInt("attach") > 0);
				topic.setFirstPostTime(df.format(rs.getTimestamp("topic_time")));
				topic.setLastPostTime(df.format(rs.getTimestamp("post_time")));
				topic.setLastPostDate(new Date(rs.getTimestamp("post_time").getTime()));

				l.add(topic);

				sbFirst.append(rs.getInt("user_id")).append(',');
				sbLast.append(rs.getInt("last_user_id")).append(',');
			}

			rs.close();

			// Users
			if (sbFirst.length() > 0) {
				sbLast.delete(sbLast.length() - 1, sbLast.length());

				String sql = SystemGlobals
						.getSql("TopicModel.getUserInformation");
				sql = sql.replaceAll("#ID#", sbFirst.toString()
						+ sbLast.toString());

				Map<Integer, String> users = new HashMap<Integer, String>();

				pstmt2 = JForumExecutionContext.getConnection().prepareStatement(sql);
				rs = pstmt2.executeQuery();

				while (rs.next()) {
					users.put(Integer.valueOf(rs.getInt("user_id")), rs.getString("username"));
				}

				for (Iterator<Topic> iter = l.iterator(); iter.hasNext();) {
					Topic topic = (Topic) iter.next();
					topic.getPostedBy().setUsername(users.get(Integer.valueOf(topic.getPostedBy().getId())));
					topic.getLastPostBy().setUsername(users.get(Integer.valueOf(topic.getLastPostBy().getId())));
				}
			}

			return l;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(rs);
			DbUtils.close(pstmt2);
		}
	}
}
