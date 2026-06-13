package cn.jackbin.SimpleRecord.service.impl;

import cn.jackbin.SimpleRecord.bo.PageBO;
import cn.jackbin.SimpleRecord.constant.CodeMsg;
import cn.jackbin.SimpleRecord.constant.CommonConstants;
import cn.jackbin.SimpleRecord.constant.RecordConstant;
import cn.jackbin.SimpleRecord.dto.RecordDetailBookSumDTO;
import cn.jackbin.SimpleRecord.dto.SpendCategoryTotalDTO;
import cn.jackbin.SimpleRecord.entity.DictDO;
import cn.jackbin.SimpleRecord.entity.DictItemDO;
import cn.jackbin.SimpleRecord.entity.RecordDetailDO;
import cn.jackbin.SimpleRecord.exception.BusinessException;
import cn.jackbin.SimpleRecord.mapper.RecordDetailMapper;
import cn.jackbin.SimpleRecord.service.DictItemService;
import cn.jackbin.SimpleRecord.service.DictService;
import cn.jackbin.SimpleRecord.service.RecordDetailService;
import cn.jackbin.SimpleRecord.dto.RecordDetailDTO;
import cn.jackbin.SimpleRecord.utils.DateUtil;
import cn.jackbin.SimpleRecord.dto.MonthRecordAnalysisDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jackbin
 * @since 2020-07-21
 */
@Service
public class RecordDetailServiceImpl extends ServiceImpl<RecordDetailMapper, RecordDetailDO> implements RecordDetailService {
    @Autowired
    private RecordDetailMapper recordDetailMapper;
    @Autowired
    private DictItemService dictItemService;
    @Autowired
    private DictService dictService;

    @Override
    public int add(Integer userId, Integer recordAccountId, Integer recordBookId, Integer recordTypeId, String recordCategory, Double amount,
                    Date occurTime, String tag, String remark, Integer recoverableStatus) {
        return add(userId, recordAccountId, null, null, recordBookId, null, recordTypeId, recordCategory,
                amount, occurTime, tag, remark, recoverableStatus);
    }

    @Override
    public int add(Integer userId, Integer recordAccountId, Integer sourceAccountId, Integer targetAccountId, Integer recordBookId,
                   Integer relationRecordId, Integer recordTypeId, String recordCategory, Double amount, Date occurTime,
                   String tag, String remark, Integer recoverableStatus) {
        RecordDetailDO recordDetailDO = new RecordDetailDO();
        recordDetailDO.setUserId(userId);
        recordDetailDO.setRecordAccountId(recordAccountId);
        recordDetailDO.setSourceAccountId(sourceAccountId);
        recordDetailDO.setTargetAccountId(targetAccountId);
        recordDetailDO.setRecordBookId(recordBookId);
        recordDetailDO.setRelationRecordId(relationRecordId);
        recordDetailDO.setRecordType(recordTypeId);
        recordDetailDO.setRecordCategory(recordCategory);
        recordDetailDO.setOccurTime(occurTime);
        recordDetailDO.setAmount(amount);
        recordDetailDO.setTag(tag);
        recordDetailDO.setRemark(remark);
        recordDetailDO.setRecoverableStatus(recoverableStatus);
        recordDetailDO.setStatus(CommonConstants.STATUS_NORMAL);
        if (recordDetailMapper.insert(recordDetailDO) < 1){
            throw new BusinessException(CodeMsg.ADD_DATA_ERROR);
        }
        return recordDetailDO.getId().intValue();
    }

    @Override
    public void update(Long id, Integer recordAccountId, Integer recordBookId, Integer recordTypeId, String recordCategory, Double amount, Date occurTime, String tag, String remark, Integer recoverableStatus) {
        RecordDetailDO recordDetailDO = new RecordDetailDO();
        recordDetailDO.setId(id);
        recordDetailDO.setRecordAccountId(recordAccountId);
        recordDetailDO.setRecordBookId(recordBookId);
        recordDetailDO.setRecordType(recordTypeId);
        recordDetailDO.setRecordCategory(recordCategory);
        recordDetailDO.setOccurTime(occurTime);
        recordDetailDO.setAmount(amount);
        recordDetailDO.setTag(tag);
        recordDetailDO.setRemark(remark);
        recordDetailDO.setRecoverableStatus(recoverableStatus);
        recordDetailMapper.updateById(recordDetailDO);
    }

