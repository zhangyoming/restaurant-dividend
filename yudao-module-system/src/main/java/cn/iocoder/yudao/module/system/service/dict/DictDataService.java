package cn.iocoder.yudao.module.system.service.dict;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
//接口负责定义能力
/**
 * 字典数据 Service 接口
 *
 * @author ruoyi
 */
public interface DictDataService {

    /**
     * 创建字典数据
     *
     * @param createReqVO 字典数据信息
     * @return 字典数据编号
     */
    Long createDictData(DictDataSaveReqVO createReqVO);

    /**
     * 更新字典数据
     *
     * @param updateReqVO 字典数据信息
     */
//    Service 不返回
//    Controller 返回 true 告诉前端操作成功
    void updateDictData(DictDataSaveReqVO updateReqVO);

    /**
     * 删除字典数据
     *
     * @param id 字典数据编号
     */
    void deleteDictData(Long id);

    /**
     * 批量删除字典数据
     *
     * @param ids 字典数据编号列表
     */
    void deleteDictDataList(List<Long> ids);

    /**
     * 获得字典数据列表
     *
     * @param status   状态
     * @param dictType 字典类型
     * @return 字典数据全列表
     */
//   @Nullable 表示参数可以为空
//    字典数据数据库实体对象 DictDataDO
//    status 可以不传 dictType 也可以不传
    List<DictDataDO> getDictDataList(@Nullable Integer status, @Nullable String dictType);

    /**
     * 获得字典数据分页列表
     *
     * @param pageReqVO 分页请求
     * @return 字典数据分页列表
     */
    PageResult<DictDataDO> getDictDataPage(DictDataPageReqVO pageReqVO);

    /**
     * 获得字典数据详情
     *
     * @param id 字典数据编号
     * @return 字典数据
     */
    DictDataDO getDictData(Long id);

    /**
     * 获得指定字典类型的数据数量
     *
     * @param dictType 字典类型
     * @return 数据数量
     */
//    它通常被 DictTypeServiceImpl 删除字典类型时调用。
//    字典类型下面还有子数据，不能删除这个字典类型
    long getDictDataCountByDictType(String dictType);

    /**
     * 校验字典数据们是否有效。如下情况，视为无效：
     * 1. 字典数据不存在
     * 2. 字典数据被禁用
     *
     * @param dictType 字典类型
     * @param values   字典数据值的数组
     */
//    这个方法常用于业务保存前校验参数
    void validateDictDataList(String dictType, Collection<String> values);

    /**
     * 获得指定的字典数据
     *
     * @param dictType 字典类型
     * @param value    字典数据值
     * @return 字典数据
     */
//    根据字典类型和字典值查询某条字典数据
//    getDictData(dictType, value) 根据字典值查
    DictDataDO getDictData(String dictType, String value);

    /**
     * 解析获得指定的字典数据，从缓存中
     *
     * @param dictType 字典类型
     * @param label    字典数据标签
     * @return 字典数据
     */
//    parseDictData(dictType, label) 根据字典标签查
    DictDataDO parseDictData(String dictType, String label);

    /**
     * 获得指定数据类型的字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DictDataDO> getDictDataListByDictType(String dictType);

}
