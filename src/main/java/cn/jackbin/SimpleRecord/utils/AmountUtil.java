package cn.jackbin.SimpleRecord.utils;

import cn.jackbin.SimpleRecord.constant.CodeMsg;
import cn.jackbin.SimpleRecord.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: create by bin
 * @version: v1.0
 * @description: 记账金额处理工具，统一正数校验与小数精度
 * @date: 2026/6/13
 **/
public class AmountUtil {

    /** 金额保留的小数位数 */
    public static final int SCALE = 2;

    /**
     * 统一处理用户录入的记账金额：校验为大于0的数值，并按固定精度四舍五入。
     * 各记账类型只负责决定金额的正负，录入金额本身必须为正数。
     *
     * @param amount 用户录入的原始金额
     * @return 校验通过并按 {@link #SCALE} 位小数四舍五入后的金额
     */
    public static Double normalize(Double amount) {
        if (amount == null || amount <= 0) {
            throw new BusinessException(CodeMsg.RECORD_AMOUNT_ILLEGAL);
        }
        return BigDecimal.valueOf(amount).setScale(SCALE, RoundingMode.HALF_UP).doubleValue();
    }
}
