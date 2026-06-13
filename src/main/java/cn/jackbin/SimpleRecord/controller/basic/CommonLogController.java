package cn.jackbin.SimpleRecord.controller.basic;


import cn.jackbin.SimpleRecord.bo.PageBO;
import cn.jackbin.SimpleRecord.entity.CommonLogDO;
import cn.jackbin.SimpleRecord.service.CommonLogService;
import cn.jackbin.SimpleRecord.vo.GetCommonLogVO;
import cn.jackbin.SimpleRecord.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jackbin
 * @since 2020-07-21
 */
@Api(value = "CommonLogController", tags = { "日志相关接口" })
@RestController
@RequestMapping("/log")
public class CommonLogController {

    @Autowired
    private CommonLogService commonLogService;

    @ApiOperation(value = "分页查询操作日志")
    @PostMapping("/page")
    public Result<?> getPage(@RequestBody @Validated GetCommonLogVO vo) {
        PageBO<CommonLogDO> pageBO = new PageBO<>(vo.getPageNo(), vo.getPageSize());
        commonLogService.getByPage(vo.getBusinessTypeCode(), vo.getTitle(),
                vo.getBeginTime(), vo.getEndTime(), pageBO);
        return Result.success(pageBO);
    }
}
