package wang.switchy.hin2n.model;

//
// Created by switchwang(https://github.com/switch-st) on 2018-04-28.
//

public class EdgeStatus {
    public enum RunningStatus {
        CONNECTING,                     // Connecting to N2N network
        CONNECTED,                      // Connect to N2N network successfully
        SUPERNODE_DISCONNECT,           // Disconnect from the supernode
        DISCONNECT,                     // Disconnect from N2N network
        FAILED                          // Fail to connect to N2N network
    }

    public RunningStatus runningStatus;
}
