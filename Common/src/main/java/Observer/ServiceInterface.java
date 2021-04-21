package Observer;

import Domain.Employee;
import Domain.Show;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface ServiceInterface extends Serializable {

    Iterable<Show> findAllShows() throws Exception;

    Employee employeeByUser(String user) throws Exception;

    Employee login(String user, String pass, ObserverInterface o) throws Exception;

    List<Show> filterShowsByDate(LocalDate date) throws Exception;

    Boolean buyTicket(Show s, Integer no, String client) throws Exception;

    void addObserver(ObserverInterface o);

    void removeObserver(ObserverInterface o);

    void myNotifyAll();

    void close(ObserverInterface o);
}
