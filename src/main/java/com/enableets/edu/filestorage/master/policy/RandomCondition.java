package com.enableets.edu.filestorage.master.policy;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.enableets.edu.filestorage.core.Constants;

/**
 * 随机Condition
 * 
 * @author lemon
 * @since 2018/6/9
 */
public class RandomCondition implements Condition {

	/**
	 * 随机获取
	 */
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String random = context.getEnvironment().getProperty("storage.master.policy").toString();
		if (random.equalsIgnoreCase(Constants.RANDOM_POLICY)) {
			return true;
		}
		return false;
	}

}
