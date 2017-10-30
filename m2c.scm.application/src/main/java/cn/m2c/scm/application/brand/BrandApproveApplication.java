package cn.m2c.scm.application.brand;

import cn.m2c.common.MCode;
import cn.m2c.ddd.common.event.annotation.EventListener;
import cn.m2c.scm.application.brand.command.BrandApproveAgreeCommand;
import cn.m2c.scm.application.brand.command.BrandApproveCommand;
import cn.m2c.scm.application.brand.command.BrandApproveRejectCommand;
import cn.m2c.scm.domain.IDGenerator;
import cn.m2c.scm.domain.NegativeException;
import cn.m2c.scm.domain.model.brand.BrandApprove;
import cn.m2c.scm.domain.model.brand.BrandApproveRepository;
import cn.m2c.scm.domain.model.brand.BrandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 品牌审批
 */
@Service
public class BrandApproveApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrandApproveApplication.class);

    @Autowired
    BrandApproveRepository brandApproveRepository;
    @Autowired
    BrandRepository brandRepository;

    /**
     * 添加品牌信息（商家平台，需审批）
     *
     * @param command
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, NegativeException.class})
    public void addBrandApprove(BrandApproveCommand command) throws NegativeException {
        LOGGER.info("addBrandApprove command >>{}", command);
        // 与当前品牌库中的不能重名
        if (brandRepository.brandNameIsRepeat(null, command.getBrandName()) ||
                brandApproveRepository.brandNameIsRepeat(null, null, command.getBrandName())) {
            throw new NegativeException(MCode.V_301, "品牌名称已存在");
        }
        BrandApprove brandApprove = brandApproveRepository.getBrandApproveByApproveId(command.getBrandApproveId());
        if (null == brandApprove) {
            brandApprove = new BrandApprove(command.getBrandApproveId(), command.getBrandId(), command.getBrandName(), command.getBrandNameEn(), command.getBrandLogo(), command.getFirstAreaCode(),
                    command.getTwoAreaCode(), command.getThreeAreaCode(), command.getFirstAreaName(), command.getTwoAreaName(),
                    command.getThreeAreaName(), command.getDealerId(), command.getDealerName());
            brandApproveRepository.save(brandApprove);
        }
    }

    /**
     * 修改品牌库中品牌（商家平台，需审核）
     *
     * @param command
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, NegativeException.class})
    public void modifyBrand(BrandApproveCommand command) throws NegativeException {
        LOGGER.info("modifyBrand command >>{}", command);
        // 与当前品牌库中的不能重名
        if (brandRepository.brandNameIsRepeat(command.getBrandId(), command.getBrandName()) ||
                brandApproveRepository.brandNameIsRepeat(command.getBrandApproveId(), command.getBrandId(), command.getBrandName())) {
            throw new NegativeException(MCode.V_301, "品牌名称已存在");
        }
        BrandApprove brandApprove = brandApproveRepository.getBrandApproveByBrandId(command.getBrandId());
        if (null == brandApprove) {//不存在审核记录，就增加一条审核记录
            brandApprove = new BrandApprove(IDGenerator.get(IDGenerator.SCM_BRANDE_APPROVE_PREFIX_TITLE), command.getBrandId(), command.getBrandName(), command.getBrandNameEn(), command.getBrandLogo(), command.getFirstAreaCode(),
                    command.getTwoAreaCode(), command.getThreeAreaCode(), command.getFirstAreaName(), command.getTwoAreaName(),
                    command.getThreeAreaName(), command.getDealerId(), command.getDealerName());
            brandApproveRepository.save(brandApprove);
        } else { //存在审核记录，就覆盖之前的
            brandApprove.modifyBrandApprove(command.getBrandName(), command.getBrandNameEn(), command.getBrandLogo(), command.getFirstAreaCode(),
                    command.getTwoAreaCode(), command.getThreeAreaCode(), command.getFirstAreaName(), command.getTwoAreaName(),
                    command.getThreeAreaName());
        }
    }

    /**
     * 修改审批中的品牌信息（商家平台）
     *
     * @param command
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, NegativeException.class})
    public void modifyBrandApprove(BrandApproveCommand command) throws NegativeException {
        LOGGER.info("addBrandApprove command >>{}", command);
        // 与当前品牌库中的不能重名
        if (brandRepository.brandNameIsRepeat(null, command.getBrandName()) ||
                brandApproveRepository.brandNameIsRepeat(command.getBrandApproveId(), command.getBrandId(), command.getBrandName())) {
            throw new NegativeException(MCode.V_301, "品牌名称已存在");
        }
        BrandApprove brandApprove = brandApproveRepository.getBrandApproveByApproveId(command.getBrandApproveId());
        if (null == brandApprove) {
            throw new NegativeException(MCode.V_300, "审核品牌信息不存在");
        }
        brandApprove.modifyBrandApprove(command.getBrandName(), command.getBrandNameEn(), command.getBrandLogo(), command.getFirstAreaCode(),
                command.getTwoAreaCode(), command.getThreeAreaCode(), command.getFirstAreaName(), command.getTwoAreaName(),
                command.getThreeAreaName());
    }

    /**
     * 同意
     *
     * @param command
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, NegativeException.class})
    @EventListener(isListening = true)
    public void agreeBrandApprove(BrandApproveAgreeCommand command) throws NegativeException {
        LOGGER.info("agreeBrandApprove command >>{}", command);
        BrandApprove brandApprove = brandApproveRepository.getBrandApproveByApproveId(command.getBrandApproveId());
        if (null == brandApprove) {
            throw new NegativeException(MCode.V_300, "审核品牌信息不存在");
        }
        brandApprove.agree();
        brandApproveRepository.remove(brandApprove);
    }

    /**
     * 拒绝
     *
     * @param command
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, NegativeException.class})
    public void rejectBrandApprove(BrandApproveRejectCommand command) throws NegativeException {
        LOGGER.info("rejectBrandApprove command >>{}", command);
        BrandApprove brandApprove = brandApproveRepository.getBrandApproveByApproveId(command.getBrandApproveId());
        if (null == brandApprove) {
            throw new NegativeException(MCode.V_300, "审核品牌信息不存在");
        }
        brandApprove.reject(command.getRejectReason());
    }

    /**
     * 删除品牌审核信息
     *
     * @param approveId
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, NegativeException.class})
    public void delBrandApprove(String approveId) throws NegativeException {
        LOGGER.info("delBrandApprove approveId >>{}", approveId);
        BrandApprove brandApprove = brandApproveRepository.getBrandApproveByApproveId(approveId);
        if (null == brandApprove) {
            throw new NegativeException(MCode.V_300, "审核品牌信息不存在");
        }
        brandApprove.delete();
    }

    /**
     * 删除品牌审核信息
     *
     * @param brandId
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, NegativeException.class})
    public void delBrandApproveByBrandId(String brandId) throws NegativeException {
        LOGGER.info("delBrandApproveByBrandId brandId >>{}", brandId);
        BrandApprove brandApprove = brandApproveRepository.getBrandApproveByBrandId(brandId);
        if (null == brandApprove) {
            throw new NegativeException(MCode.V_300, "审核品牌信息不存在");
        }
        brandApprove.delete();
    }
}
