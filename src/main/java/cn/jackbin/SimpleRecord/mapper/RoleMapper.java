package cn.jackbin.SimpleRecord.mapper;

import cn.jackbin.SimpleRecord.common.config.datasource.MyBaseMapper;
import cn.jackbin.SimpleRecord.entity.RoleDO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jackbin
 * @since 2020-07-21
 */
@Repository
public interface RoleMapper extends MyBaseMapper<RoleDO> {
    List<RoleDO> queryByUserId(Long userId);

    int queryTotal(String name, Boolean deleted, Date date);

    List<RoleDO> queryByPage(String name, Boolean deleted, Date date, int begin, int end);

    void notDelete(Long id);
}
