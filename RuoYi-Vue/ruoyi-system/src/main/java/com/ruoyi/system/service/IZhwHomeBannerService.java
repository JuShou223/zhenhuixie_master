package com.ruoyi.system.service;

import com.ruoyi.system.domain.ZhwHomeBanner;
import java.util.List;

public interface IZhwHomeBannerService {

    /** 管理端：全部横幅 */
    List<ZhwHomeBanner> listBanners();

    /** 公开端：启用中的横幅 */
    List<ZhwHomeBanner> listEnabledBanners();

    ZhwHomeBanner getBanner(Long bannerId);

    Long createBanner(ZhwHomeBanner banner);

    void updateBanner(ZhwHomeBanner banner);

    void deleteBanner(Long bannerId);
}
