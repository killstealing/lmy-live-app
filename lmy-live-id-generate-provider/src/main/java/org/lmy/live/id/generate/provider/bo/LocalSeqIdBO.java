package org.lmy.live.id.generate.provider.bo;

import java.util.concurrent.atomic.AtomicLong;

public class LocalSeqIdBO {
    //mysql 配置的 id
    private Integer id;
    //对应分布式 id 的配置说明
    private String desc;
    //当前在本地内存的 id 值
    private AtomicLong currentValue;
    //本地内存记录 id 段的开始位置
    private Long currentStart;
    //本地内存记录 id 段的结束位置
    private Long nextThreshold;
    //步长
    private Integer step;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
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

    public AtomicLong getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(AtomicLong currentValue) {
        this.currentValue = currentValue;
    }

    @Override
    public String toString() {
        return "LocalSeqIdBO{" +
                "id=" + id +
                ", desc='" + desc + '\'' +
                ", currentValue=" + currentValue +
                ", currentStart=" + currentStart +
                ", nextThreshold=" + nextThreshold +
                ", step=" + step +
                '}';
    }
}
