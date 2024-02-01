package main.repo.impl;

import main.domain.User;
import main.dto.UserNameDto;
import main.repo.UserRepository;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.List;

@Repository("UserRepositoryJP")
public class UserRepositoryJPImpl implements UserRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public UserRepositoryJPImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<User> gerUsers() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);
        TypedQuery<User> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    @Override
    public UserNameDto findNameById(int id) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserNameDto> criteriaQuery = criteriaBuilder.createQuery(UserNameDto.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery = criteriaQuery.select(criteriaBuilder.construct(UserNameDto.class,
                        root.get("user_f_name"),
                        root.get("user_l_name")))
                .where(criteriaBuilder.equal(root.get("id"), id));
        TypedQuery<UserNameDto> dtoTypedQuery = entityManager.createQuery(criteriaQuery);
        List<UserNameDto> list = dtoTypedQuery.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<User> gerUsers(int first, int last) {
        return null;
    }

    @Override
    public Pair<Integer, Integer> selectIdsInterval() throws SQLException {
        return null;
    }
}
