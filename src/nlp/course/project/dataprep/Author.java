package nlp.course.project.dataprep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Author {
	private int authorId;
	private List<TopicStances> topicStances;
	
	public Author(int authorId, List<TopicStances> topicStances) {
		this.authorId = authorId;
		this.topicStances = topicStances;
	}
	
	public Author(int authorId) {
		this.authorId = authorId;
		this.topicStances = new ArrayList<TopicStances>();
	}
	
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public List<TopicStances> getTopicStances() {
		return topicStances;
	}
	public void setTopicStances(List<TopicStances> topicStances) {
		this.topicStances = topicStances;
	}
	
	public boolean addTopicToAuthor(int topicId) {
		//if topic already in author, return false
		for(TopicStances topicStance : topicStances) {
			if(topicStance.getTopicId()==topicId) {
				return false;
			}
		}
		topicStances.add(new TopicStances(topicId, new ArrayList<StanceVotes>()));
		return true;
	}
	
	public void addStanceVotesToTopic(final int topicId, final int stanceId, final int votes) {
		if(addTopicToAuthor(topicId)) { //if true, this is a new topic for the author (ie: stances have no votes)
			for(TopicStances topicStance : topicStances) {
				if(topicStance.getTopicId()==topicId) {
					topicStance.addStanceVotesToList(new StanceVotes(stanceId, votes));
				}
			}
		} else { //topic already included in TopicStances list
			for(TopicStances topicStance : topicStances) {
				if(topicStance.getTopicId()==topicId) {
					//does stance already have some votes?
					if(topicStance.isStanceInList(stanceId)) {
						topicStance.incrementVoteCountForStance(stanceId, votes);
					} else {
						topicStance.addStanceVotesToList(new StanceVotes(stanceId, votes));
					}
				}
 			}
		}
	}
	
	public void calculateStances() {
		for(TopicStances ts : topicStances) {
			ts.calculateStance();
		}
	}
	
	
	public Integer getStanceIdForTopic(int topicId, double proportionCutoff) {
		Integer topicStanceId = null;
		for(TopicStances ts : topicStances) {
			if(topicId==ts.getTopicId()) {
				topicStanceId = 
						(ts.getStanceProportion() > proportionCutoff ? ts.getMaxStanceId() : null);
				break;
			}
		}
		return topicStanceId;
	}
}
