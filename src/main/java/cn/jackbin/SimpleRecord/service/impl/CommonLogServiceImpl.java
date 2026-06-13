package cn.jackbin.SimpleRecord.service.impl;

import cn.jackbin.SimpleRecord.bo.PageBO;
import cn.jackbin.SimpleRecord.entity.CommonLogDO;
import cn.jackbin.SimpleRecord.mapper.CommonLogMapper;
import cn.jackbin.SimpleRecord.service.CommonLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jackbin
 * @since 2020-07-21
 */
@Service
public class CommonLogServiceImpl extends ServiceImpl<CommonLogMapper, CommonLogDO> implements CommonLogService {

    @Autowired
    private CommonLogMapper commonLogMapper;

    @Override
    public void getByPage(String businessTypeCode, String title, String beginTime, String endTime, PageBO<CommonLogDO> pageBO) {
        IPage<CommonLogDO> page = new Page<>(pageBO.getPageNo(), pageBO.getPageSize());
        QueryWrapper<CommonLogDO> queryWrapper = new QueryWrapper<>();

        // 按业务类型精确查询
        if (StringUtils.isNotBlank(businessTypeCode)) {
            queryWrapper.eq("business_type_code", businessTypeCode);
        }

        // 按操作标题关键词模糊查询
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }

        // 按操作时间范围查询
        if (StringUtils.isNotBlank(beginTime)) {
            queryWrapper.ge("create_time", beginTime);
        }
        if (StringUtils.isNotBlank(endTime)) {
            queryWrapper.le("create_time", endTime);
        }

        // 按创建时间倒序排列，最新的日志在前
        queryWrapper.orderByDesc("create_time");

        page = commonLogMapper.selectPage(page, queryWrapper);
        pageBO.setTotal((int) page.getTotal());
        pageBO.setList(page.getRecords());
    }
}
