package cn.jackbin.SimpleRecord.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: create by bin
 * @version: v1.0
 * @description: 操作日志查询VO
 **/
@ApiModel(value = "GetCommonLogVO对象", description = "操作日志查询参数")
@Data
public class GetCommonLogVO extends PageVO {

    /**
     * 业务类型编码（精确匹配）
     */
    @ApiModelProperty(value = "业务类型编码，如 query、insert、update、del 等")
    private String businessTypeCode;

    /**
     * 操作标题关键词（模糊匹配）
     */
    @ApiModelProperty(value = "操作标题关键词")
    private String title;

    /**
     * 操作时间范围 - 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    @ApiModelProperty(value = "操作开始时间，格式：yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    /**
     * 操作时间范围 - 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    @ApiModelProperty(value = "操作结束时间，格式：yyyy-MM-dd HH:mm:ss")
    private String endTime;
}
