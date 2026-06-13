package cn.jackbin.SimpleRecord.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * @author: create by bin
 * @version: v1.0
 * @description: cn.jackbin.SimpleRecord.vo
 * @date: 2020/10/15 21:49
 **/
@Data
@NoArgsConstructor
public class RecordDetailVO {
    private Long id;

    @Positive(message = "来源账户须为整数")
    private Integer sourceAccountId;

    @Positive(message = "目标账户须为整数")
    private Integer targetAccountId;

    @Positive(message = "账单Id为整数")
    private Integer recordBookId;

    @NotNull(message = "记账类型不能为空")
    private String recordTypeCode;

    @NotBlank(message = "记账类别不能为空")
    private String recordCategory;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于零")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确，小数位不能超过两位")
    private Double amount;

    @NotNull(message = "日期不能为空")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date occurTime;

    // 是否报销
    private Integer recoverableStatus;

    private String tag;

    private String remark;
}
