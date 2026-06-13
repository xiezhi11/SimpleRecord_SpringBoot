package cn.jackbin.SimpleRecord.service.impl;

import cn.jackbin.SimpleRecord.entity.RoleDO;
import cn.jackbin.SimpleRecord.entity.UserRoleDO;
import cn.jackbin.SimpleRecord.mapper.UserRoleMapper;
import cn.jackbin.SimpleRecord.service.RoleService;
import cn.jackbin.SimpleRecord.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private RoleService roleService;

    @Transactional
    @Override
    public void edit(Integer userId, List<Integer> roleIds) {
        // 去重，避免重复插入相同角色
        List<Integer> distinctRoleIds = roleIds == null ? new ArrayList<>() :
                roleIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        // 角色不存在或已停用时抛出明确的业务异常
        roleService.checkRolesAvailable(distinctRoleIds);
        // 删除用户的所有角色
        userRoleMapper.deleteByUserId(userId);
        // 重新分配（空列表表示清空角色）
        if (!distinctRoleIds.isEmpty()) {
            List<UserRoleDO> list = distinctRoleIds.stream()
                    .map(roleId -> new UserRoleDO(userId, roleId))
                    .collect(Collectors.toList());
            saveBatch(list);
        }
    }
}
