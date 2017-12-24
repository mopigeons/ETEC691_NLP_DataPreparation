package nlp.course.project.dataprep;

import java.util.List;

public class TopicStances {
	private int topicId;
	private List<StanceVotes> stanceVotes;
	private int maxStanceId=-1;
	private double stanceProportion;
	
	public int getMaxStanceId() {
		return maxStanceId;
	}

	
	
	public double getStanceProportion() {
		return stanceProportion;
	}


	public TopicStances(final int topicId, List<StanceVotes> stanceVotes) {
		this.topicId = topicId;
		this.stanceVotes = stanceVotes;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public List<StanceVotes> getStanceVotes() {
		return stanceVotes;
	}

	public void setStanceVotes(List<StanceVotes> stanceVotes) {
		this.stanceVotes = stanceVotes;
	}
	
	public void addStanceVotesToList(StanceVotes sv) {
		this.stanceVotes.add(sv);
	}
	
	public boolean isStanceInList(int stanceId) {
		for(StanceVotes sv : stanceVotes) {
			if(sv.getStanceId()==stanceId) {
				return true;
			}
		}
		return false;
	}
	
	public void incrementVoteCountForStance(int stanceId, int votes) {
		for(StanceVotes sv : stanceVotes) {
			if(sv.getStanceId()==stanceId) {
				int prevCount = sv.getVoteCount();
				sv.setVoteCount(prevCount+votes);
			}
		}
	}
	
	public int calculateStance() {
		int maxStanceId = -1;
		int maxVotes= 0;
		int totalVotes = 0;
		for(StanceVotes sv : stanceVotes) {
			int currentVoteCount = sv.getVoteCount();
			totalVotes += currentVoteCount;
			if(currentVoteCount>maxVotes) {
				maxVotes = currentVoteCount;
				maxStanceId = sv.getStanceId();
			}
		}
		if(maxStanceId>=0) {
			this.maxStanceId = maxStanceId;
			this.stanceProportion = (double)maxVotes/(double)totalVotes;
		}
		return this.maxStanceId;
	}

}
