package ni.edu.uam.practicaorm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ni.edu.uam.practicaorm.config.HibernateUtil;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ni/edu/uam/practicaorm/estudiante-view.fxml"));
        Scene scene = new Scene(loader.load(), 850, 550);
        stage.setTitle("Gestión Académica - UAM (Hibernate ORM)");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        try {
            HibernateUtil.shutdown();
        } catch (Throwable ignored) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}