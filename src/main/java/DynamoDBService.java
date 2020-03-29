import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DynamoDBService {
    private static AWSCredential myCredential = new AWSCredential();
    private static AmazonDynamoDB client = AmazonDynamoDBClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(myCredential.getCredentials()))
            .withRegion(myCredential.getRegion())
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(client);

    private static final int expirationTimeSec = 3600;

    private static String tableName = System.getenv("TABLE_NAME");

    public void createItem(String username, String bills) {
        Table table = dynamoDB.getTable(tableName);
        long ut = System.currentTimeMillis() / 1000;

        try {
            Item item = new Item().withPrimaryKey("Username", username)
                    .withLong("TTL", ut + expirationTimeSec)
                    .withJSON("Message", bills);
            table.putItem(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsItem(String username) {
        Table table = dynamoDB.getTable(tableName);
        try {

            Item item = table.getItem("Username", username, "Username, TTL, Message", null);
            return item == null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
