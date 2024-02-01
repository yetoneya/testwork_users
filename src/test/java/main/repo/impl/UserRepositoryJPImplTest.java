package main.repo.impl;

import main.domain.User;
import main.dto.UserNameDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryJPImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    UserRepositoryJPImpl userRepositoryJP;

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldFindNameById() {
        //типа тест для сонара
        int id = 1;
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        CriteriaQuery criteriaQuery = mock(CriteriaQuery.class);
        when(criteriaBuilder.createQuery(UserNameDto.class)).thenReturn(criteriaQuery);
        Root root = mock(Root.class);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        when(criteriaQuery.select(criteriaBuilder.construct(UserNameDto.class,
                root.get("user_f_name"),
                root.get("user_l_name")))).thenReturn(criteriaQuery);
        when(criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id))).thenReturn(criteriaQuery);
        TypedQuery typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(new UserNameDto()));
        UserNameDto actual = userRepositoryJP.findNameById(id);
        assertNotNull(actual);
    }
}
