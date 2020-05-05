import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public class Client2 extends Socket {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8889;
    private static final String END_MARK = "quit";

    private Socket client;
    private Writer writer;
    private BufferedReader in;

    public Client2() throws Exception {
        super(SERVER_IP, SERVER_PORT);
        this.client = this;
        System.out.println("You are now connecting from port " + client.getLocalPort());
    }

    public void load() throws Exception {
        this.writer = new OutputStreamWriter(this.getOutputStream(), "UTF-8");
        new Thread(new ReceiveMsgTask()).start();

        while(true) {
            in = new BufferedReader(new InputStreamReader(System.in));
            String inputMsg = in.readLine();
            writer.write(inputMsg);
            writer.write("\n");
            writer.flush();
        }
    }

    class ReceiveMsgTask implements Runnable {

        private BufferedReader buff;

        @Override
        public void run() {
            try {
                this.buff = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                while (true) {
                    String result = buff.readLine();
                    if (END_MARK.equals(result)) {
                        System.out.println("You are now disconnected from port " + client.getLocalPort());
                        break;
                    } else {
                        String[] resultArray = result.split(" ");
                        if (!resultArray[0].equals(Integer.toString(client.getLocalPort()))) System.out.println(result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    buff.close();
                    writer.close();
                    client.close();
                    in.close();
                } catch (Exception e) {

                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Client2 client = new Client2();
            client.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}