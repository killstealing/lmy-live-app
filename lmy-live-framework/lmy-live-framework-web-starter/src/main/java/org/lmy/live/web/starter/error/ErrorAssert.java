package org.lmy.live.web.starter.error;


/**
 * @Author idea
 * @Date: Created in 11:18 2023/8/2
 * @Description
 */
public class ErrorAssert {


    /**
     * 判断参数不能为空
     *
     * @param obj
     * @param lmyBaseError
     */
    public static void isNotNull(Object obj, LmyBaseError lmyBaseError) {
        if (obj == null) {
            throw new LmyErrorException(lmyBaseError);
        }
    }

    /**
     * 判断字符串不能为空
     *
     * @param str
     * @param lmyBaseError
     */
    public static void isNotBlank(String str, LmyBaseError lmyBaseError) {
        if (str == null || str.trim().length() == 0) {
            throw new LmyErrorException(lmyBaseError);
        }
    }

    /**
     * flag == true
     *
     * @param flag
     * @param lmyBaseError
     */
    public static void isTure(boolean flag, LmyBaseError lmyBaseError) {
        if (!flag) {
            throw new LmyErrorException(lmyBaseError);
        }
    }
}
