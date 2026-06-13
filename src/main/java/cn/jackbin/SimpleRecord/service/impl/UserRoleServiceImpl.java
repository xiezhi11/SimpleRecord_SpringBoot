package cn.jackbin.SimpleRecord.service.impl;

import cn.jackbin.SimpleRecord.constant.CodeMsg;
import cn.jackbin.SimpleRecord.entity.RoleDO;
import cn.jackbin.SimpleRecord.entity.UserRoleDO;
import cn.jackbin.SimpleRecord.exception.BusinessException;
import cn.jackbin.SimpleRecord.mapper.RoleMapper;
import cn.jackbin.SimpleRecord.mapper.UserRoleMapper;
import cn.jackbin.SimpleRecord.service.UserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jackbin
 * @since 2020-07-21
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleDO> implements UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Transactional
    @Override
    public void edit(Integer userId, List<Integer> roleIds) {
        // 空列表：仅清除该用户的所有角色
        if (roleIds == null || roleIds.isEmpty()) {
            userRoleMapper.deleteByUserId(userId);
            return;
        }

        // 校验角色ID列表（重复、存在性、启用状态）
        validateRoleIds(roleIds);

        // 删除用户的所有角色
        userRoleMapper.deleteByUserId(userId);
        // 去重后批量插入
        List<UserRoleDO> list = roleIds.stream()
                .distinct()
                .map(roleId -> new UserRoleDO(userId, roleId))
                .collect(Collectors.toList());
        saveBatch(list);
    }

    /**
     * 校验角色ID列表：
     * 1. 列表内不能有重复ID
     * 2. 每个角色必须存在且未被停用（逻辑删除）
     *
     * @param roleIds 待分配的角色ID列表
     */
    private void validateRoleIds(List<Integer> roleIds) {
        // 1. 检查输入列表中是否有重复的角色ID
        Set<Integer> uniqueIds = new HashSet<>();
        for (Integer roleId : roleIds) {
            if (!uniqueIds.add(roleId)) {
                throw new BusinessException(CodeMsg.ROLE_ASSIGN_DUPLICATE);
            }
        }

        // 2. 逐一校验每个角色的存在性和启用状态
        for (Integer roleId : roleIds) {
            // 使用 selectOneWithoutLogicDel 绕过逻辑删除过滤，以便区分"不存在"与"已停用"
            QueryWrapper<RoleDO> qw = new QueryWrapper<>();
            qw.eq("id", roleId);
            RoleDO role = roleMapper.selectOneWithoutLogicDel(qw);

            if (role == null) {
                // 角色在数据库中完全不存在
                throw new BusinessException(CodeMsg.ROLE_NOT_FOUND);
            }
            if (role.getDeleteTime() != null) {
                // 角色存在但已被逻辑删除（停用）
                throw new BusinessException(CodeMsg.ROLE_DISABLED);
            }
        }
    }
}
