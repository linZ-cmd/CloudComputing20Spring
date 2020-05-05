
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

public class PhotoUploadS3v2 {

    public static void main(String[] args) {
        Regions clientRegion = Regions.US_EAST_2;
        String bucketName = "cloud6225assignment2";

        if (args.length < 1) {
            System.out.println("Please provide the file you want to upload.");
            System.exit(1);
        }

        String filePath = args[0];

        try {
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

        } catch ( SdkClientException e) {
            e.printStackTrace();
        }
    }
}


