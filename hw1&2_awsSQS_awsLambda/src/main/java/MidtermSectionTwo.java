import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.w3c.dom.ls.LSInput;

public class MidtermSectionTwo {
    public static void main(String[] args) {
        if (args.length == 0 || !args[0].equals("my-software.extension")) {
            System.out.println("Please provide the file you want to upload.");
            System.exit(1);
        }

        Regions clientRegion = Regions.US_EAST_2;
        String bucketName = "cloud6225assignment2";

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(clientRegion)
                    .build();
            if (!s3Client.doesBucketExistV2(bucketName)) {
                System.exit(1);
            }

            System.out.println("The following files are available in your s3 repository");
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
            ListObjectsV2Result result;
            do {
                result = s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    System.out.println(objectSummary.getKey());
                }
            } while (result.isTruncated());
        }catch (AmazonServiceException e) {
            e.printStackTrace();
        }

    }
}
