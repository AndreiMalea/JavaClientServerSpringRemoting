package Service;

import Domain.*;
import Observer.ObservableInterface;
import Observer.ObserverInterface;
import Observer.ServiceInterface;
import Repo.*;
import Interfaces.*;

import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Service extends UnicastRemoteObject implements ServiceInterface {
    private final Properties conn;
    private ArtistRepo artistRepo = null;
    private EmployeeRepo employeeRepo = null;
    private OfficeRepo officeRepo = null;
    private ShowRepo showRepo = null;
    private TransactionDBRepo transactionDBRepo = null;

    public Service() throws RemoteException {
        conn = new Properties();
        String s = (new File("")).getAbsolutePath();
        try {
            conn.load(new FileReader(s + "/databases/db.config"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        artistRepo = new ArtistDBRepo(this.conn);
        employeeRepo = new EmployeeDBRepo(this.conn);
        officeRepo = new OfficeDBRepo(this.conn);
        showRepo = new ShowDBRepo(this.conn);
        transactionDBRepo = new TransactionDBRepo(this.conn);
    }

    private static List<ObserverInterface> observerList;

    public static void initObservable() {
        observerList = new ArrayList<>();
    }
    @Override
    public void addObserver(ObserverInterface o) {
        observerList.add(o);
        System.out.println(observerList);
    }

    @Override
    public void removeObserver(ObserverInterface o) {
        observerList.remove(o);
        System.out.println(observerList);
    }

    @Override
    public void myNotifyAll() {
        observerList.forEach(e -> {
            try {
                e.notified();
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });
    }

    @Override
    public synchronized Iterable<Show> findAllShows() {
        return showRepo.findAll();
    }

    @Override
    public synchronized Employee employeeByUser(String user) {
        return employeeRepo.getEmployeeByUser(user);
    }

    @Override
    public synchronized Employee login(String user, String pass, ObserverInterface o) {
        if (employeeRepo.usernameExists(user)) {
            if (employeeRepo.getPasswordByUser(user).equals(pass)) {
                this.addObserver(o);
                return employeeRepo.getEmployeeByUser(user);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Show> filterShowsByDate(LocalDate date) {
        return showRepo.filterByDate(date);
    }

    @Override
    public synchronized Boolean buyTicket(Show s, Integer no, String client) throws Exception {
        if (s.getTicketNumber() == 0) return false;
        s.decreaseTicketNumber(no);
        transactionDBRepo.save(new Transaction(client, s, LocalDate.now(), no));
        showRepo.update(s);
        this.myNotifyAll();
        return true;
    }

    @Override
    public void close(ObserverInterface o) {
        this.removeObserver(o);
    }

}