    @Override
    public void update(Long id, Integer recordBookId, Double amount, Date occurTime, String tag, String remark) {
        RecordDetailDO recordDetailDO = new RecordDetailDO();
        recordDetailDO.setId(id);
        recordDetailDO.setRecordBookId(recordBookId);
        recordDetailDO.setOccurTime(occurTime);
        recordDetailDO.setAmount(amount);
        recordDetailDO.setTag(tag);
        recordDetailDO.setRemark(remark);
        recordDetailMapper.updateById(recordDetailDO);
    }

    @Override
    public RecordDetailDO getByRId(Integer rid) {
        QueryWrapper<RecordDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("relation_record_id", rid);
        return recordDetailMapper.selectOne(queryWrapper);
    }

    @Override
    public void updateRId(Long id, Integer rid) {
        RecordDetailDO recordDetailDO = new RecordDetailDO();
        recordDetailDO.setId(id);
        recordDetailDO.setRelationRecordId(rid);
        recordDetailMapper.updateById(recordDetailDO);
    }

    @Override
    public void removeByRId(Long rid) {
        QueryWrapper<RecordDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("relation_record_id", rid);
        recordDetailMapper.delete(queryWrapper);
    }

    @Override
    public List<Double> getSpendTotalByMonth(Integer userId, Date date) {
        List<Double> list = new ArrayList<>();
        DictDO dictDO = dictService.getByCode(RecordConstant.RECORD_TYPE);
        DictItemDO expendDictItemDO = dictItemService.getByValue(dictDO.getId().intValue(), RecordConstant.EXPEND_RECORD_TYPE);
        DictItemDO incomeDictItemDO = dictItemService.getByValue(dictDO.getId().intValue(), RecordConstant.INCOME_RECORD_TYPE);
        Double expendTotal = recordDetailMapper.queryTotalByMonth(userId, expendDictItemDO.getId().intValue(), date);
        Double incomeTotal = recordDetailMapper.queryTotalByMonth(userId, incomeDictItemDO.getId().intValue(), date);
        list.add(Objects.requireNonNullElse(expendTotal, 0.0));
        list.add(Objects.requireNonNullElse(incomeTotal, 0.0));
        return list;
    }

    @Override
    public List<SpendCategoryTotalDTO> getSpendTotalBySpendCategory(Integer userId, String recordTypeCode, Date date, int begin, int end) {
        DictDO dictDO = dictService.getByCode(RecordConstant.RECORD_TYPE);
        DictItemDO dictItemDO = dictItemService.getByValue(dictDO.getId().intValue(), recordTypeCode);
        return recordDetailMapper.querySpendSpendCategoryTotalByMonth(userId, dictItemDO.getId().intValue(), date, begin, end);
    }

    @Override
    public List<SpendCategoryTotalDTO> getSpendSpendCategoryTotalByYear(Integer userId, String recordTypeCode, Date date) {
        DictDO dictDO = dictService.getByCode(RecordConstant.RECORD_TYPE);
        DictItemDO dictItemDO = dictItemService.getByValue(dictDO.getId().intValue(), recordTypeCode);
        return recordDetailMapper.querySpendSpendCategoryTotalByYear(userId, dictItemDO.getId().intValue(), date);
    }

    @Override
    public void getMonthBookRecords(Integer recordBookId, Integer userId, Date date, Date occurTime, String keyWord, PageBO<RecordDetailDTO> pageBO) {
        Page<RecordDetailDTO> page = new Page<>(pageBO.getPageNo(), pageBO.getPageSize());
        recordDetailMapper.queryByMonthAndBook(page, recordBookId, userId, date, occurTime, keyWord);
        pageBO.setTotal((int) page.getTotal());
        pageBO.setList(page.getRecords());
    }

