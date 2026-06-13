package cn.jackbin.SimpleRecord.common.utils;

import cn.jackbin.SimpleRecord.constant.CodeMsg;
import cn.jackbin.SimpleRecord.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额处理工具类
 * <p>
 * 提供金额的正数校验和小数精度归一化，确保各记账类型（收入、支出、借贷、转账）
 * 在处理金额时行为一致。
 * </p>
 * <ul>
 *   <li>金额必须大于零（不允许零或负数）</li>
 *   <li>小数位数不超过 {@link #MAX_SCALE} 位，超出部分按四舍五入处理</li>
 * </ul>
 */
public final class AmountUtils {

    /**
     * 金额允许的最大小数位数
     */
    public static final int MAX_SCALE = 2;

    private AmountUtils() {
        // 工具类禁止实例化
    }

    /**
     * 校验并归一化金额。
     * <p>
     * 1. 校验金额不能为 null、零或负数；<br/>
     * 2. 将金额四舍五入到 {@link #MAX_SCALE} 位小数后返回。
     * </p>
     *
     * @param amount 原始金额
     * @return 归一化后的金额（Double）
     * @throws BusinessException 金额为 null 或非正数时抛出
     */
    public static Double validateAndNormalize(Double amount) {
        validate(amount);
        return normalize(amount);
    }

    /**
     * 校验金额是否为合法正数。
     *
     * @param amount 原始金额
     * @throws BusinessException 金额为 null 或非正数时抛出
     */
    public static void validate(Double amount) {
        if (amount == null) {
            throw new BusinessException(CodeMsg.AMOUNT_MUST_BE_POSITIVE);
        }
        BigDecimal bd = new BigDecimal(Double.toString(amount));
        if (bd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CodeMsg.AMOUNT_MUST_BE_POSITIVE);
        }
    }

    /**
     * 将金额四舍五入到 {@link #MAX_SCALE} 位小数。
     *
     * @param amount 原始金额（需已通过 {@link #validate(Double)} 校验）
     * @return 归一化后的金额
     */
    public static Double normalize(Double amount) {
        BigDecimal bd = new BigDecimal(Double.toString(amount));
        return bd.setScale(MAX_SCALE, RoundingMode.HALF_UP).doubleValue();
    }
}
