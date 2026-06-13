package cn.jackbin.SimpleRecord.common.config.datasource.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author: create by bin
 * @version: v1.0
 * @description: 查询某个值，排除逻辑删除字段
 * @date: 2021/8/5 19:54
 **/
public class SelectOneWithoutLogicDel extends AbstractMethodWrapper {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        MethodConstant sqlMethod = MethodConstant.SELECT_ONE_WITHOUT_LOGIC_DEL;
        // 构建sql
        String sql = String.format(sqlMethod.getSql(), sqlFirst(),
                sqlSelectColumns(tableInfo, true),
                tableInfo.getTableName(),
                sqlWhereEntityWrapperWithoutLogicDel(true, tableInfo), sqlComment()
        );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, sqlMethod.getMethod(), sqlSource, tableInfo);
    }


}
