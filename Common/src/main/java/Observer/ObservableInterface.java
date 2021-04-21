package Observer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface ObservableInterface extends Serializable {
    List<ObserverInterface> observerList = new ArrayList<>();

    default List<ObserverInterface> getObserverList() {
        return observerList;
    }

    default void addObserver(ObserverInterface o) {
        synchronized (this.observerList) {
            observerList.add(o);
            System.out.println(observerList);
        }
    }

    default void removeObserver(ObserverInterface o) {
        synchronized (this.observerList) {
            observerList.remove(o);
        }
    }

    default void myNotifyAll(){
        observerList.forEach(e-> {
            try {
                e.notified();
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });
    }

    default void myNotifyAllExcept(ObserverInterface obs) {
        observerList.forEach(e-> {
            if (e != obs) {
                try {
                    e.notified();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
    }
}
