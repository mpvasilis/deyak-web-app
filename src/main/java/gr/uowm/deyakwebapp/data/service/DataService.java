package gr.uowm.deyakwebapp.data.service;

import gr.uowm.deyakwebapp.data.entity.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DataService {
    @Autowired
    private EntityManagerFactory entityManagerFactory;


    private final DataRepository repository;

    public DataService(DataRepository repository) {
        this.repository = repository;
    }

    public Optional<Data> get(Long id) {
        return repository.findById(id);
    }

    public Data update(Data entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Data> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Data> list(Pageable pageable, Specification<Data> filter) {
        return repository.findAll(filter, pageable);
    }

    public Set<Integer> getAllCustomerNumbers() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query = entityManager.createQuery("SELECT DISTINCT d.customerNo FROM Data d");
        List<Integer> resultList = query.getResultList();
        entityManager.close();
        return new HashSet<>(resultList);
    }

    public long getTotalDistinctCustomerNumbers() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Data> root = query.from(Data.class);

        query.select(criteriaBuilder.countDistinct(root.get("customerNo")));

        TypedQuery<Long> typedQuery = entityManager.createQuery(query);
        return typedQuery.getSingleResult();
    }
    public long getTotalDataCount() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Data> root = query.from(Data.class);

        query.select(criteriaBuilder.count(root));

        TypedQuery<Long> typedQuery = entityManager.createQuery(query);
        return typedQuery.getSingleResult();
    }
    public List<Data> getFilteredData(Specification<Data> filters) {
        return repository.findAll(filters);
    }
    public Optional<Data> getLastDataForCustomer(int customerNo) {
        return repository.findFirstByCustomerNoOrderByDateDesc(customerNo);
    }
    public int count() {
        return (int) repository.count();
    }

    public List<Data> getAll() {
        return repository.findAll();
    }
    public void saveData(Data dataEntity) {
        repository.save(dataEntity);
    }
}
