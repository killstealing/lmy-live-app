package org.lmy.live.id.generate.provider.service;

public interface IdGenerateService {
    /**
     * 生成有序 id
     *
     * @param code
     * @return
     */
    Long getSeqId(Integer code);

    /**
     * 生成无序 id
     *
     * @param code
     * @return
     */
    Long getUnSeqId(Integer code);
}
