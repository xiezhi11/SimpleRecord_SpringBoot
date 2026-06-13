package cn.jackbin.SimpleRecord.service;

import cn.jackbin.SimpleRecord.bo.PageBO;
import cn.jackbin.SimpleRecord.entity.CommonLogDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

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
     * 按业务类型、操作标题关键词、操作时间范围组合分页查询操作日志
     *
     * @param businessTypeCode 业务类型编码
     * @param title            操作标题关键词
     * @param beginTime        操作时间范围-开始
     * @param endTime          操作时间范围-结束
     * @param pageBO           分页对象
     */
    void getByPage(String businessTypeCode, String title, Date beginTime, Date endTime, PageBO<CommonLogDO> pageBO);
}
