package com.att.datalake.loco.integration.beanprocess;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;

import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.integration.activator.Ingester;
import com.att.datalake.loco.integration.hdfs.MoveToHdfs;

@Component
public class IngesterPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String arg1) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String arg1) throws BeansException {
		if (bean.getClass().isAssignableFrom(Ingester.class)) {
			MoveToHdfs mover = Mockito.mock(MoveToHdfs.class);
			Mockito.when(mover.move(anyString(), anyString(), anyBoolean())).thenReturn(true);
			return new Ingester(mover);
		}
		return bean;
	}
}