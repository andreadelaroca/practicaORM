package ni.edu.uam.practicaorm.config;

import ni.edu.uam.practicaorm.model.Estudiante;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

public final class HibernateUtil {
    private static final SessionFactory FACTORY = crear();

    private HibernateUtil() {}

    private static SessionFactory crear() {
        try (InputStream in = HibernateUtil.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontro hibernate.properties en el classpath");
            }
            Properties props = new Properties();
            props.load(in);
            return new Configuration()
                    .addProperties(props)
                    .addAnnotatedClass(Estudiante.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return FACTORY;
    }

    public static void shutdown() {
        if (FACTORY != null && !FACTORY.isClosed()) {
            FACTORY.close();
        }
    }
}