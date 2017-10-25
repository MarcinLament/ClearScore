package lammar.com.csdemo.data.source;

import lammar.com.csdemo.data.CreditScore;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by marcinlament on 02/02/2017.
 */

public interface ClearScoreService {

    String SERVICE_ENDPOINT = "https://5lfoiyb0b3.execute-api.us-west-2.amazonaws.com";

    @GET("/prod/mockcredit/values")
    Observable<CreditScore> getScore();

}
