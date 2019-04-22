package org.shareData.acceptor.i;

import org.shareData.acceptor.agreement.AccAgreement;
import org.shareData.acceptor.agreement.AccState;

public interface PaxosType {
public AccState paxosDo(AccAgreement body);
}
