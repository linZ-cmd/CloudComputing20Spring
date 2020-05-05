import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<S3Event, Void> {

    @Override
    public Void handleRequest(S3Event s3Event, Context context) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion("us-east-2")
                    .build();
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                    .withRegion("us-west-1")
                    .build();

            // Step1: get the object metadata and write into a file
            S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);
            String bucketName = record.getS3().getBucket().getName();
            String objectKey = record.getS3().getObject().getKey().replace('+', ' ');
            S3Object s3Object = s3Client.getObject(bucketName, objectKey);
            S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
            byte[] buf = new byte[1024];
            int count;
            File file = new File("/tmp/" + objectKey);
            OutputStream outputStream = new FileOutputStream(file);
            while( (count = s3ObjectInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, count);
            }
            s3ObjectInputStream.close();
            outputStream.close();

            // Step2: extract details from object metadata using jpegMetaDataReader
            Metadata metadata = JpegMetadataReader.readMetadata(file);
            String latitude = "no latitude info extracted from img";
            String longitude = "no longitude info extracted from img";
            String device = "no device info extracted from img";
            String dateTaken = "no date taken info extracted from img";
            for(Directory directory : metadata.getDirectories()){
                for(Tag tag : directory.getTags()){
                    if (tag.getTagName().equals("GPS Latitude")) latitude = tag.getDescription();
                    if (tag.getTagName().equals("GPS Longitude")) longitude = tag.getDescription();
                    if (tag.getTagName().equals("Model")) device = tag.getDescription();
                    if (tag.getTagName().equals("GPS Date Stamp")) dateTaken = tag.getDescription();
                }
            }

            // Step3: put the details into the DynamoDB, each object as one item
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("photo name", new AttributeValue(objectKey));
            item.put("image", new AttributeValue("https://" +
                    bucketName+ ".s3-us-east-2.amazonaws.com/"
                    + objectKey));
            item.put("location", new AttributeValue("lat:" + latitude + ", long:" + longitude));
            item.put("data taken", new AttributeValue(dateTaken));
            item.put("device used", new AttributeValue(device));
            PutItemRequest putItemRequest = new PutItemRequest("img", item);
            client.putItem(putItemRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
