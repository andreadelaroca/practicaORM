package ni.edu.uam.practicaorm.repository;

import ni.edu.uam.practicaorm.config.HibernateUtil;
import ni.edu.uam.practicaorm.model.Estudiante;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class EstudianteRepository {

    public void guardar(Estudiante estudiante) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(estudiante);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public List<Estudiante> listar() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Estudiante ORDER BY id ASC", Estudiante.class)
                    .getResultList();
        }
    }

    public List<Estudiante> buscarPorCarrera(String carrera) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Estudiante> query = session.createQuery(
                    "FROM Estudiante e WHERE LOWER(e.carrera) = LOWER(:carrera) ORDER BY e.apellidos ASC",
                    Estudiante.class
            );
            query.setParameter("carrera", carrera);
            return query.getResultList();
        }
    }

    public boolean existeCorreo(String correo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(e) FROM Estudiante e WHERE LOWER(e.correo) = LOWER(:correo)",
                    Long.class
            );
            query.setParameter("correo", correo.trim());
            return query.getSingleResult() > 0;
        }
    }
}