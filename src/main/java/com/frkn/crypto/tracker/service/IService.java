package com.frkn.crypto.tracker.service;

import com.frkn.crypto.tracker.model.PortfolioEntry;

import java.util.List;
import java.util.Optional;

public interface IService<T, S> {

    T save(T entity);

    Optional<T> findById(S id);

    Iterable<T> findAll();

    void deleteById(S id);

    List<PortfolioEntry> findAllByRecordLimit(Integer limit);

    Optional<PortfolioEntry> findAny();


}
