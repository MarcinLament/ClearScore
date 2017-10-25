package lammar.com.csdemo.data;

/**
 * Created by marcinlament on 01/02/2017.
 */

public class CreditScore {

    public CreditReportInfo creditReportInfo;

    public static class CreditReportInfo{
        public int score;
        public int maxScoreValue;
        public int minScoreValue;
    }
}
