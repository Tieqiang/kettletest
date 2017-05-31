package common.util;

import com.google.common.base.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Throwables.propagate;

/**
 * Created with IntelliJ IDEA.
 * User: heren
 * Date: 13-2-25
 * Time: 下午6:06
 * 废弃 用BaseFacade中方法代替
 */
@Deprecated
public class JpaUtil {

    public static int checkIsNull(StringBuffer jpql, Object param, int count,
                         String judge, ArrayList<Object> params,boolean like) {
        if(param != null) {
            if(count == 0) {
                jpql.append(" where " + judge);
            } else {
                jpql.append(" and " + judge);
            }
            if(like) {param = param.toString()+"%";}
            params.add(param);
            return ++count;
        }
        return count;
    }

    public static <T> T persist(T entity,EntityManager entityManager) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            propagate(e);
        }
        return entity;
    }

    public static <T> List<T> find(EntityManager entityManager,Class<T> type, String query, Object... parameters) {
        return createQuery(entityManager,type, query, parameters).getResultList();
    }

    public static <T> Optional<T> first(EntityManager entityManager, Class<T> type, String query, Object... parameters) {
        try {
            return Optional.of(createQuery(entityManager, type, query, parameters).setMaxResults(1).getSingleResult());
        } catch (NoResultException e) {
            return absent();
        }
    }

    public static <T> TypedQuery<T> createQuery(EntityManager entityManager,Class<T> type, String query, Object[] parameters) {
        TypedQuery<T> registerQuery = entityManager.
                createQuery(query, type);
        for (int i = 0; i < parameters.length; i++)
            registerQuery.setParameter(i + 1, parameters[i]);
        return registerQuery;
    }


}
