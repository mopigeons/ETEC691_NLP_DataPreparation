 package nlp.course.project.dataprep;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class AnnotatedForumPost {
	private Integer authorId;
	private List<String> textTokens;
	private Integer topicId;
	private Integer topicStanceId;
	
	
	public AnnotatedForumPost(int authorId, String text, int topicId) {
		this.authorId = authorId;
		generateTextTokens(text);
		this.topicId = topicId;
		this.topicStanceId = null;
	}
	
	public AnnotatedForumPost(int authorId, String text, int topicId, Integer topicStanceId) {
		this.authorId = authorId;
		generateTextTokens(text);
		this.topicId = topicId;
		this.topicStanceId = topicStanceId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public void setText(String text) {
		generateTextTokens(text);
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public Integer getTopicStanceId() {
		return topicStanceId;
	}

	public void setTopicStanceId(Integer topicStanceId) {
		this.topicStanceId = topicStanceId;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Author ID: ").append(authorId).append("\n");
		sb.append("Topic ID: ").append(topicId).append("\n");
		sb.append("Topic Stance ID: ").append(topicStanceId == null ? null : topicStanceId).append("\n");
		sb.append("Text: \n").append(textTokens.toString()).append("\n\n ************************** \n\n");
		return sb.toString();
	}
	
	public void setTextTokens(List<String> tokens) {
		List<String> newTokens = new ArrayList<String>();
		for(String token: tokens) {
			newTokens.add(token);
		}
		this.textTokens = newTokens;
	}
	
	public List<String> getTextTokens() {
		List<String> tokens = new ArrayList<String>();
		for(String token : textTokens) {
			tokens.add(token);
		}
		return tokens;
	}
	
	private void generateTextTokens(String text) {
		List<String> tokens = new ArrayList<String>();
		StringReader sr = new StringReader(text);
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(sr, new CoreLabelTokenFactory(), "untokenizable=allDelete");
		while(ptbt.hasNext()) {
			CoreLabel label = ptbt.next();
			tokens.add(label.toString());
		}
		this.textTokens = tokens;
	}
	
	@SuppressWarnings("unchecked")
	public String toJSON() throws IOException {
		JSONObject obj = new JSONObject();
		Integer authorId = this.authorId == null ? null : this.authorId;
		obj.put("authorId", authorId);
		List<String> textTokens = getTextTokens();
		obj.put("textTokens", textTokens);
		Integer topicId = this.topicId == null ? null : this.topicId;
		obj.put("topicId", topicId);
		Integer topicStanceId = this.topicStanceId == null ? null : this.topicStanceId;
		obj.put("topicStanceId", topicStanceId);
		StringWriter out = new StringWriter();
		obj.writeJSONString(out);
		return out.toString();
	}
	
}
