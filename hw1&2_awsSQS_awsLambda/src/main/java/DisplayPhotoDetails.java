import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class DisplayPhotoDetails {
    public static void main(String[] args) throws IOException {

        // Step1: create a html file as the output file
        File outputHtml = new File("/Users/lzhang13/Desktop/index.html");
        StringBuilder htmlContent = new StringBuilder("<div>");

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_WEST_1).build();

        // Step2: scan the img table from DynamoDB
        ScanRequest scanRequest = new ScanRequest().withTableName("img");
        ScanResult scanResult = client.scan(scanRequest);

        // Step3: write the html content into the file
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputHtml));
        for (Map<String, AttributeValue> item : scanResult.getItems()) {
            htmlContent.append("<div>" + "<p>" + "<span>" + "img: <img src=\"")
                    .append(item.get("image").getS()).append("\" width=\"500\" height=\"500\">").append("</span>").append("</p>")
                    .append("<p>").append("<span>").append("Photo Name: ").append(item.get("photo name").getS()).append("</span>").append("</p>")
                    .append("<p>").append("<span>").append("Date Taken: ").append(item.get("data taken").getS()).append("</span>").append("</p>")
                    .append("<p>").append("<span>").append("Location: ").append(item.get("location").getS()).append("</span>").append("</p>")
                    .append("<p>").append("<span>").append("Device Used: ").append(item.get("device used").getS()).append("</span>").append("</p>")
                    .append("<br></br>").append("</div>");
        }
        htmlContent.append("/div");
        bufferedWriter.write(htmlContent.toString());
        bufferedWriter.close();
    }
}
