package cn.m2c.scm.application.postage.query;

import cn.m2c.common.MCode;
import cn.m2c.ddd.common.port.adapter.persistence.springJdbc.SupportJdbcTemplate;
import cn.m2c.scm.application.dealer.data.bean.DealerBean;
import cn.m2c.scm.application.dealer.query.DealerQuery;
import cn.m2c.scm.application.goods.query.GoodsQueryApplication;
import cn.m2c.scm.application.goods.query.data.bean.GoodsBean;
import cn.m2c.scm.application.goods.query.data.representation.GoodsSkuInfoRepresentation;
import cn.m2c.scm.application.postage.data.bean.PostageModelBean;
import cn.m2c.scm.application.postage.data.bean.PostageModelRuleBean;
import cn.m2c.scm.application.postage.data.representation.PostageModelRuleRepresentation;
import cn.m2c.scm.domain.NegativeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运费模板查询
 */
@Service
public class PostageModelQueryApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostageModelQueryApplication.class);

    @Resource
    private SupportJdbcTemplate supportJdbcTemplate;

    public SupportJdbcTemplate getSupportJdbcTemplate() {
        return supportJdbcTemplate;
    }

    @Autowired
    GoodsQueryApplication goodsQueryApplication;
    @Autowired
    DealerQuery dealerQuery;

    public List<PostageModelBean> queryPostageModelsByDealerId(String dealerId) {
        String sql = "SELECT * FROM t_scm_postage_model WHERE 1 = 1 AND dealer_id = ? AND model_status = 1 order by created_date desc";
        List<PostageModelBean> postageModelBeans = this.getSupportJdbcTemplate().queryForBeanList(sql.toString(), PostageModelBean.class,
                new Object[]{dealerId});
        if (null != postageModelBeans && postageModelBeans.size() > 0) {
            for (PostageModelBean bean : postageModelBeans) {
                bean.setPostageModelRuleBeans(queryPostageModelRuleByModelId(bean.getId()));
            }
        }
        return postageModelBeans;
    }

    public List<PostageModelRuleBean> queryPostageModelRuleByModelId(Integer modelId) {
        String sql = "SELECT * FROM t_scm_postage_model_rule  WHERE 1 = 1 AND model_id = ?";
        List<PostageModelRuleBean> postageModelRuleBeans = this.getSupportJdbcTemplate().queryForBeanList(sql.toString(), PostageModelRuleBean.class,
                new Object[]{modelId});
        return postageModelRuleBeans;
    }

    public PostageModelBean queryPostageModelsByModelId(String modelId) {
        String sql = "SELECT * FROM t_scm_postage_model WHERE 1 = 1 AND model_id = ? AND model_status = 1";
        PostageModelBean postageModelBean = this.getSupportJdbcTemplate().queryForBean(sql.toString(), PostageModelBean.class,
                new Object[]{modelId});
        if (null != postageModelBean) {
            postageModelBean.setPostageModelRuleBeans(queryPostageModelRuleByModelId(postageModelBean.getId()));
        }
        return postageModelBean;
    }

    public String getPostageModelNameByModelId(String modelId) {
        PostageModelBean bean = queryPostageModelsByModelId(modelId);
        return null != bean ? bean.getModelName() : "";
    }

    /**
     * 查询商品的运费规则
     *
     * @param skuIds
     * @param cityCode
     * @return
     */
    public Map<String, PostageModelRuleRepresentation> getGoodsPostageRule(List<String> skuIds, String cityCode) throws NegativeException {
        Map<String, PostageModelRuleRepresentation> map = new HashMap<>();
        List<GoodsSkuInfoRepresentation> goodsInfoList = goodsQueryApplication.queryGoodsBySkuIds(skuIds);
        if (null != goodsInfoList && goodsInfoList.size() > 0) {
            for (GoodsSkuInfoRepresentation info : goodsInfoList) {
                PostageModelBean postageModelBean = queryPostageModelsByModelId(info.getGoodsPostageId());
                if (postageModelBean == null) {
                    map.put(info.getSkuId(), null);
                    continue;
                }
                List<DealerBean> dealerBeanList = dealerQuery.getDealers(postageModelBean.getDealerId());
                String dealerName = "";
                if (null != dealerBeanList && dealerBeanList.size() > 0) {
                    dealerName = dealerBeanList.get(0).getDealerName();
                }
                if (null != postageModelBean) {
                	if(postageModelBean.getChargeType() == 0 || postageModelBean.getChargeType() == 1) {//0按重量，1按件数
                		List<PostageModelRuleBean> ruleBeans = postageModelBean.getPostageModelRuleBeans();
                        if (null != ruleBeans && ruleBeans.size() > 0) {
                            boolean specialFlag = false;
                            PostageModelRuleBean defaultBean = null;
                            for (PostageModelRuleBean bean : ruleBeans) {
                                Integer defaultFlag = bean.getDefaultFlag();//全国（默认运费），0：是，1：不是
                                if (defaultFlag == 1) { // 不是全国默认
                                    if (StringUtils.isNotEmpty(bean.getCityCode())) {
                                        List<String> codes = Arrays.asList(bean.getCityCode().split(","));
                                        if (codes.contains(cityCode)) {
                                            map.put(info.getSkuId(), new PostageModelRuleRepresentation(bean, postageModelBean, dealerName));
                                            specialFlag = true;
                                            break;
                                        }
                                    }
                                } else {
                                    defaultBean = bean;
                                }
                            }
                            if (!specialFlag && null != defaultBean) {
                                map.put(info.getSkuId(), new PostageModelRuleRepresentation(defaultBean, postageModelBean, dealerName));
                            }
                        }
                	}else {//2全国包邮模板
                		map.put(info.getGoodsId(), null);
                	}
                }
            }
        }
        return map;
    }

    /**
     * 根据商品ids查询运费规则
     *
     * @param goodsIds
     * @param cityCode
     * @return
     */
    public Map<String, PostageModelRuleRepresentation> getGoodsPostageRuleByGoodsId(List<String> goodsIds, String cityCode) throws NegativeException {
        Map<String, PostageModelRuleRepresentation> map = new HashMap<>();
        List<GoodsBean> goodsInfoList = goodsQueryApplication.queryGoodsByGoodsIds(goodsIds);
        if (null != goodsInfoList && goodsInfoList.size() > 0) {
            for (GoodsBean info : goodsInfoList) {
                PostageModelBean postageModelBean = queryPostageModelsByModelId(info.getGoodsPostageId());
                if (postageModelBean == null) {
                    map.put(info.getGoodsId(), null);
                    continue;
                }
                List<DealerBean> dealerBeanList = dealerQuery.getDealers(postageModelBean.getDealerId());
                String dealerName = "";
                if (null != dealerBeanList && dealerBeanList.size() > 0) {
                    dealerName = dealerBeanList.get(0).getDealerName();
                }
                if (null != postageModelBean) {
                	if(postageModelBean.getChargeType() == 0 || postageModelBean.getChargeType() == 1) {//0按重量，1按件数
                		List<PostageModelRuleBean> ruleBeans = postageModelBean.getPostageModelRuleBeans();
                        if (null != ruleBeans && ruleBeans.size() > 0) {
                            boolean specialFlag = false;
                            PostageModelRuleBean defaultBean = null;
                            for (PostageModelRuleBean bean : ruleBeans) {
                                Integer defaultFlag = bean.getDefaultFlag();//全国（默认运费），0：是，1：不是
                                if (defaultFlag == 1) { // 不是全国默认
                                    if (StringUtils.isNotEmpty(bean.getCityCode())) {
                                        List<String> codes = Arrays.asList(bean.getCityCode().split(","));
                                        if (codes.contains(cityCode)) {
                                            map.put(info.getGoodsId(), new PostageModelRuleRepresentation(bean, postageModelBean, dealerName));
                                            specialFlag = true;
                                            break;
                                        }
                                    }
                                } else {
                                    defaultBean = bean;
                                }
                            }
                            if (!specialFlag && null != defaultBean) {
                                map.put(info.getGoodsId(), new PostageModelRuleRepresentation(defaultBean, postageModelBean, dealerName));
                            }
                        }
                	}else if(postageModelBean.getChargeType() == 2) {//2全国包邮
                		map.put(info.getGoodsId(), null);
                	}
                }
            }
        }
        return map;
    }

    /**
     * 根据商家id查询商家是否已经创建全国包邮模板
     * @param dealerId
     * @return
     * @throws NegativeException 
     */
	public Boolean queryDealerPostageNationwide(String dealerId, String modelId){
		String sql = "SELECT * FROM t_scm_postage_model WHERE 1 = 1 AND dealer_id = ? AND model_status = 1 AND charge_type = 2 AND model_id <> ? ";
		PostageModelBean postageModelBean = this.getSupportJdbcTemplate().queryForBean(sql.toString(), PostageModelBean.class, dealerId, modelId);
		if(null == postageModelBean) {//商家未创建过全国包邮模板
			return false;
		}else {//商家已创建过全国包邮模板
			return true;
		}
	}
	
}
