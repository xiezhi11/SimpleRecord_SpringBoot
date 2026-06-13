package cn.jackbin.SimpleRecord.service.impl;

import cn.jackbin.SimpleRecord.bo.RecordDetailBO;
import cn.jackbin.SimpleRecord.constant.CodeMsg;
import cn.jackbin.SimpleRecord.constant.RecordConstant;
import cn.jackbin.SimpleRecord.entity.DictDO;
import cn.jackbin.SimpleRecord.entity.DictItemDO;
import cn.jackbin.SimpleRecord.entity.RecordAccountDO;
import cn.jackbin.SimpleRecord.entity.RecordDetailDO;
import cn.jackbin.SimpleRecord.exception.BusinessException;
import cn.jackbin.SimpleRecord.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author: create by bin
 * @version: v1.0
 * @description: 支出记账策略
 * @date: 2021/10/8 20:58
 **/
@Component
public class ExpendRecordDetail implements RecordDetailHandler {
    @Autowired
    private RecordDetailFactory factory;

    @Autowired
    private RecordDetailService recordDetailService;

    @Autowired
    private RecordAccountService recordAccountService;

    @Autowired
    private DictService dictService;

    @Autowired
    private DictItemService dictItemService;

    @PostConstruct
    public void init(){
        factory.addHandler(EXPEND_TYPE, this);
    }

    @Override
    public void handleAdd(Integer userId, RecordDetailBO bo) {
        recordDetailService.add(userId, bo.getTargetAccountId(), bo.getRecordBookId(), bo.getRecordTypeId(), bo.getRecordCategory(),
                -bo.getAmount(), bo.getOccurTime(), bo.getTag(), bo.getRemark(), bo.getRecoverableStatus());
    }

    @Override
    public void handleUpdate(RecordDetailBO bo) {
        recordDetailService.update(bo.getId(), bo.getTargetAccountId(), bo.getRecordBookId(), bo.getRecordTypeId(), bo.getRecordCategory(),
                -bo.getAmount(), bo.getOccurTime(), bo.getTag(), bo.getRemark(), bo.getRecoverableStatus());
    }

    @Override
    public void handleDel(RecordDetailDO recordDetailDO) {
        recordDetailService.removeById(recordDetailDO.getId());
    }

    @Override
    public void check(Integer userId, RecordDetailBO recordDetailBO) {
        // 目标账户不能为应收/应付账户
        RecordAccountDO targetAccount = recordAccountService.getById(recordDetailBO.getTargetAccountId());
        String accountType = dictItemService.getById(targetAccount.getType()).getValue();
        if (RecordConstant.PAYMENT_ACCOUNT.equals(accountType)){
            throw new BusinessException(CodeMsg.TARGET_RECORD_ACCOUNT_NOT_PAYMENT);
        }
    }

    /**
     * 批量报销
     */
    @Transactional
    public void recoverRecords(Integer userId, List<Long> ids){
        // 入参校验：待报销列表不能为空
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(CodeMsg.PARAMETER_ISNULL);
        }
        // 过滤重复 id，避免同一条记录被重复报销
        List<Long> distinctIds = ids.stream().distinct().collect(Collectors.toList());
        // 查询记录，数量不一致说明存在查不到对应数据的 id
        List<RecordDetailDO> list = recordDetailService.listByIds(distinctIds);
        if (list.size() != distinctIds.size()) {
            throw new BusinessException(CodeMsg.NOT_FIND_DATA);
        }
        // 归属校验：禁止报销他人的记账记录
        boolean existsOthers = list.stream().anyMatch(n -> !Objects.equals(n.getUserId(), userId));
        if (existsOthers) {
            throw new BusinessException(CodeMsg.OPERATE_RECORD_FORBIDDEN);
        }
        // 状态校验：仅待报销状态的记录可以报销
        boolean existsNotRecoverable = list.stream()
                .anyMatch(n -> !Objects.equals(n.getRecoverableStatus(), RecordConstant.TO_RECOVERABLE));
        if (existsNotRecoverable) {
            throw new BusinessException(CodeMsg.RECORD_NOT_RECOVERABLE);
        }
        // 获取dictDO
        DictDO dictDO = dictService.getByCode(RecordConstant.RECORD_TYPE);
        // 从字典获取recordType
        DictItemDO dictItemDO = dictItemService.getByValue(dictDO.getId().intValue(), INCOME_TYPE);
        AtomicReference<Double> total = new AtomicReference<>(0.0);
        AtomicInteger count = new AtomicInteger();
        list.forEach(n -> {
            total.updateAndGet(v -> v + Math.abs(n.getAmount()));
            count.getAndIncrement();
        });
        RecordDetailDO recoverableRecord = new RecordDetailDO();
        recoverableRecord.setUserId(userId);
        recoverableRecord.setRecordAccountId(list.get(0).getRecordAccountId());
        recoverableRecord.setRecordBookId(list.get(0).getRecordBookId());
        recoverableRecord.setRecordType(dictItemDO.getId().intValue());
        recoverableRecord.setRecordCategory(RecordConstant.BXK);
        recoverableRecord.setAmount(Math.abs(total.get()));
        recoverableRecord.setOccurTime(new Date());
        recoverableRecord.setRemark(buildRecoverableRemark(count.get(), total.get()));
        recordDetailService.save(recoverableRecord);
        // 修改为已报销的状态
        list.forEach(n -> n.setRecoverableStatus(RecordConstant.IS_RECOVERABLE));
        recordDetailService.updateBatchById(list);

    }

    private String buildRecoverableRemark(int count, double amount){
        return "报销款：共报销" + count + "笔账单，总金额为" + amount + "¥";
    }
}
