package cn.iocoder.yudao.module.system.dal.mysql.dept;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.UserPostDO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;
//用户和岗位关联表的 Mapper。
//它不是管理“岗位本身”的 PostMapper，也不是管理“用户本身”的 UserMapper，而是专门管理：
//
//用户和岗位的绑定关系
@Mapper
public interface UserPostMapper extends BaseMapperX<UserPostDO> {
//    user_id 和 post_id 的对应关系
//根据用户 ID 查询岗位关系
    default List<UserPostDO> selectListByUserId(Long userId) {
        return selectList(UserPostDO::getUserId, userId);
    }

//    删除某个用户的指定岗位绑定 删除某个用户和某些岗位之间的绑定关系。
    default void deleteByUserIdAndPostId(Long userId, Collection<Long> postIds) {
        delete(new LambdaQueryWrapperX<UserPostDO>()
                .eq(UserPostDO::getUserId, userId)
                .in(UserPostDO::getPostId, postIds));
    }
//    根据岗位 ID 集合查询用户岗位关系 查询哪些用户绑定了这些岗位
    default List<UserPostDO> selectListByPostIds(Collection<Long> postIds) {
        return selectList(UserPostDO::getPostId, postIds);
    }
//    删除某个用户的所有岗位绑定 删除某个用户的全部岗位绑定关系。
    default void deleteByUserId(Long userId) {
//     Wrappers.lambdaUpdate   MyBatis-Plus 提供的工具写法
//        构造一个条件：user_id = userId
//        然后执行 delete
//        下面这种写法效果类似：
//        delete(new LambdaQueryWrapperX<UserPostDO>()
//                .eq(UserPostDO::getUserId, userId));
        delete(Wrappers.lambdaUpdate(UserPostDO.class).eq(UserPostDO::getUserId, userId));
    }
}
