import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server extends ServerSocket {

    private static final int SERVER_PORT = 8889;
    private static final String END_MARK = "quit";

    private static List<String> userList = new CopyOnWriteArrayList<String>();
    private static List<Task> threadList = new ArrayList<Task>();
    private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
            10);

    public Server() throws Exception {
        super(SERVER_PORT);
    }

    // start server and deal with stream from client asynchronously
    public void load() throws Exception {
        // start thread which can send msg to client
        new Thread(new PushMsgTask()).start();

        while (true) {
            Socket socket = this.accept();
            new Thread(new Task(socket)).start();
        }
    }

    // take msg from msg queue and send msg to all clients in the list
    class PushMsgTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                String msg = null;
                try {
                    msg = msgQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    for (Task thread : threadList) {
                        thread.sendMsg(msg);
                    }
                }
            }
        }

    }

    //server thread to deal with msg from clients
    class Task implements Runnable {
        private Socket socket;
        private BufferedReader buff;
        private Writer writer;
        private String userName;

        public Task(Socket socket) {
            this.socket = socket;
            this.userName = String.valueOf(socket.getPort());
            try {
                this.buff = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), "UTF-8"));
                this.writer = new OutputStreamWriter(socket.getOutputStream(),
                        "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            userList.add(this.userName);
            threadList.add(this);
            pushMsg(socket.getPort() + " is connecting at "
                    + new SimpleDateFormat("dd/MM/yyyy/HH:mm:ss")
                    .format(new Date()));
            System.out.println("Client is connecting from port "
                    + socket.getPort() + " at "
                    + new SimpleDateFormat("dd/MM/yyyy/HH:mm:ss")
                    .format(new Date()));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msg = buff.readLine();

                    if (END_MARK.equals(msg)) {
                        sendMsg(END_MARK);
                        break;
                    } else {
                        pushMsg(String.format("%1$s says：'%2$s'", userName, msg) +
                                " at " +
                                new SimpleDateFormat("dd/MM/yyyy/HH:mm:ss")
                                        .format(new Date()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                    buff.close();
                    socket.close();
                } catch (Exception e) {

                }
                userList.remove(userName);
                threadList.remove(this);
                pushMsg(socket.getPort() + " disconnected from port at "
                        + new SimpleDateFormat("dd/MM/yyyy/HH:mm:ss")
                        .format(new Date()));
                System.out.println("Client disconnected from port "
                        + socket.getPort() + " at "
                        + new SimpleDateFormat("dd/MM/yyyy/HH:mm:ss")
                        .format(new Date()));
            }
        }

        private void pushMsg(String msg) {
            try {
                msgQueue.put(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void sendMsg(String msg) {
            try {
                writer.write(msg);
                writer.write("\015\012");
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Server is starting... Waiting for clients to connect.");
            Server server = new Server();
            server.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}