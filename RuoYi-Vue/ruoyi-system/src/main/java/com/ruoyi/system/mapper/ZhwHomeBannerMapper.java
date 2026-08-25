package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ZhwHomeBanner;
import java.util.List;

public interface ZhwHomeBannerMapper {

    /** 管理端：查询全部横幅（任意状态），按 sort_order 排序 */
    List<ZhwHomeBanner> selectBannerList();

    /** 公开端：查询启用中的横幅，按 sort_order 排序 */
    List<ZhwHomeBanner> selectEnabledBannerList();

    ZhwHomeBanner selectBannerById(Long bannerId);

    int insertBanner(ZhwHomeBanner banner);

    int updateBanner(ZhwHomeBanner banner);

    int deleteBanner(Long bannerId);
}
