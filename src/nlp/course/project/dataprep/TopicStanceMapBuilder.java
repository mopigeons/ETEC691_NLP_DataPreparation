package nlp.course.project.dataprep;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class TopicStanceMapBuilder {
	public static Map<String, Integer> topicIds;
	public static Map<Integer, Map<String, Integer>> topicStanceIds;
	static {
		topicIds = new HashMap<String, Integer>();
		topicStanceIds = new HashMap<Integer, Map<String,Integer>>();
		String host = DataPreparation.host;
		String user = DataPreparation.user;
		String pass = DataPreparation.pass;
		try(Connection conn = DriverManager.getConnection(host, user, pass)) {
			try(Statement stmt = conn.createStatement()) {
				String topicIdQuery = "SELECT topic, topic_id FROM convinceme.topic;";
				try(ResultSet rs = stmt.executeQuery(topicIdQuery)) {
					while(rs.next()) {
						String topic = rs.getString("topic");
						int topic_id = rs.getInt("topic_id");
						topicIds.put(topic, topic_id);
					}
				}
				for(Map.Entry<String, Integer> topic : topicIds.entrySet()) {
					String topicStanceQuery = 
							"SELECT topic_stance_id, stance FROM convinceme.topic_stance where topic_id="+topic.getValue()+";";
					Map<String,Integer> stanceMap = new HashMap<String,Integer>();
					try(ResultSet rs2 = stmt.executeQuery(topicStanceQuery)) {
						while(rs2.next()) {
							int topic_stance_id = rs2.getInt("topic_stance_id");
							String stance = rs2.getString("stance");
							stanceMap.put(stance, topic_stance_id);
						}
					}
					topicStanceIds.put(topic.getValue(), stanceMap);
				}
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	
}
