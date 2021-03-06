package cn.m2c.scm.port.adapter.persistence.hibernate.goods;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.m2c.ddd.common.port.adapter.persistence.hibernate.HibernateSupperRepository;
import cn.m2c.scm.domain.model.goods.GoodsGuarantee;
import cn.m2c.scm.domain.model.goods.GoodsGuaranteeRepository;

/**
 * 商品保障
 * */
@Repository
public class HibernateGoodsGuaranteeRepository extends HibernateSupperRepository implements GoodsGuaranteeRepository{
	/**
	 * 获取商品保障
	 */
	@Override
	public GoodsGuarantee queryGoodsGuaranteeByIdAndDealerId(String guaranteeId, String dealerId) {
		/*GoodsGuarantee goodsGuarantee = (GoodsGuarantee) this.session().createQuery(" FROM GoodsGuarantee WHERE guaranteeId=:guaranteeId  AND is_default = 0 ")
				.setString("guaranteeId", guaranteeId).uniqueResult();*/
		StringBuilder sql = new StringBuilder("SELECT * FROM t_scm_goods_guarantee WHERE guarantee_id=:guaranteeId AND dealer_id=:dealerId AND is_default = 0 AND guarantee_status = 1 ");
		Query query = this.session().createSQLQuery(sql.toString()).addEntity(GoodsGuarantee.class);
		query.setParameter("guaranteeId", guaranteeId);
		query.setParameter("dealerId",dealerId);
		return (GoodsGuarantee) query.uniqueResult();
	}

	/**
	 * 查询是否有重名(true有重名)
	 */
	@Override
	public boolean goodsGuaranteeNameIsRepeat(String guaranteeName, String dealerId, String guaranteeId) {
		StringBuilder sql = new StringBuilder("SELECT * FROM t_scm_goods_guarantee WHERE ( guarantee_name=:guaranteeName AND dealer_id=:dealerId AND is_default = 0 AND guarantee_status = 1 AND guarantee_id<>:guaranteeId) OR ( guarantee_name=:guaranteeName AND is_default = 1 AND guarantee_status = 1 )");
		Query query = this.session().createSQLQuery(sql.toString()).addEntity(GoodsGuarantee.class);
		query.setParameter("guaranteeName",guaranteeName);
		query.setParameter("dealerId",dealerId);
		query.setParameter("guaranteeId",guaranteeId);
		List<GoodsGuarantee> list = query.list();
		return null != list && list.size() > 0;
	}

	@Override
	public void save(GoodsGuarantee goodsGuarantee) {
		this.session().saveOrUpdate(goodsGuarantee);
	}

	@Override
	public void remove(GoodsGuarantee goodsGuarantee) {
		this.session().delete(goodsGuarantee);
	}

	/**
	 * 查询商家商品保障
	 */
	@Override
	public List<GoodsGuarantee> queryGoodsGuaranteeByDealerId(String dealerId) {
		StringBuilder sql = new StringBuilder("SELECT * FROM t_scm_goods_guarantee WHERE ( dealer_id=:dealerId AND is_default = 0 AND guarantee_status = 1) OR (is_default = 1 AND guarantee_status = 1) ORDER BY guarantee_order ASC , created_date DESC");
		Query query = this.session().createSQLQuery(sql.toString()).addEntity(GoodsGuarantee.class);
		query.setParameter("dealerId",dealerId);
		return query.list();
	}

	@Override
	public GoodsGuarantee queryGoodsGuaranteeById(String guaranteeId) {
		StringBuilder sql = new StringBuilder("SELECT * FROM t_scm_goods_guarantee WHERE is_default = 0 AND guarantee_status = 1 AND guarantee_id=:guaranteeId ");
		Query query = this.session().createSQLQuery(sql.toString()).addEntity(GoodsGuarantee.class);
		query.setParameter("guaranteeId",guaranteeId);
		return (GoodsGuarantee) query.uniqueResult();
	}

}
