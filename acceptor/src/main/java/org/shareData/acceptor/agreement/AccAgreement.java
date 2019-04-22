package org.shareData.acceptor.agreement;

public class AccAgreement {
private byte request;//确认提交的是几段请求
private long maxKey;//最大主键值
private long accerptKey;//提案主键值
private String accerptVal;//提案值
private long businessId;//业务号

public long getBusinessId() {
	return businessId;
}
public void setBusinessId(long businessId) {
	this.businessId = businessId;
}
public long getMaxKey() {
	return maxKey;
}
public void setMaxKey(long maxKey) {
	this.maxKey = maxKey;
}
public long getAccerptKey() {
	return accerptKey;
}
public void setAccerptKey(long accerptKey) {
	this.accerptKey = accerptKey;
}
public String getAccerptVal() {
	return accerptVal;
}
public void setAccerptVal(String accerptVal) {
	this.accerptVal = accerptVal;
}
public byte getRequest() {
	return request;
}
public void setRequest(byte request) {
	this.request = request;
}
}
