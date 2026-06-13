package cn.jackbin.SimpleRecord.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author: create by bin
 * @version: v1.0
 * @description: 操作日志查询对象
 * @date: 2021/7/19 21:39
 **/
@ApiModel(value = "GetCommonLogVO对象", description = "操作日志查询对象")
@EqualsAndHashCode(callSuper = true)
@Data
public class GetCommonLogVO extends PageVO {

    /**
     * 业务类型编码
     */
    @ApiModelProperty(value = "业务类型编码")
    private String businessTypeCode;

    /**
     * 操作标题关键词
     */
    @ApiModelProperty(value = "操作标题关键词")
    private String title;

    /**
     * 操作时间范围-开始
     */
    @ApiModelProperty(value = "操作时间范围-开始（yyyy-MM-dd HH:mm:ss）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    /**
     * 操作时间范围-结束
     */
    @ApiModelProperty(value = "操作时间范围-结束（yyyy-MM-dd HH:mm:ss）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
}
