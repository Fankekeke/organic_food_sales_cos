package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.MerchantInfo;
import cc.mrbird.febs.cos.entity.OrganicCertifications;
import cc.mrbird.febs.cos.service.IMerchantInfoService;
import cc.mrbird.febs.cos.service.IOrganicCertificationsService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/organic-certifications")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrganicCertificationsController {

    private final IOrganicCertificationsService organicCertificationsService;

    private final IMerchantInfoService merchantInfoService;

    /**
     * 分页获取商家申报信息
     *
     * @param page         分页对象
     * @param organicCertifications 商家申报信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<OrganicCertifications> page, OrganicCertifications organicCertifications) {
        return R.ok();
    }

    /**
     * 获取ID获取商家申报详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(organicCertificationsService.getById(id));
    }

    /**
     * 获取商家申报信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(organicCertificationsService.list());
    }

    /**
     * 根据商家ID查询申报信息
     *
     * @param merchantId 商家ID
     * @return 申报信息
     */
    @GetMapping("/queryCertificationByMerchant")
    public R queryCertificationByMerchant(Integer merchantId) {
        MerchantInfo merchantInfo = merchantInfoService.getOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getUserId, merchantId));
        return R.ok(organicCertificationsService.getOne(Wrappers.<OrganicCertifications>lambdaQuery().eq(OrganicCertifications::getMerchantId, merchantInfo.getId())));
    }

    /**
     * 根据商家ID查询申报信息
     *
     * @param merchantId 商家ID
     * @return 申报信息
     */
    @GetMapping("/queryCertificationByMerchantUser")
    public R queryCertificationByMerchantUser(Integer merchantId) {
        return R.ok(organicCertificationsService.getOne(Wrappers.<OrganicCertifications>lambdaQuery().eq(OrganicCertifications::getMerchantId, merchantId)));
    }

    /**
     * 新增商家申报信息
     *
     * @param organicCertifications 商家申报信息
     * @return 结果
     */
    @PostMapping
    public R save(OrganicCertifications organicCertifications) {
        MerchantInfo merchantInfo = merchantInfoService.getOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getUserId, organicCertifications.getMerchantId()));
        organicCertifications.setMerchantId(Long.valueOf(merchantInfo.getId()));
        organicCertifications.setCreatedAt(DateUtil.formatDateTime(new Date()));
        return R.ok(organicCertificationsService.save(organicCertifications));
    }

    /**
     * 修改商家申报信息
     *
     * @param organicCertifications 商家申报信息
     * @return 结果
     */
    @PutMapping
    public R edit(OrganicCertifications organicCertifications) {
        return R.ok(organicCertificationsService.updateById(organicCertifications));
    }

    /**
     * 删除商家申报信息
     *
     * @param ids ids
     * @return 商家申报信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(organicCertificationsService.removeByIds(ids));
    }
}
