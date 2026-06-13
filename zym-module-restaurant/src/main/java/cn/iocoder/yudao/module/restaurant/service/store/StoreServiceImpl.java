package cn.iocoder.yudao.module.restaurant.service.store;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StorePageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreSaveReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.cost.CostMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendDetailMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.revenue.RevenueMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.storeshareholder.StoreShareholderMapper;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 餐饮门店 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class StoreServiceImpl implements StoreService {

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private DeptService deptService;

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private RevenueMapper revenueMapper;

    @Resource
    private CostMapper costMapper;

    @Resource
    private StoreShareholderMapper storeShareholderMapper;

    @Resource
    private DividendPeriodMapper dividendPeriodMapper;

    @Resource
    private DividendDetailMapper dividendDetailMapper;

    @Override
    public Long createStore(StoreSaveReqVO createReqVO) {
        // 校验正确性
        validateStoreForCreateOrUpdate(null, createReqVO);

        // 插入门店
        StoreDO store = BeanUtils.toBean(createReqVO, StoreDO.class);
        storeMapper.insert(store);
        return store.getId();
    }

    @Override
    public void updateStore(StoreSaveReqVO updateReqVO) {
        // 校验正确性
        validateStoreForCreateOrUpdate(updateReqVO.getId(), updateReqVO);

        // 更新门店
        StoreDO updateObj = BeanUtils.toBean(updateReqVO, StoreDO.class);
        storeMapper.updateById(updateObj);
    }

    @Override
    public void deleteStore(Long id) {
        // 校验存在
        validateStoreExists(id);

        // 如果门店已有业务数据，不允许物理删除
        validateStoreNoBusinessData(id);

        // 删除门店
        storeMapper.deleteById(id);
    }

    private void validateStoreForCreateOrUpdate(Long id, StoreSaveReqVO reqVO) {
        // 校验自己存在
        validateStoreExists(id);

        // 校验门店编码唯一
        validateStoreCodeUnique(id, reqVO.getCode());

        // 校验门店名称唯一，第一阶段可做；如果允许不同区域同名门店，可后续改成 deptId + name 唯一
        validateStoreNameUnique(id, reqVO.getName());

        // 校验部门有效
        validateDept(reqVO.getDeptId());

        // 校验负责人用户有效
        validateManagerUser(reqVO.getManagerUserId());
    }

    @VisibleForTesting
    void validateStoreExists(Long id) {
        if (id == null) {
            return;
        }
        StoreDO store = storeMapper.selectById(id);
        if (store == null) {
            throw exception(STORE_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    void validateStoreCodeUnique(Long id, String code) {
        StoreDO store = storeMapper.selectByCode(code);
        if (store == null) {
            return;
        }
        if (id == null) {
            throw exception(STORE_CODE_DUPLICATE);
        }
        if (!store.getId().equals(id)) {
            throw exception(STORE_CODE_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateStoreNameUnique(Long id, String name) {
        StoreDO store = storeMapper.selectByName(name);
        if (store == null) {
            return;
        }
        if (id == null) {
            throw exception(STORE_NAME_DUPLICATE);
        }
        if (!store.getId().equals(id)) {
            throw exception(STORE_NAME_DUPLICATE);
        }
    }
    @VisibleForTesting
    void validateStoreNoBusinessData(Long storeId) {
        Long revenueCount = revenueMapper.selectCountByStoreId(storeId);
        if (revenueCount != null && revenueCount > 0) {
            throw exception(STORE_HAS_BUSINESS_DATA);
        }

        Long costCount = costMapper.selectCountByStoreId(storeId);
        if (costCount != null && costCount > 0) {
            throw exception(STORE_HAS_BUSINESS_DATA);
        }

        Long relationCount = storeShareholderMapper.selectCountByStoreId(storeId);
        if (relationCount != null && relationCount > 0) {
            throw exception(STORE_HAS_BUSINESS_DATA);
        }

        Long dividendPeriodCount = dividendPeriodMapper.selectCountByStoreId(storeId);
        if (dividendPeriodCount != null && dividendPeriodCount > 0) {
            throw exception(STORE_HAS_BUSINESS_DATA);
        }

        Long detailCount = dividendDetailMapper.selectCountByStoreId(storeId);
        if (detailCount != null && detailCount > 0) {
            throw exception(STORE_HAS_BUSINESS_DATA);
        }
    }
    @VisibleForTesting
    void validateDeptExists(Long deptId) {
        if (deptId == null) {
            throw exception(STORE_DEPT_NOT_EXISTS);
        }
        deptService.validateDeptList(Collections.singleton(deptId));
    }

    private void validateDept(Long deptId) {
        if (deptId == null) {
            return;
        }
        deptService.validateDeptList(Collections.singleton(deptId));
    }

    private void validateManagerUser(Long managerUserId) {
        if (managerUserId == null) {
            return;
        }
        adminUserService.validateUserList(Collections.singleton(managerUserId));
    }

    @Override
    public StoreDO getStore(Long id) {
        return storeMapper.selectById(id);
    }

    @Override
    public PageResult<StoreDO> getStorePage(StorePageReqVO pageReqVO) {
        return storeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<StoreDO> getStoreList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return storeMapper.selectListByIds(ids);
    }

    @Override
    public List<StoreDO> getSimpleStoreList() {
        return storeMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    public void validateStoreList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<StoreDO> stores = storeMapper.selectListByIds(ids);
        Map<Long, StoreDO> storeMap = convertMap(stores, StoreDO::getId);
        ids.forEach(id -> {
            StoreDO store = storeMap.get(id);
            if (store == null) {
                throw exception(STORE_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(store.getStatus())) {
                throw exception(STORE_NOT_ENABLE, store.getName());
            }
        });
    }
    @Override
    public StoreDO getStoreByCode(String code) {
        return storeMapper.selectByCode(code);
    }
    @Override
    public List<StoreDO> getStoreListByStatus(Integer status) {
        return storeMapper.selectListByStatus(status);
    }

}