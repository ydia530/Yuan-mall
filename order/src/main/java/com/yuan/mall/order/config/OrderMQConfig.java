package com.yuan.mall.order.config;

import com.yuan.common.constant.RabbitInfo;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.yuan.common.constant.RabbitInfo.Order.baseRoutingKey;


/**
 * Description：容器中的所有bean都会自动创建到RabbitMQ中 [RabbitMQ没有这个队列、交换机、绑定]
 * date：2020/7/3 17:03
 * 创建交换机、队列、bind
 */
@Configuration
public class OrderMQConfig {

	/* 容器中的Queue、Exchange、Binding 会自动创建（在RabbitMQ）不存在的情况下 */

	/**
	 * 死信队列
	 *
	 * @return
	 */@Bean
	public Queue orderDelayQueue() {
        /*
            Queue(String name,  队列名字
            boolean durable,  是否持久化
            boolean exclusive,  是否排他
            boolean autoDelete, 是否自动删除
            Map<String, Object> arguments) 属性
         */
		HashMap<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", "order-event-exchange");
		arguments.put("x-dead-letter-routing-key", "order.release.order");
		arguments.put("x-message-ttl", 60000); // 消息过期时间 1分钟
		Queue queue = new Queue("order.delay.queue", true, false, false, arguments);

		return queue;
	}

	/**
	 * 普通队列
	 *
	 * @return
	 */
	@Bean
	public Queue orderReleaseQueue() {

		Queue queue = new Queue("order.release.order.queue", true, false, false);

		return queue;
	}

	/**
	 * TopicExchange
	 *
	 * @return
	 */
	@Bean
	public Exchange orderEventExchange() {
		/*
		 *   String name,
		 *   boolean durable,
		 *   boolean autoDelete,
		 *   Map<String, Object> arguments
		 * */
		return new TopicExchange("order-event-exchange", true, false);

	}


	@Bean
	public Binding orderCreateBinding() {
		/*
		 * String destination, 目的地（队列名或者交换机名字）
		 * DestinationType destinationType, 目的地类型（Queue、Exhcange）
		 * String exchange,
		 * String routingKey,
		 * Map<String, Object> arguments
		 * */
		return new Binding("order.delay.queue",
				Binding.DestinationType.QUEUE,
				"order-event-exchange",
				"order.create.order",
				null);
	}

	@Bean
	public Binding orderReleaseBinding() {

		return new Binding("order.release.order.queue",
				Binding.DestinationType.QUEUE,
				"order-event-exchange",
				"order.release.order",
				null);
	}

	/**
	 * 订单释放直接和库存释放进行绑定
	 * @return
	 */
	@Bean
	public Binding orderReleaseOtherBinding() {

		return new Binding("stock.release.stock.queue",
				Binding.DestinationType.QUEUE,
				"order-event-exchange",
				"order.release.other.#",
				null);
	}


	/**
	 * 商品秒杀队列
	 * @return
	 */
	@Bean
	public Queue orderSecKillOrrderQueue() {
		Queue queue = new Queue("order.seckill.order.queue", true, false, false);
		return queue;
	}

	@Bean
	public Binding orderSecKillOrrderQueueBinding() {
		//String destination, DestinationType destinationType, String exchange, String routingKey,
		// 			Map<String, Object> arguments
		Binding binding = new Binding(
				"order.seckill.order.queue",
				Binding.DestinationType.QUEUE,
				"order-event-exchange",
				"order.seckill.order",
				null);

		return binding;
	}
}
