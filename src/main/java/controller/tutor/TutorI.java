package controller.tutor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TutorI extends Remote {
	public int sendRequest(TransferObject req) throws RemoteException;
	public TransferObject getAnswer(int id) throws RemoteException;

}
