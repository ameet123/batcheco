package com.att.datalake.loco.integration.core;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

/**
 * mainly for testing, so we can see the messages as they are flying thr various channels
 * @author ac2211
 *
 */
@Component
public class NoopChannelInterceptor extends ChannelInterceptorAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(NoopChannelInterceptor.class);

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		String file;
		LOGGER.debug("++++ Message payload type:{} classname:{}", message.getPayload().getClass().getCanonicalName(), message.getPayload().getClass());
		if (message.getPayload().getClass().equals(com.att.datalake.loco.integration.core.OfferMessage.class)) {
			file = ((OfferMessage)message.getPayload()).getOfferFile().getName();
		} else {
			file = ((File) message.getPayload()).getName();
		}
		LOGGER.debug("++++ {} - {}", channel.toString(), file);
		return super.preSend(message, channel);
	}
}