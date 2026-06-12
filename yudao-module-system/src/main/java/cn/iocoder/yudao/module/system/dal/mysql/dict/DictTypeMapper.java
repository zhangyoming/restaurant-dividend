package cn.iocoder.yudao.module.system.dal.mysql.dict;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictTypeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
//@Mapper 是 MyBatis 的注解 @Mapper = 这是数据库操作接口
//告诉 MyBatis：这是一个 Mapper 接口，请帮我生成它的实现类 这个 Mapper 操作的是 DictTypeDO 对应的数据库表。
public interface DictTypeMapper extends BaseMapperX<DictTypeDO> {
//    BaseMapperX 应该是芋道在 MyBatis-Plus 基础上封装的增强版 Mapper
//    继承basemapper，父类的通用数据库方法也继承了，不用写基础的mapper接口和实现方法了
//    Java 8 以后，接口里可以写 default 方法
    default PageResult<DictTypeDO> selectPage(DictTypePageReqVO reqVO) {
//        不用 XML，也不用实现类，就能封装常用查询逻辑
        return selectPage(reqVO, new LambdaQueryWrapperX<DictTypeDO>()
//                芋道封装的查询条件构造器 用 Java 代码拼 SQL 条件
                .likeIfPresent(DictTypeDO::getName, reqVO.getName())
//                如果 reqVO.getName() 不为空，就添加 name 模糊查询条件；如果为空，就不加这个条件
                .likeIfPresent(DictTypeDO::getType, reqVO.getType())
//                如果 status 不为空，就添加 status 等于条件
                .eqIfPresent(DictTypeDO::getStatus, reqVO.getStatus())
//                如果 createTime 时间范围不为空，就添加创建时间范围查询
                .betweenIfPresent(DictTypeDO::getCreateTime, reqVO.getCreateTime())
//                按 id 倒序排列
                .orderByDesc(DictTypeDO::getId));
    }

    default DictTypeDO selectByType(String type) {
        return selectOne(DictTypeDO::getType, type);
    }

    default DictTypeDO selectByName(String name) {
        return selectOne(DictTypeDO::getName, name);
    }
//    逻辑删除方法 updateToDelete
    @Update("UPDATE system_dict_type SET deleted = 1, deleted_time = #{deletedTime} WHERE id = #{id}")
//    @Param 的作用是给 SQL 参数起名字
    void updateToDelete(@Param("id") Long id, @Param("deletedTime") LocalDateTime deletedTime);

}
