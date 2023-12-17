package org.lmy.live.id.generate.interfaces;

public interface IdGenerateRpc {
    Long getSeqId(Integer code);

    /**
     * 生成无序 id
     *
     * @param code
     * @return
     */
    Long getUnSeqId(Integer code);
}
