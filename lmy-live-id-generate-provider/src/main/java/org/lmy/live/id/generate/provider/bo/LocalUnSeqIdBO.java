package org.lmy.live.id.generate.provider.bo;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LocalUnSeqIdBO {
    //mysql 配置的 id
    private int id;
    /**
     * 提前将无序的id存放在这条队列中
     */
    private ConcurrentLinkedQueue<Long> idQueue;
    /**
     * 当前id段的开始值
     */
    private Long currentStart;
    /**
     * 当前id段的结束值
     */
    private Long nextThreshold;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ConcurrentLinkedQueue<Long> getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(ConcurrentLinkedQueue<Long> idQueue) {
        this.idQueue = idQueue;
    }

    public Long getCurrentStart() {
        return currentStart;
    }

    public void setCurrentStart(Long currentStart) {
        this.currentStart = currentStart;
    }

    public Long getNextThreshold() {
        return nextThreshold;
    }

    public void setNextThreshold(Long nextThreshold) {
        this.nextThreshold = nextThreshold;
    }

    @Override
    public String toString() {
        return "LocalUnSeqIdBO{" +
                "id=" + id +
                ", idQueue=" + idQueue +
                ", currentStart=" + currentStart +
                ", nextThreshold=" + nextThreshold +
                '}';
    }
}
