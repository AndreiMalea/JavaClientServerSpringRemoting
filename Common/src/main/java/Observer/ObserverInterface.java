package Observer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverInterface extends Serializable, Remote {
    void notified() throws RemoteException;
}
