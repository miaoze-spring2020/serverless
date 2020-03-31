import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class AWSCredential {
    private static String accessKey = System.getenv("ACCESS_KEY");
    private static String secretKey = System.getenv("SECRET_KEY");
    private static String region = System.getenv("REGION");

    private static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

    public AWSCredentials getCredentials() {
        return credentials;
    }

    public String getRegion(){
        return region;
    }
}
