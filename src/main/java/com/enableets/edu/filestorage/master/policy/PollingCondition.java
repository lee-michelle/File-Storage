package com.enableets.edu.filestorage.master.policy;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.enableets.edu.filestorage.core.Constants;

/**
 * 轮循Condition
 * 
 * @author lemon
 * @since 2018/6/9
 */
public class PollingCondition implements Condition {

	/**
	 * 轮循获取
	 */
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String polling = context.getEnvironment().getProperty("storage.master.policy").toString();
		if (polling.equalsIgnoreCase(Constants.POLLING_POLICY)) {
			return true;
		}
		return false;
	}

}
