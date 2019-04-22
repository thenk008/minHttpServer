package org.shareData.acceptor.agreement;

public class AccState {
	private byte request;//确认是返回的几段提交
	private byte state;//这个请求的状态
	private long accerptKey;//提案KEY
	private String accerptVal;//提案VALUE
    private long bussinessId;//提案业务号
    private int  isEnd;//是否结束

	public int getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(int isEnd) {
		this.isEnd = isEnd;
	}

	public long getBussinessId() {
		return bussinessId;
	}

	public void setBussinessId(long bussinessId) {
		this.bussinessId = bussinessId;
	}

	public byte getRequest() {
		return request;
	}

	public void setRequest(byte request) {
		this.request = request;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
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

}
