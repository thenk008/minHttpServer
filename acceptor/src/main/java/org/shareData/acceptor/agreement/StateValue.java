package org.shareData.acceptor.agreement;

public class StateValue {
public static final byte OK=1;//当前没有接受的提案编号
public static final byte POK=2;//这个提案编号是最大的
public static final byte AOK=3;//已经接受本次提案
public static final byte ERROR_NOTACCEPT =4;//按照约定已经不可以接受当前提案
public static final byte ERROR_NOTANSER =5;//二段请求忽略
public static final byte PREPARE_ONE =1;//一段提交
public static final byte PREPARE_TWO=2;//二段提交
public static final byte PREPARE_END =3;//提交结束
public static long PORT = 8081;
}
