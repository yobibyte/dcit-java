import org.apache.log4j.PropertyConfigurator;

public class Launcher {
    public static void main (String [] args) {

        PropertyConfigurator.configure("log4j.properties");
        int port = 4444;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        Runnable serv = new Server(port);
        Api a = new ApiImpl((Server) serv);
        Runnable cli = new Client(a);
        Thread servThread = new Thread(serv);
        Thread cliThread = new Thread(cli);

        servThread.start();
        cliThread.start();
    }
}
//Checklist
/*
*# mesh network
*# join operation
*# New machines join the network by sending a join message to one of the machines already in the network.
*#    The address of the new host is thereupon propagated in the network.
*# Hosts also need to be able to sign off from the network again.

*# One node in the network needs to be elected as master node. The master node stores a
string variable that is initially empty. --- DONE
*# Start message
*# The master node needs to be elected by the Bully algorithm.
*# In case the current master node signs off or fails a new master has to be elected.
*# The process takes 20 seconds. During this time all the nodes in the network do the
following: LOOP
    a) Wait a random amount of time
    b) Read the string variable from the master node
    c) Append some random english word to this string
    d) Write the updated string to the master node
    END LOOP
*# After the process has ended all the nodes read the final string from the master node and
write it to the screen.
*# Moreover they check if all the words they added to the string are
present in the final string. The result of this check is also written to the screen.
*# Ricart & Agrawala.
* All hosts have to write all the actions they perform to the screen in order to be able to
retrace the process.
*/