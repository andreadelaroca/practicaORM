package ni.edu.uam.practicaorm.config;

import ni.edu.uam.practicaorm.model.Estudiante;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

public final class HibernateUtil {
    private static SessionFactory factory;

    static {
        try {
            Class.forName("org.postgresql.Driver");

            try (InputStream in = HibernateUtil.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
                if (in == null) {
                    throw new IllegalStateException("No se encontro hibernate.properties en src/main/resources/");
                }
                Properties props = new Properties();
                props.load(in);

                factory = new Configuration()
                        .addProperties(props)
                        .addAnnotatedClass(Estudiante.class)
                        .buildSessionFactory();
            }
        } catch (Throwable ex) {
            System.err.println("--- ERROR CRITICO AL INICIALIZAR HIBERNATE ---");
            ex.printStackTrace();
            factory = null;
        }
    }

    private HibernateUtil() {}

    public static SessionFactory getSessionFactory() {
        if (factory == null) {
            throw new IllegalStateException("SessionFactory no fue inicializado correctamente. Revisa los logs anteriores.");
        }
        return factory;
    }

    public static void shutdown() {
        if (factory != null && !factory.isClosed()) {
            factory.close();
        }
    }
}