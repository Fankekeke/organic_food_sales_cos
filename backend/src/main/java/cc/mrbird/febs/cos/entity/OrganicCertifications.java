package cc.mrbird.febs.cos.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商家有机申报信息汇总表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrganicCertifications implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 关联商家ID
     */
    private Long merchantId;

    /**
     * 身份证明图片
     */
    private String idCardImg;

    /**
     * 营业执照图片
     */
    private String businessLicenseImg;

    /**
     * 土地租赁合同或产权证明
     */
    private String landProofImg;

    /**
     * 环境检测报告
     */
    private String envTestReport;

    /**
     * 产品检验报告
     */
    private String productTestReport;

    /**
     * 投入品证明（种子、肥料等）
     */
    private String inputMaterialProof;

    /**
     * 商家的其他要求或问题
     */
    private String additionalRequests;

    /**
     * 审核状态: 0-待审核, 1-通过, 2-驳回
     */
    private Integer status;

    /**
     * 管理员审核备注/驳回原因
     */
    private String adminRemark;

    /**
     * 管理员是否已查收提醒
     */
    private Boolean hasNotifiedAdmin;

    /**
     * 申请提交时间
     */
    private String createdAt;

    private String updatedAt;


}
