package nlp.course.project.dataprep;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DataPreparation {
	
	private static final String PROPERTIES_FILE = "./project.properties";
	private static final String PROPNAME_DB_ADDRESS = "DB_ADDRESS";
	private static final String PROPNAME_DB_USER = "DB_USER";
	private static final String PROPNAME_DB_PASSWORD = "DB_PASSWORD";
	private static final String PROPNAME_OUTPUT_PATH = "FILE_OUTPUT_PATH";
	
	private static final String OUTPUT_DATA_FILE_PREFIX = "topic_";
	
	
	protected static String host;
	protected static String user;
	protected static String pass;
	protected static String outputFilePath;
	static {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			host = prop.getProperty(PROPNAME_DB_ADDRESS);
			user = prop.getProperty(PROPNAME_DB_USER);
			pass = prop.getProperty(PROPNAME_DB_PASSWORD);
			outputFilePath = prop.getProperty(PROPNAME_OUTPUT_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input!=null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static final double PROPORTION_CUTOFF = 0.5;
	
	protected static final int other_stance_id = 9999;
	
	/**
	 * Prepares and returns a map in the format <Topic_Id, <list of annotated forum posts>>
	 * @return
	 * @throws SQLException if there is an error querying the DB
	 */
	private static Map<Integer, List<AnnotatedForumPost>> generateDataMap() throws SQLException {
		
		List<Author> authorList = AuthorTopicStanceMap.getAuthorList();

		//query for posts
		//convince me
		String convinceMeQuery = "SELECT author_id, text, topic, T1.topic_id, topic_stance_id FROM "
				+ "convinceme.topic_stance RIGHT JOIN (SELECT author_id, text, post_view.topic, topic_id, "
				+ "stance FROM convinceme.post_view LEFT JOIN convinceme.topic ON topic.topic = "
				+ "post_view.topic WHERE post_view.topic IS NOT NULL) T1 on T1.stance=topic_stance.stance;";
		//create debate
		String createDebateQuery = "SELECT author_id, text, topic, T1.topic_id, topic_stance_id FROM "
				+ "createdebate.topic_stance RIGHT JOIN (SELECT author_id, text, post_view.topic, topic_id, "
				+ "stance FROM createdebate.post_view LEFT JOIN createdebate.topic ON topic.topic = "
				+ "post_view.topic WHERE post_view.topic IS NOT NULL) T1 on T1.stance=topic_stance.stance;";
		//four forums
		String fourForumsQuery = "SELECT author_id, text, post_view.topic, topic_id FROM "
				+ "fourforums.post_view LEFT JOIN fourforums.topic ON post_view.topic = topic.topic WHERE "
				+ "post_view.topic IS NOT NULL;";
			//note: use author objects to annotate with stance as the posts are not directly annotated
		
		
		List<AnnotatedForumPost> fullPostList = new ArrayList<AnnotatedForumPost>();
		
		try(Connection conn = DriverManager.getConnection(host, user, pass)) {
			try(Statement stmt = conn.createStatement()) {
				try(ResultSet rs = stmt.executeQuery(convinceMeQuery)) {
					while(rs.next()) {
						int authorId = rs.getInt("author_id");
						String text = rs.getString("text");
						int topicId = rs.getInt("topic_id");
						int topicStanceId = rs.getInt("topic_stance_id");
						AnnotatedForumPost afp = new AnnotatedForumPost(authorId, text, topicId, topicStanceId);
						fullPostList.add(afp);
					}
				}
				try(ResultSet rs = stmt.executeQuery(createDebateQuery)) {
					while(rs.next()) {
						int authorId = rs.getInt("author_id");
						String text = rs.getString("text");
						int topicId = rs.getInt("topic_id");
						int topicStanceId = rs.getInt("topic_stance_id");
						AnnotatedForumPost afp = new AnnotatedForumPost(authorId, text, topicId, topicStanceId);
						fullPostList.add(afp);
					}
				}
				try(ResultSet rs = stmt.executeQuery(fourForumsQuery)) {
					while(rs.next()) {
						int authorId = rs.getInt("author_id");
						String text = rs.getString("text");
						int topicId = rs.getInt("topic_id");
						Integer topicStanceId = getTopicStanceId(authorId, topicId, authorList);
						AnnotatedForumPost afp = new AnnotatedForumPost(authorId, text, topicId, topicStanceId);
						fullPostList.add(afp);
					}
				}
			}
		}
		
		Map<Integer, List<AnnotatedForumPost>> afpMap = arrangePostListsByTopic(fullPostList);
		return afpMap;
	}
	
	private static Integer getTopicStanceId(int authorId, int topicId, List<Author> authorList) {
		Integer topicStanceId = null;
		for(Author author : authorList) {
			if(author.getAuthorId()==authorId) {
				topicStanceId = author.getStanceIdForTopic(topicId, PROPORTION_CUTOFF);
				break;
			}
		}
		return topicStanceId;
	}
	
	private static Map<Integer, List<AnnotatedForumPost>> arrangePostListsByTopic(List<AnnotatedForumPost> postList) {
		Map<Integer, List<AnnotatedForumPost>> afpByTopic = new HashMap<Integer,List<AnnotatedForumPost>>();
		for(AnnotatedForumPost afp : postList) {
			Integer currentTopicId = afp.getTopicId();
			if(afpByTopic.containsKey(currentTopicId)) {
				afpByTopic.get(currentTopicId).add(afp);
			}
			else {
				List<AnnotatedForumPost> afpList = new ArrayList<AnnotatedForumPost>();
				afpList.add(afp);
				afpByTopic.put(currentTopicId, afpList);
			}
		}
		return afpByTopic;
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		Map<Integer, List<AnnotatedForumPost>> afpMap = generateDataMap();
		for(Map.Entry<Integer, List<AnnotatedForumPost>> mapEntry : afpMap.entrySet()) {
			String outputFile = outputFilePath+OUTPUT_DATA_FILE_PREFIX+mapEntry.getKey()+".json";
			try(BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(outputFile)))) {
				for(AnnotatedForumPost afp : mapEntry.getValue()) {
					bw.write(afp.toJSON()+"\n");
				}
			}
			System.out.println(mapEntry.getKey()+" : "+mapEntry.getValue().size());
		}
	}
}
