package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ZhwHomeBanner;
import com.ruoyi.system.mapper.ZhwHomeBannerMapper;
import com.ruoyi.system.service.IZhwHomeBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZhwHomeBannerServiceImpl implements IZhwHomeBannerService {

    @Autowired
    private ZhwHomeBannerMapper bannerMapper;

    @Override
    public List<ZhwHomeBanner> listBanners() {
        return bannerMapper.selectBannerList();
    }

    @Override
    public List<ZhwHomeBanner> listEnabledBanners() {
        return bannerMapper.selectEnabledBannerList();
    }

    @Override
    public ZhwHomeBanner getBanner(Long bannerId) {
        return bannerMapper.selectBannerById(bannerId);
    }

    @Override
    public Long createBanner(ZhwHomeBanner banner) {
        bannerMapper.insertBanner(banner);
        return banner.getBannerId();
    }

    @Override
    public void updateBanner(ZhwHomeBanner banner) {
        bannerMapper.updateBanner(banner);
    }

    @Override
    public void deleteBanner(Long bannerId) {
        bannerMapper.deleteBanner(bannerId);
    }
}
