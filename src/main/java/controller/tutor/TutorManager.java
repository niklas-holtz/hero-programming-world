package controller.tutor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TutorManager {
	
	private Registry registry;
	private int port;
	
	public TutorManager(int port) {
		this.port = port;

	}
	
	public Tutor getTutorAsServer() {
		try {
			TutorI tutor = new Tutor();
			LocateRegistry.createRegistry(port);
			registry = LocateRegistry.getRegistry(port);
			registry.rebind("Tutor", (Tutor) tutor);
			
			return (Tutor) tutor;
		} catch (NumberFormatException | RemoteException e) {
		}
		return null;
	}

	public TutorI getTutorAsClient() {
		try {
			registry = LocateRegistry.getRegistry(port);
			TutorI tutor = (TutorI) registry.lookup("Tutor");
			return tutor;
		} catch (RemoteException | NotBoundException e) {
		}
		return null;
	}
	
}
