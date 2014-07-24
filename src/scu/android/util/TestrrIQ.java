package scu.android.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

public class TestrrIQ extends IQ {
	public static final String ELEMENT = "query";
	public static final String NAMESPACE = "jabber:iq:testrr"; // 指定命名空间
																// 服务端接收请求后将根据此命名空间调用业务处理类处理并响应请求
																// 可以用B/S求响应方式套用理解
	private final List<Item> items;
	private String node;

	@SuppressWarnings("unchecked")
	public TestrrIQ() {
		this.items = new CopyOnWriteArrayList();
	}

	public void addItem(Item item) {
		synchronized (this.items) {
			this.items.add(item);
		}
	}

	public Iterator<Item> getItems() {
		synchronized (this.items) {
			return Collections.unmodifiableList(this.items).iterator();
		}
	}

	public String getNode() {
		return this.node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();

		buf.append("<query xmlns=\"jabber:iq:testrr\"");

		if (getNode() != null) {
			buf.append(" node=\"");
			buf.append(getNode());
			buf.append("\"");
		}

		buf.append(">");

		synchronized (this.items) {
			for (Item item : this.items) {
				buf.append(item.toXML());
			}
		}

		buf.append("</query>");

		return buf.toString();
	}
}