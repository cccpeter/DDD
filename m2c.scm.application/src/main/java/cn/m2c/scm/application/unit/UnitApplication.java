package cn.m2c.scm.application.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.m2c.common.MCode;
import cn.m2c.scm.application.unit.command.UnitCommand;
import cn.m2c.scm.domain.NegativeException;
import cn.m2c.scm.domain.model.unit.Unit;
import cn.m2c.scm.domain.model.unit.UnitRepository;

@Service
public class UnitApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitApplication.class);
    
    
    @Autowired
    UnitRepository unitRepository;
    
    /**
     * 添加计量单位
     * @param command
     * @throws NegativeException
     */
    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,NegativeException.class})
    public void addUnit(UnitCommand command) throws NegativeException{
    	LOGGER.info("addUnit command >> {}",command);
    	System.out.println(command);
    	Unit unitByName = unitRepository.unitNameIsRepeat(command.getUnitName());
    	if (unitByName!=null) {
			throw new NegativeException(MCode.V_301,"计量单位已存在");
		}
    	Unit unit = unitRepository.getUnitByUnitId(command.getUnitId());
    	if (null == unit) {
			unit = new Unit(command.getUnitId(),command.getUnitName());
			unitRepository.saveUnit(unit);
		}
    }
    
    
    
    /**
     * 删除计量单位
     * @param unitName
     * @throws NegativeException
     */
    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,NegativeException.class})
    public void delUnit(String unitId) throws NegativeException{
    	LOGGER.info("delUnit unitName >>{}",unitId);
    	Unit unit = unitRepository.getUnitByUnitId(unitId);
    	if (null == unit) {
			throw new NegativeException(MCode.V_300,"计量单位不存在");
		}
    	unit.deleteUnit();
    	unitRepository.saveUnit(unit);
    }
    
    /**
     * 修改计量单位信息
     * @param command
     * @throws NegativeException
     */
    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,NegativeException.class})
    public void modifyUnit(UnitCommand command) throws NegativeException {
    	LOGGER.info("modify unitName >>{}",command.getUnitName());
    	Unit unitByName = unitRepository.unitNameIsRepeat(command.getUnitName());
    	if (unitByName!=null) {
			throw new NegativeException(MCode.V_301,"计量单位已存在");
		}
    	Unit unit = unitRepository.getUnitByUnitId(command.getUnitId());
    	if (null == unit) {
			throw new NegativeException(MCode.V_300,"计量单位不存在");
		}
    	unit.modify(command.getUnitId(),command.getUnitName(), command.getUnitStatus());
    	unitRepository.saveUnit(unit);
    }

}
