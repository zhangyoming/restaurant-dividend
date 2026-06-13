package cn.iocoder.yudao.module.restaurant.service.shareholder;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderSaveReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.shareholder.ShareholderDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendDetailMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.shareholder.ShareholderMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.storeshareholder.StoreShareholderMapper;
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

@Service
@Validated
public class ShareholderServiceImpl implements ShareholderService {

    @Resource
    private ShareholderMapper shareholderMapper;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private StoreShareholderMapper storeShareholderMapper;

    @Resource
    private DividendDetailMapper dividendDetailMapper;

    @Override
    public Long createShareholder(ShareholderSaveReqVO createReqVO) {
        validateShareholderForCreateOrUpdate(null, createReqVO);

        ShareholderDO shareholder = BeanUtils.toBean(createReqVO, ShareholderDO.class);
        shareholderMapper.insert(shareholder);
        return shareholder.getId();
    }

    @Override
    public void updateShareholder(ShareholderSaveReqVO updateReqVO) {
        validateShareholderForCreateOrUpdate(updateReqVO.getId(), updateReqVO);

        ShareholderDO updateObj = BeanUtils.toBean(updateReqVO, ShareholderDO.class);
        shareholderMapper.updateById(updateObj);
    }

    @Override
    public void deleteShareholder(Long id) {
        validateShareholderExists(id);

        // 股东已有持股关系或分红记录，不允许物理删除
        validateShareholderNoBusinessData(id);

        shareholderMapper.deleteById(id);
    }

    private void validateShareholderForCreateOrUpdate(Long id, ShareholderSaveReqVO reqVO) {
        validateShareholderExists(id);
        validateShareholderPhoneUnique(id, reqVO.getPhone());
        validateShareholderUserUnique(id, reqVO.getUserId());
    }

    @VisibleForTesting
    void validateShareholderExists(Long id) {
        if (id == null) {
            return;
        }
        ShareholderDO shareholder = shareholderMapper.selectById(id);
        if (shareholder == null) {
            throw exception(SHAREHOLDER_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    void validateShareholderPhoneUnique(Long id, String phone) {
        ShareholderDO shareholder = shareholderMapper.selectByPhone(phone);
        if (shareholder == null) {
            return;
        }
        if (id == null) {
            throw exception(SHAREHOLDER_PHONE_DUPLICATE);
        }
        if (!shareholder.getId().equals(id)) {
            throw exception(SHAREHOLDER_PHONE_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateShareholderUserUnique(Long id, Long userId) {
        if (userId == null) {
            return;
        }

        adminUserService.validateUserList(Collections.singleton(userId));

        ShareholderDO shareholder = shareholderMapper.selectByUserId(userId);
        if (shareholder == null) {
            return;
        }
        if (id == null) {
            throw exception(SHAREHOLDER_USER_DUPLICATE);
        }
        if (!shareholder.getId().equals(id)) {
            throw exception(SHAREHOLDER_USER_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateShareholderNoBusinessData(Long shareholderId) {
        Long relationCount = storeShareholderMapper.selectCountByShareholderId(shareholderId);
        if (relationCount != null && relationCount > 0) {
            throw exception(SHAREHOLDER_HAS_BUSINESS_DATA);
        }

        Long detailCount = dividendDetailMapper.selectCountByShareholderId(shareholderId);
        if (detailCount != null && detailCount > 0) {
            throw exception(SHAREHOLDER_HAS_BUSINESS_DATA);
        }
    }

    @Override
    public ShareholderDO getShareholder(Long id) {
        return shareholderMapper.selectById(id);
    }

    @Override
    public PageResult<ShareholderDO> getShareholderPage(ShareholderPageReqVO pageReqVO) {
        return shareholderMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ShareholderDO> getShareholderList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return shareholderMapper.selectListByIds(ids);
    }

    @Override
    public Map<Long, ShareholderDO> getShareholderMap(Collection<Long> ids) {
        return convertMap(getShareholderList(ids), ShareholderDO::getId);
    }

    @Override
    public List<ShareholderDO> getSimpleShareholderList() {
        return shareholderMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    public void validateShareholderList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<ShareholderDO> shareholders = shareholderMapper.selectListByIds(ids);
        Map<Long, ShareholderDO> shareholderMap = convertMap(shareholders, ShareholderDO::getId);
        ids.forEach(id -> {
            ShareholderDO shareholder = shareholderMap.get(id);
            if (shareholder == null) {
                throw exception(SHAREHOLDER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(shareholder.getStatus())) {
                throw exception(SHAREHOLDER_NOT_ENABLE, shareholder.getName());
            }
        });
    }

}