    @Override
    public void getMonthAccountRecords(Integer recordAccountId, Integer userId, Date date, Date occurTime, String keyWord, PageBO<RecordDetailDTO> pageBO) {
        Page<RecordDetailDTO> page = new Page<>(pageBO.getPageNo(), pageBO.getPageSize());
        recordDetailMapper.queryByMonthAndAccount(page, recordAccountId, userId, date, occurTime, keyWord);
        pageBO.setTotal((int) page.getTotal());
        pageBO.setList(page.getRecords());
    }

    @Override
    public void getRecoverableList(Integer userId, Integer recoverableStatus, PageBO<RecordDetailDTO> pageBO) {
        Page<RecordDetailDTO> page = new Page<>(pageBO.getPageNo(), pageBO.getPageSize());
        recordDetailMapper.queryRecoverableList(page, userId, recoverableStatus);
        pageBO.setTotal((int) page.getTotal());
        pageBO.setList(page.getRecords());
    }

    @Override
    public List<MonthRecordAnalysisDTO> getLatestSixMonthList(Integer userId, String recordTypeCode, Date beginDate, Date endDate) {
        DictDO dictDO = dictService.getByCode(RecordConstant.RECORD_TYPE);
        DictItemDO dictItemDO = dictItemService.getByValue(dictDO.getId().intValue(), recordTypeCode);

        // 查询数据库中的月度统计数据
        List<MonthRecordAnalysisDTO> recordAnalysisDTOS = recordDetailMapper.queryByInterval(userId, dictItemDO.getId().intValue(), beginDate, endDate);

        // 将数据库结果转换为Map，便于快速查找
        Map<String, MonthRecordAnalysisDTO> monthDataMap = new HashMap<>();
        for (MonthRecordAnalysisDTO dto : recordAnalysisDTOS) {
            if (dto != null && dto.getOccurMonth() != null) {
                monthDataMap.put(dto.getOccurMonth(), dto);
            }
        }

        // 生成完整的月份列表
        List<Long> intervalDate = DateUtil.getIntervalTimeByMonth(beginDate, endDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        List<MonthRecordAnalysisDTO> ret = new ArrayList<>();

        // 遍历所有月份，确保每个月都有完整的数据结构
        for (int i = 0; i < intervalDate.size() - 1; i++) {
            Date tempDate = new Date(intervalDate.get(i));
            String monthStr = sdf.format(tempDate);

            MonthRecordAnalysisDTO monthRecord = monthDataMap.get(monthStr);
            if (monthRecord == null) {
                // 缺失月份：创建带有明确月份和默认金额的对象
                monthRecord = new MonthRecordAnalysisDTO(monthStr, 0.0);
            }

            ret.add(monthRecord);
        }

        return ret;
    }

    @Override
    public List<RecordDetailBookSumDTO> getSumByRecordBookIds(Integer recordTypeId, List<Integer> recordBookIds) {
        return recordDetailMapper.querySumByRecordBookIds(recordTypeId, recordBookIds);
    }

    @Override
    public void getListByRecordBookId(Integer userId, Integer recordBookId, PageBO<RecordDetailDO> pageBO) {
        IPage<RecordDetailDO> page = new Page<>(pageBO.getPageNo(), pageBO.getPageSize());//参数一是当前页，参数二是每页个数
        QueryWrapper<RecordDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("record_book_id", recordBookId);
        page = recordDetailMapper.selectPage(page, queryWrapper);
        pageBO.setTotal((int) page.getTotal());
        pageBO.setList(page.getRecords());
    }

    @Override
    public void getListByRecordAccountId(Integer userId, Integer recordAccountId, PageBO<RecordDetailDO> pageBO) {
        IPage<RecordDetailDO> page = new Page<>(pageBO.getPageNo(), pageBO.getPageSize());//参数一是当前页，参数二是每页个数
        QueryWrapper<RecordDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("record_account_id", recordAccountId);
        page = recordDetailMapper.selectPage(page, queryWrapper);
        pageBO.setTotal((int) page.getTotal());
        pageBO.setList(page.getRecords());
    }
}
