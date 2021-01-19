package tom.eyre.mp2021.data;

import lombok.Data;

@Data
public class QuestionsByType {

    private String type;
    private int totalVotes;
    private int voteFor;
    private int voteAgainst;
    private int voteAbstained;
    private int voteDidNotVote;
    private int agreedWithUser;
    private int noRecord;
}
