package cn.m2c.scm.application.standstard;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.m2c.common.MCode;
import cn.m2c.scm.application.standstard.command.StantardCommand;
import cn.m2c.scm.domain.NegativeException;
import cn.m2c.scm.domain.model.stantard.Stantard;
import cn.m2c.scm.domain.model.stantard.StantardRepository;

@Service
public class StandstardApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(StandstardApplication.class);
	
	
	@Autowired
	StantardRepository stantardRepository;
	
	/**
	 * 添加规格
	 * @param command
	 * @throws NegativeException
	 */
	@Transactional(rollbackFor = {Exception.class,RuntimeException.class,NegativeException.class})
	public void addStantard(StantardCommand command) throws NegativeException{
    	LOGGER.info("addStantard command >> {}",command);
    	
    	/*if (stantardRepository.stantardNameIsRepeat(command.getStantardName()) != null) {
			throw new NegativeException(MCode.V_301,"规格已存在");
		}*/
    	Stantard stantard = stantardRepository.getStantardByStantardId(command.getStantardId());
    	if (null == stantard) {
    		stantard = new Stantard(command.getStantardId(), command.getStantardName(), command.getStantardStatus());
    	}
			stantardRepository.saveStantard(stantard);
		}
	
	
	/**
	 * 删除规格
	 * @param stantardName
	 * @throws NegativeException
	 */
	 @Transactional(rollbackFor = {Exception.class,RuntimeException.class,NegativeException.class})
	    public void delStantard(String stantardId) throws NegativeException{
	    	LOGGER.info("delStantard stantardName >>{}",stantardId);
	    	
	    	Stantard stantard = stantardRepository.getStantardByStantardId(stantardId);
	    	if (stantard.getUseNum() > 0) {
	    		throw new NegativeException(MCode.V_300,"规格被使用不能被删除");
			}
	    	if (null == stantard) {
				throw new NegativeException(MCode.V_300,"规格不存在");
			}
	    	stantard.delStanstard();
	    	stantardRepository.saveStantard(stantard);
	    }
	 
	 
	 /**
	  * 修改规格
	  * @param command
	  * @throws NegativeException
	  */
	  @Transactional(rollbackFor = {Exception.class,RuntimeException.class,NegativeException.class})
	    public void modifyStantard(StantardCommand command) throws NegativeException {
	    	LOGGER.info("modify stantardName >>{}",command.getStantardName());
	    	Stantard nameIsRepeat = stantardRepository.stantardNameIsRepeat(command.getStantardName());
	    	if (nameIsRepeat != null ) {
	    		if(!nameIsRepeat.getStantardId().equals(command.getStantardId())) {
				throw new NegativeException(MCode.V_301,"规格已存在");
	    		}
			}
	    	Stantard stantard = stantardRepository.getStantardByStantardId(command.getStantardId());
	    	if (null == stantard) {
				throw new NegativeException(MCode.V_300,"规格不存在");
			}
	    	stantard.modify(command.getStantardId(), command.getStantardName(), command.getStantardStatus());
	    	stantardRepository.saveStantard(stantard);
	    }
	  
	  @Transactional(rollbackFor = {Exception.class,RuntimeException.class,NegativeException.class})
	  public void beUsed(List<String> list) {
		  for (String stantardId : list) {
          	if (null != stantardId) {
          		Stantard stantard = stantardRepository.getStantardByStantardId(stantardId);
          		stantard.used();
          		stantardRepository.saveStantard(stantard);
          	}
  		}
	  }
	  
	  @Transactional(rollbackFor = {Exception.class,RuntimeException.class,NegativeException.class})
	  public void noBeUsed(List<String> list) {
		  for (String stantardId : list) {
				if (null != stantardId) {
					Stantard stantard = stantardRepository.getStantardByStantardId(stantardId);
					if (stantard.getUseNum() > 0) {
						stantard.noUsed();
						stantardRepository.saveStantard(stantard);
					}
				}
			}
	  }
}
