
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectCannedAclProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import java.io.*;

public class PhotoUpload_s3 {
    private static boolean checkPhotoType(String keyName) {
        String[] strings = keyName.split("\\.");
        String photoType = strings[strings.length-1];
        return photoType.toLowerCase().equals("jpeg") || photoType.toLowerCase().equals("jpg") ||
                photoType.toLowerCase().equals("gif") || photoType.toLowerCase().equals("png");
    }

    public static void main(String[] args) throws IOException {
        Regions clientRegion = Regions.DEFAULT_REGION;
        String bucketName = "cloudcomputing6225assignment2";
        File outputHtml = new File("/Users/lzhang13/Desktop/index.html");
        String htmlContent = "<div>";

        if (args.length < 1) {
            System.out.println("Please provide the file you want to upload.");
            System.exit(1);
        }

        String filePath = args[0];

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputHtml));
            // Step1: create a s3 bucket as a container to store photo data if it does not exist
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(clientRegion)
                    .build();

            if (!s3Client.doesBucketExistV2(bucketName)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucketName));
            }

            // Step2: upload the file objects into the bucket
            TransferManager xferMgr = TransferManagerBuilder.standard().withS3Client(s3Client).build();
            try {
                ObjectCannedAclProvider objectCannedAclProvider = new ObjectCannedAclProvider() {
                    @Override
                    public CannedAccessControlList provideObjectCannedAcl(File file) {
                        return CannedAccessControlList.PublicRead;
                    }
                };
                MultipleFileUpload xfer = xferMgr.uploadDirectory(bucketName,
                        "", new File(filePath), true, null, null, objectCannedAclProvider);
                xfer.waitForCompletion();
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            xferMgr.shutdownNow(false);


            // Step3: retrieve objects from the container
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(2);
            ListObjectsV2Result result;
            do {
                result = s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    // Step4: recursively write object data into the html file
                    if (!checkPhotoType(objectSummary.getKey())) continue;
                    htmlContent +=
                            "<div>" +
                                "<p>" +
                                    "<span>" + "img: <img src=\"https://" +
                                    bucketName+ ".s3-" + s3Client.getRegionName() + ".amazonaws.com/"
                                    + objectSummary.getKey() + "\" width=\"500\" height=\"500\">" +
                                    "</span>" +
                                "</p>" +
                                "<p>" +
                                    "<span>" + "Photo Name: " + objectSummary.getKey() +
                                    "</span>" +
                                "</p>" +
                                "<p>" +
                                    "<span>" + "Last Modified" + objectSummary.getLastModified() +
                                    "</span>" +
                                "</p>" +
                                "<p>" +
                                    "<span>" + "Size: " + objectSummary.getSize()/1000 + " KB" +
                                    "</span>" +
                                "</p>" +
                                "<br></br>" +
                            "</div>";
                }
                // If there are more than maxKeys keys in the bucket, get a continuation token
                // and list the next objects.
                String token = result.getNextContinuationToken();
                req.setContinuationToken(token);
            } while (result.isTruncated());

            htmlContent += "/div";
            bufferedWriter.write(htmlContent);
            bufferedWriter.close();
        } catch ( SdkClientException | IOException e) {
            e.printStackTrace();
        }
    }
}


