package nlp.course.project.dataprep;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorTopicStanceMap {
	
	private static List<Author> authorList;
	static {
		authorList = new ArrayList<Author>();
		String host = DataPreparation.host;
		String user = DataPreparation.user;
		String pass = DataPreparation.pass;
		//<Author_Id, <Topic_Id, <Stance_Id,#votes>>>
		try(Connection conn = DriverManager.getConnection(host, user, pass)) {
			try(Statement stmt = conn.createStatement()) {
				String query = 
						"SELECT discussion_id, author_id, topic_id,"
						+ "topic_stance_id_1, topic_stance_votes_1,"
						+ "topic_stance_id_2, topic_stance_votes_2,"
						+ "topic_stance_votes_other "
						+ "FROM fourforums.mturk_author_stance;";
				try(ResultSet rs = stmt.executeQuery(query)) {
					while(rs.next()) {
						int authorId = rs.getInt("author_id");
						int topicId = rs.getInt("topic_id");
						int topicStanceId1 = rs.getInt("topic_stance_id_1");
						int topicStanceVotes1 = rs.getInt("topic_stance_votes_1");
						int topicStanceId2 = rs.getInt("topic_stance_id_2");
						int topicStanceVotes2 = rs.getInt("topic_stance_votes_2");
						int topicStanceVotesOther = rs.getInt("topic_stance_votes_other");
						boolean authorInList = false;
						for(Author author : authorList) {
							if(author.getAuthorId()==authorId) {
								authorInList = true;
								author.addStanceVotesToTopic(topicId, topicStanceId1, topicStanceVotes1);
								author.addStanceVotesToTopic(topicId, topicStanceId2, topicStanceVotes2);
								author.addStanceVotesToTopic(topicId, DataPreparation.other_stance_id, topicStanceVotesOther);
							}
						} 
						if(!authorInList) {
							Author author = new Author(authorId);
							author.addStanceVotesToTopic(topicId, topicStanceId1, topicStanceVotes1);
							author.addStanceVotesToTopic(topicId, topicStanceId2, topicStanceVotes2);
							author.addStanceVotesToTopic(topicId, DataPreparation.other_stance_id, topicStanceVotesOther);
							authorList.add(author);
						}
					}
				}	
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Author a: authorList) {
			a.calculateStances();
		}
		
	}
	public static List<Author> getAuthorList() {
		return authorList;
	}
	
	
	
	

}
