package gr.uowm.deyakwebapp.data.service;


import gr.uowm.deyakwebapp.data.entity.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DataRepository
        extends
        JpaRepository<Data, Long>,
        JpaSpecificationExecutor<Data> {
    Optional<Data> findFirstByCustomerNoOrderByDateDesc(int customerNo);


}