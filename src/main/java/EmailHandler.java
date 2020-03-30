import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.json.JSONObject;
import org.json.JSONTokener;

public class EmailHandler implements RequestHandler<SNSEvent, String> {

    private static AWSCredential myCredential = new AWSCredential();

    private static String HOSTNAME = "csye6225@" + System.getenv("WEB_HOSTNAME");

    public String handleRequest(SNSEvent event, Context context) {
        String record = event.getRecords().get(0).getSNS().getMessage();
        JSONObject recordJson = new JSONObject(new JSONTokener(new JSONObject(record).toString()));

        String username = recordJson.getString("username");
        String bills = recordJson.getJSONArray("bills").toString();

        DynamoDBService dbService = new DynamoDBService();
        if (!dbService.existsItem(username)) {
        sendEmail(username, bills);
        }
        return null;
    }

    private void sendEmail(String username, String message) {
        try {
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(myCredential.getCredentials()))
                        .withRegion(myCredential.getRegion()).build();
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(username))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(message)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData("Bills requested by you")))
                .withSource(HOSTNAME);
        //configuration set not used
        client.sendEmail(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}