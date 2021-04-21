package Controller;

import Domain.Employee;
import Domain.Show;
import Domain.ShowAux;
import Observer.ServiceInterface;
import Observer.ObserverInterface;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;

public class EmployeeController extends UnicastRemoteObject implements ObserverInterface {
    @FXML
    TextField clientField;
    @FXML
    DatePicker datePicker;
    @FXML
    Spinner<Integer> spinner;
    @FXML
    Button buttonLogout;
    @FXML
    Button buttonBuy;
    @FXML
    TableView<ShowAux> tabelShow;
    @FXML
    TableColumn<ShowAux, String> colArtist;
    @FXML
    TableColumn<ShowAux, Integer> colTickets;
    @FXML
    TableColumn<ShowAux, LocalDate> colDate;
    private Stage prevStage;
    private Stage thisStage;
    private ServiceInterface srv;
    private Employee user;
    private Show selectedShow = null;
    private LocalDate selectedDate = null;

    public EmployeeController() throws RemoteException {
    }

    @Override
    public void notified() throws RemoteException{
        Platform.runLater(() -> {
            synchronized (this) {
                this.reloadTable();
            }
        });
    }

    public void init(ServiceInterface srv, Stage stage, Stage thisStage, Employee user){
        this.user = user;
        this.prevStage = stage;
        this.thisStage = thisStage;
        this.srv = srv;
        this.initTableShow();
        System.out.println(srv);
        try {
            reloadTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        thisStage.setTitle(this.user.getName());

        ObservableList<ShowAux> selectedShows = tabelShow.getSelectionModel().getSelectedItems();
        selectedShows.addListener(new ListChangeListener<ShowAux>() {
            @Override
            public void onChanged(Change<? extends ShowAux> c) {
                if (!c.getList().isEmpty()) {
                    selectedShow = c.getList().get(0).getShow();
                    System.out.println(selectedShow);
                    if (selectedShow.getTicketNumber() == 0) {
                        spinner.setValueFactory(null);
                    } else {
                        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, selectedShow.getTicketNumber()));
                    }
                } else {
                    selectedShow = null;
                    System.out.println("null");
                    spinner.setValueFactory(null);
                }
            }
        });

        tabelShow.setRowFactory(tv -> new TableRow<ShowAux>() {
            @Override
            protected void updateItem(ShowAux item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || item.getTicketNumber() == null)
                    setStyle("");
                else if (item.getTicketNumber().equals(0))
                    setStyle("-fx-background-color: #ffd7d1;");
                else
                    setStyle("");
            }
        });

        ObserverInterface o = this;
        thisStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                thisStage.close();
                srv.close(o);
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void initTableShow() {
        colArtist.setCellValueFactory(new PropertyValueFactory<ShowAux, String>("artist"));
        colTickets.setCellValueFactory(new PropertyValueFactory<ShowAux, Integer>("ticketNumber"));
        colDate.setCellValueFactory(new PropertyValueFactory<ShowAux, LocalDate>("date"));
    }

    @FXML
    private void datePickerAction() throws Exception {
        selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            reloadTableDate(selectedDate);
        } else {
            reloadTable();
        }
    }

    @FXML
    private void buyTicket() {
        if (selectedShow != null) {
            try {
                if (!clientField.getText().equals("")) {
                    if (srv.buyTicket(selectedShow, spinner.getValue(), clientField.getText())) {
                        this.reloadTable();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success!");
                        alert.setHeaderText("Ticket/s successfully bought!");
                        alert.setContentText("Enjoy the show!");
                        alert.showAndWait();
                        clientField.clear();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Buy failed...");
                    alert.setHeaderText("Problem with client!");
                    alert.setContentText("Client field can't be empty!");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Buy failed...");
                alert.setHeaderText("Problem with selected show!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Buy failed...");
            alert.setHeaderText("Problem with show selection!");
            alert.setContentText("No show is selected, first select a show from the list!");
            alert.showAndWait();
        }
    }

    private void reloadTable() {
        try {
            Iterable<Show> lst = srv.findAllShows();
            if (lst != null) {
                tabelShow.getItems().clear();
                lst.forEach(e -> {
                    tabelShow.getItems().add(new ShowAux(e));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadTableDate(LocalDate selectedDate) throws Exception {
        Iterable<Show> lst = srv.filterShowsByDate(selectedDate);
        if (lst != null) {
            tabelShow.getItems().clear();
            lst.forEach(e -> {
                tabelShow.getItems().add(new ShowAux(e));
            });
        }
    }

    @FXML
    private void logout() {
        System.out.println("logout");
        prevStage.show();
        thisStage.close();
        srv.close(this);
    }
}
