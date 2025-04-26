package database;

import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.logging.Logger;


@Stateless
public class UserService {


    public boolean saveUserToDb(User user) {
        EntityManager em = Persistence.createEntityManagerFactory("default").createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                Logger.getLogger(UserService.class.getName()).severe(e.getMessage());
                Logger.getLogger(UserService.class.getName()).severe(user.getLogin());
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
        return false;
    }

    public User findByUsername(String login) {
        EntityManager em = Persistence.createEntityManagerFactory("default").createEntityManager();
        String query = "SELECT u FROM User u WHERE u.login = :login";
        TypedQuery<User> typedQuery = em.createQuery(query, User.class);
        typedQuery.setParameter("login", login);
        return typedQuery.getResultStream().findFirst().orElse(null);
    }

}
