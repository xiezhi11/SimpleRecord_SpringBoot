package cn.jackbin.SimpleRecord.service;

import cn.jackbin.SimpleRecord.bo.PageBO;
import cn.jackbin.SimpleRecord.entity.CommonLogDO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jackbin
 * @since 2020-07-21
 */
public interface CommonLogService extends IService<CommonLogDO> {

    /**
     * 分页查询操作日志（支持按业务类型、标题关键词、操作时间范围组合查询）
     *
     * @param businessTypeCode 业务类型编码（精确匹配，可为空）
     * @param title            操作标题关键词（模糊匹配，可为空）
     * @param beginTime        操作开始时间（可为空）
     * @param endTime          操作结束时间（可为空）
     * @param pageBO           分页参数及结果载体
     */
    void getByPage(String businessTypeCode, String title, String beginTime, String endTime, PageBO<CommonLogDO> pageBO);
}
