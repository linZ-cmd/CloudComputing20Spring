import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;

import java.util.List;

public class FIFO_sqs_IPAddr {
    public static void main(String[] args) {
        /*
         * Create a new instance of the builder with all defaults (credentials
         * and region) set automatically. For more information, see
         * Creating Service Clients in the AWS SDK for Java Developer Guide.
         */
        final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion("us-east-2").build();

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SQS FIFO Queues");
        System.out.println("===========================================\n");

        // check the number of message from shell
        final int count = args.length;

        try {
            final String myQueueUrl = "https://sqs.us-east-2.amazonaws.com/172374917462/assignment1_IPAddr.fifo";

            // Send a message.
            System.out.println("Sending messages to assignment1_IPAddr.fifo.\n");
            for (int i=0;i<count;i++) {
                final SendMessageRequest sendMessageRequest =
                        new SendMessageRequest(myQueueUrl,args[i]);
                /*
                 * When you send messages to a FIFO queue, you must provide a
                 * non-empty MessageGroupId.
                 */
                sendMessageRequest.setMessageGroupId("IPAddress");
                final SendMessageResult sendMessageResult = sqs
                        .sendMessage(sendMessageRequest);

                final String sequenceNumber = sendMessageResult.getSequenceNumber();
                final String messageId = sendMessageResult.getMessageId();
                System.out.println("SendMessage succeed with messageId "
                        + messageId + ", sequence number " + sequenceNumber + "\n");
            }

            // Receive messages.
            System.out.println("Receiving most first message from assignment1_IPAddr.fifo.\n");
            final ReceiveMessageRequest receiveMessageRequest =
                    new ReceiveMessageRequest(myQueueUrl);

            final List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

            // We only need to retreive the most first IP Address, and since the default maximum retrieve number is 1,
            // the for loop below can only be execute once.
            for (Message mostFirstMessage : messages) {
                System.out.println("Message");
                System.out.println("  MessageId:     "
                        + mostFirstMessage.getMessageId());
                System.out.println("  ReceiptHandle: "
                        + mostFirstMessage.getReceiptHandle());
                System.out.println("  MD5OfBody:     "
                        + mostFirstMessage.getMD5OfBody());
                System.out.println("  Body:          "
                        + mostFirstMessage.getBody());

                // delete the message we just retrieved.
                final DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(myQueueUrl, mostFirstMessage.getReceiptHandle());
                sqs.deleteMessage(deleteMessageRequest);
            }

            System.out.println();
        } catch (final AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means " +
                    "your request made it to Amazon SQS, but was " +
                    "rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (final AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means " +
                    "the client encountered a serious internal problem while " +
                    "trying to communicate with Amazon SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}