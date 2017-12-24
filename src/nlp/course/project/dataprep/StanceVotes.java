package nlp.course.project.dataprep;

public class StanceVotes {
	private int stanceId;
	private int voteCount;
	
	public StanceVotes(int stanceId, int voteCount) {
		this.stanceId = stanceId;
		this.voteCount = voteCount;
	}
	
	public int getStanceId() {
		return stanceId;
	}
	public void setStanceId(int stanceId) {
		this.stanceId = stanceId;
	}
	public int getVoteCount() {
		return voteCount;
	}
	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}
}
