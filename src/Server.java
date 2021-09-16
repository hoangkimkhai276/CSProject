import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;

public class Server {

    // All client names, so we can check for duplicates upon registration.
    private static HashMap<String, String> ids = new HashMap<>();

    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();

    public Server() {
        System.out.println("The server is running...");
        var pool = Executors.newFixedThreadPool(500);
        try (var listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(ArrayList<String> validIds) {
    }

    private static class Handler implements Runnable {
        private String id;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private Account account;
        private double counter;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting a name until we get a unique one.
                while (true) {
                    boolean isExist = false;
                    boolean isRightFormat = false;
                    boolean isValid = false;
                    id = in.nextLine();
                    if (id.startsWith("id") && id.length() == 5) {
                        for (String validId : Database.validIds) {
                            if (id.equals(validId)) {
                                isValid = true;
                                isExist = true;
                                break;
                            }
                        }
                        try {
                            double d = Double.parseDouble(id.substring(2));
                        } catch (NumberFormatException e) {
                            isValid = false;
                        }
                    }
                    if (!isValid) {
                        out.println("Error: Invalid ID");
                        if(!isExist){
                            out.println("[ID NOT FOUND] in Database, please try again");
                        }else{
                            out.println("[WRONG FORMAT] Please provide another id in format 'idxxx', Where xxx are numeric\n");
                        }
                    } else {
                        account = AccountFactory.makeAccount(id);
                        synchronized (ids) {
                            String password = account.getPassword();
                            if (!ids.containsKey(id)) {
                                ids.put(id, password);
                            } else {
                                if (!ids.get(id).equals(password)) {
                                    System.out.println("Error: Invalid ID");
                                    socket.close();
                                    return;
                                }
                                break;
                            }
                            break;
                        }
                    }
                }

                out.println("IDACCEPTED " + id);
                System.out.println(account);
                Step[] steps = account.getSteps();
                int delay = account.getDelay();
                System.out.println("Steps and delay");
                System.out.println(Arrays.toString(steps) + " " + delay);
                for (Step s : steps){
                    if (s.isIncrease()){
                        counter += s.getAmount();
                    }else{
                        counter -= s.getAmount();
                    }
                   out.println("Current counter: " + counter);
                    Thread.sleep(delay * 1000L);
                }
                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("quit")) {
                        System.out.println("Log out");
                        ids.remove(id);
                        socket.close();
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}