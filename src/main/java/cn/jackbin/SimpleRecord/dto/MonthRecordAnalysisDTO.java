package cn.jackbin.SimpleRecord.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: create by bin
 * @version: v1.0
 * @description: 月度统计分析DTO
 * @date: 2020/10/28 20:50
 **/
@Data
@NoArgsConstructor
public class MonthRecordAnalysisDTO {
    // 月份（格式：yyyy-MM）
    private String occurMonth;

    // 总额（默认值为0.0，确保缺失月份也有明确的金额）
    private Double total = 0.0;

    public MonthRecordAnalysisDTO(String occurMonth, Double total) {
        this.occurMonth = occurMonth;
        this.total = total != null ? total : 0.0;
    }
}
