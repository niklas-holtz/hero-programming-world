package controller.tutor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("serial")
public class Tutor extends UnicastRemoteObject implements TutorI {
	List<TransferObject> items;
	private TransferObject current;

	protected Tutor() throws RemoteException {
		super();
		this.items = new LinkedList<TransferObject>();
	}

	@Override
	public synchronized int sendRequest(TransferObject req) throws RemoteException {
		int randomID = new Random().nextInt();
		req.setID(randomID);
		this.items.add(req);
		return randomID;
	}

	@Override
	public synchronized TransferObject getAnswer(int id) throws RemoteException {
		for(TransferObject t : items) {
			if(t.getID() == id && t.isParsed()) {
				TransferObject buffer = t;
				this.items.remove(t);
				return buffer;
			}
		}
		return null;
	}
	

	public synchronized TransferObject getNextRequest() {
		for(TransferObject t : items) {
			if(!t.isParsed()) {
				current = t;
				return t;
			}
		}
		return null;
	}


	public synchronized void prepareAnswer() {
		if(current != null) {
			current.parse();
			current = null;
		}
	}
	
	public synchronized TransferObject getCurrent() {
		return this.current;
	}
	


}
