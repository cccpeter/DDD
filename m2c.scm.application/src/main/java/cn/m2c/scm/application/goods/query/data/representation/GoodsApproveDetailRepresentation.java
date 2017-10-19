package cn.m2c.scm.application.goods.query.data.representation;

import cn.m2c.common.JsonUtils;
import cn.m2c.scm.application.goods.query.data.bean.GoodsApproveBean;

import java.util.List;
import java.util.Map;

/**
 * 商品详情
 */
public class GoodsApproveDetailRepresentation {
    private String goodsName;
    private String goodsSubTitle;
    private String goodsClassifyId;
    private String goodsClassify;
    private String goodsBrandId;
    private String goodsBrandName;
    private String goodsUnitId;
    private String goodsUnitName;
    private Integer goodsMinQuantity;
    private String goodsBarCode;
    private String goodsKeyWord;
    private List<String> goodsGuaranteeIds;
    private List<String> goodsGuaranteeDesc;
    private List<Map> goodsSpecifications;
    private List<Map> goodsSKUs;
    private List<String> goodsMainImages;
    private String goodsDesc;
    private Integer approveStatus;//审核状态，1：审核中，2：审核不通过
    private String rejectReason;

    public GoodsApproveDetailRepresentation(GoodsApproveBean bean, String goodsClassify, List<String> goodsGuaranteeDesc, String goodsUnitName) {
        this.goodsName = bean.getGoodsName();
        this.goodsSubTitle = bean.getGoodsSubTitle();
        this.goodsClassifyId = bean.getGoodsClassifyId();
        this.goodsClassify = goodsClassify;
        this.goodsBrandId = bean.getGoodsBrandId();
        this.goodsBrandName = bean.getGoodsBrandName();
        this.goodsUnitId=bean.getGoodsUnitId();
        this.goodsUnitName = goodsUnitName;
        this.goodsMinQuantity = bean.getGoodsMinQuantity();
        this.goodsBarCode = bean.getGoodsBarCode();
        this.goodsKeyWord = bean.getGoodsKeyWord();
        this.goodsGuaranteeIds = JsonUtils.toList(bean.getGoodsGuarantee(), String.class);
        this.goodsGuaranteeDesc = goodsGuaranteeDesc;
        this.goodsSpecifications = JsonUtils.toList(bean.getGoodsSpecifications(), Map.class);
        this.goodsSKUs = JsonUtils.toList(JsonUtils.toStr(bean.getGoodsSkuApproves()), Map.class);
        this.goodsMainImages = JsonUtils.toList(bean.getGoodsMainImages(), String.class);
        this.goodsDesc = bean.getGoodsDesc();
        this.approveStatus = bean.getApproveStatus();
        this.rejectReason = bean.getRejectReason();
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsSubTitle() {
        return goodsSubTitle;
    }

    public void setGoodsSubTitle(String goodsSubTitle) {
        this.goodsSubTitle = goodsSubTitle;
    }

    public String getGoodsClassifyId() {
        return goodsClassifyId;
    }

    public void setGoodsClassifyId(String goodsClassifyId) {
        this.goodsClassifyId = goodsClassifyId;
    }

    public String getGoodsClassify() {
        return goodsClassify;
    }

    public void setGoodsClassify(String goodsClassify) {
        this.goodsClassify = goodsClassify;
    }

    public String getGoodsBrandId() {
        return goodsBrandId;
    }

    public void setGoodsBrandId(String goodsBrandId) {
        this.goodsBrandId = goodsBrandId;
    }

    public String getGoodsBrandName() {
        return goodsBrandName;
    }

    public void setGoodsBrandName(String goodsBrandName) {
        this.goodsBrandName = goodsBrandName;
    }

    public String getGoodsUnitId() {
        return goodsUnitId;
    }

    public void setGoodsUnitId(String goodsUnitId) {
        this.goodsUnitId = goodsUnitId;
    }

    public String getGoodsUnitName() {
        return goodsUnitName;
    }

    public void setGoodsUnitName(String goodsUnitName) {
        this.goodsUnitName = goodsUnitName;
    }

    public Integer getGoodsMinQuantity() {
        return goodsMinQuantity;
    }

    public void setGoodsMinQuantity(Integer goodsMinQuantity) {
        this.goodsMinQuantity = goodsMinQuantity;
    }

    public String getGoodsBarCode() {
        return goodsBarCode;
    }

    public void setGoodsBarCode(String goodsBarCode) {
        this.goodsBarCode = goodsBarCode;
    }

    public String getGoodsKeyWord() {
        return goodsKeyWord;
    }

    public void setGoodsKeyWord(String goodsKeyWord) {
        this.goodsKeyWord = goodsKeyWord;
    }

    public List<String> getGoodsGuaranteeIds() {
        return goodsGuaranteeIds;
    }

    public void setGoodsGuaranteeIds(List<String> goodsGuaranteeIds) {
        this.goodsGuaranteeIds = goodsGuaranteeIds;
    }

    public List<String> getGoodsGuaranteeDesc() {
        return goodsGuaranteeDesc;
    }

    public void setGoodsGuaranteeDesc(List<String> goodsGuaranteeDesc) {
        this.goodsGuaranteeDesc = goodsGuaranteeDesc;
    }

    public List<Map> getGoodsSpecifications() {
        return goodsSpecifications;
    }

    public void setGoodsSpecifications(List<Map> goodsSpecifications) {
        this.goodsSpecifications = goodsSpecifications;
    }

    public List<Map> getGoodsSKUs() {
        return goodsSKUs;
    }

    public void setGoodsSKUs(List<Map> goodsSKUs) {
        this.goodsSKUs = goodsSKUs;
    }

    public List<String> getGoodsMainImages() {
        return goodsMainImages;
    }

    public void setGoodsMainImages(List<String> goodsMainImages) {
        this.goodsMainImages = goodsMainImages;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}