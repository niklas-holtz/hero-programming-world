package util.observer;

import java.util.Vector;

public class ReverseObservable {
	private Vector<ReverseObserver> obs;
	
	public ReverseObservable() {
		obs = new Vector<>();
	}
	
	public synchronized void addObserver(ReverseObserver o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }
	
	public synchronized void deleteObserver(ReverseObserver o) {
        obs.removeElement(o);
    }
	 
	public void notifyObservers() {

        Object[] arrLocal;

        synchronized (this) {
            arrLocal = obs.toArray();
        }

        for (int i = 0; i < arrLocal.length; i++)
            ((ReverseObserver)arrLocal[i]).update(this, null);
    }
}
