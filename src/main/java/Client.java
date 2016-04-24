import java.util.*;

public class Client implements Runnable {
    private Api api;

    public Client(Api api) {
        this.api = api;
    }

    private void processCommand(String command) {
        String out;
        String[] args = command.split("\\s+");
        switch(args[0]) {
            case "join":
                api.join(args[1]);
                api.setStatus(true);
                System.out.println("Joined. You're online, lucky!");
                break;
            case "logoff":
                boolean status = api.getStatus();
                if(status) {
                    api.leave();
                    out = "You're logged off. Bye.";
                } else {
                    out = "Hey! You're already offline. What do you want from me?";
                }
                break;
            case "exit":
                if(api.getStatus()) {
                    api.leave();
                }
                System.out.println("Self-destruction program started. We will never see each other again =( ");
                System.exit(1);
                break;
            case "elections":
                System.out.println("Elections started");
                api.runBully();
                break;
            case "show":
                api.getNetworkMembers();
                System.out.println(api.getNetworkMembers().toString());
                break;
            case "master":
                System.out.println("Master node is " + api.getMasterAddress());
                break;
            case "fun": {
                System.out.println("Having fun right now");
                api.adventureTime(false);
                break;
            }
            case "agrawala": {
                System.out.println("Having agrawalafun right now");
                api.adventureTime(true);
                break;
            }
            case "ms":
                this.api.checkMasterAvaliability();
                System.out.println("Master string is " + api.getMasterString());
                break;
            case "cleanms":
                System.out.println("Cleaned master string");
                api.setMasterString("");
                break;
            default:
                System.out.println("Unknown command. Check the manual");
                break;
        }
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Enter your command");
            String cmd = sc.nextLine();
            try {
                processCommand(cmd);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Something went wrong, please, contact Microsoft support");
            }

        }
    }
}
