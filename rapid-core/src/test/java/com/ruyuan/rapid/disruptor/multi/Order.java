package com.ruyuan.rapid.disruptor.multi;

/**
 * <B>主类名称：</B>Order<BR>
 * <B>概要说明：</B>Order<BR>
 * @author JiFeng
 * @since 2021年12月7日 上午12:08:14
 */
public class Order {

	private String id;
	private String name;
	private double price;

	public Order() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	
	
}
