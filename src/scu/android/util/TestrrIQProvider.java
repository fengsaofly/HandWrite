package scu.android.util;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class TestrrIQProvider implements IQProvider {
  public IQ parseIQ(XmlPullParser xp) throws Exception {
    TestrrIQ result = new TestrrIQ();

    System.out.println("gloabl name " + xp.getName());
    System.out.println("gloabl text " + xp.getText());

    while (true) {
      int n = xp.next();
      if (n == XmlPullParser.START_TAG) {
        System.out.println("gloabl name " + xp.getName());
        System.out.println("gloabl text " + xp.getText());
        if ("members".equals(xp.getName())) {
          System.out.println("members " + xp.nextText());// 我们要的东西在这里; 张三 李四 王五可以根据业务模型自己开发不同的解析工具类返回相应的实体
        }
      } else if (n == XmlPullParser.END_TAG) {
        if ("query".equals(xp.getName())) {
          break;
        }
      }
    }
    return result;
  }
}