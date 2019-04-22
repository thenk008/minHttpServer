package org.shareData.acceptor.tools;

public class ToolTest {
	public static void main(String[] args) {
		 my();
	}
public static void my() {
	SuperMap<Long,String> map = new SuperMap<>();
	//long my = System.currentTimeMillis();
	map.put(1, "1");
	map.put(2, "2");
	map.put(3, "3");
	map.put(4, "4");
	map.put(5, "5");
	map.put(6, "6");
	map.delete(4);
    String a = map.get(1);
    String b = map.get(2);
    String c = map.get(3);
    String d = map.get(4);
    String e = map.get(5);
    String f = map.get(6);
    System.out.println(a+","+b+","+c+","+d+","+e+","+f);
    //long mr =System.currentTimeMillis();
    //long mc =mr-my;
    //System.out.println(mr-my);
}
}
