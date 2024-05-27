package mx.unam.fi.distributed.messages.settings;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenInfo {
    @Getter
    private static volatile int currentNodeId = -1;

    private static final Object resourceLock = new Object();

    public static void setCurrentNode(int currentNodeId) {
        synchronized (resourceLock) {
            TokenInfo.currentNodeId = currentNodeId;
        }
    }

}
