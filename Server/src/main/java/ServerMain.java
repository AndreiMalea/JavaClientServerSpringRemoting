import Service.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerMain {
    public static void main(String[] args) {
        Service.initObservable();
        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:server.xml");

    }
}
