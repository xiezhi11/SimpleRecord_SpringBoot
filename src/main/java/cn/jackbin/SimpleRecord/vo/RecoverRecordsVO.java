package cn.jackbin.SimpleRecord.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @description: 批量报销请求对象
 **/
@ApiModel(value = "RecoverRecordsVO对象", description = "批量报销请求对象")
@Data
public class RecoverRecordsVO {

    @ApiModelProperty(required = true, value = "待报销记录ID列表")
    @NotEmpty(message = "报销记录ID列表不能为空")
    private List<Long> ids;
}
