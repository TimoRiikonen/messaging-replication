
package org.exist.messaging.shared;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wessels
 */


public class JmsStatistics {

    /**
     * Number of messages
     */
    private long messageCounterOK = 0;
    private long messageCounterTotal = 0;
    /**
     * Cumulated time successful messages
     */
    private long totalTime = 0;
    /**
     * Storage for errors
     */
    private List<String> errors = new ArrayList<String>();

    public void incMessageCounterTotal() {
        messageCounterTotal++;
    }

    /**
     * @return Total number of received messages
     */
    public long getMessageCounterTotal() {
        return messageCounterTotal;
    }

    /**
     * @return Total number of NOT successfully received messages
     */
    public long getMessageCounterNOK() {
        return (messageCounterTotal - messageCounterOK);
    }

    public void incMessageCounterOK() {
        messageCounterOK++;
    }

    /**
     * @return Total number of successfully received messages
     */
    public long getMessageCounterOK() {
        return messageCounterOK;
    }

    public void addCumulatedProcessingTime(long time) {
        this.totalTime += time;
    }
    /**
     * @return Total processing time
     */
    public long getCumulatedProcessingTime() {
        return totalTime;
    }

    public void add(String error) {
        errors.add(error);
    }

    /**
     * @return List of problems
     */
    public List<String> getErrors() {
        return errors;
    }
}
