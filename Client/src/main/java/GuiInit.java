import Controller.LoginController;
import Observer.ServiceInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GuiInit extends Application {

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader msgloader = new FXMLLoader();
        msgloader.setLocation(getClass().getResource("/views/Login.fxml"));
        AnchorPane root = new AnchorPane();
        try {
            root = msgloader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(root));

        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:client.xml");
        ServiceInterface srv = (ServiceInterface) factory.getBean("clientService");
        System.out.println("Obtained a reference to the remote chat server");

        ((LoginController)msgloader.getController()).init(srv, primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
