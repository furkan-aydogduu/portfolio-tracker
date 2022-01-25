package com.frkn.crypto.tracker.service;

import com.frkn.crypto.tracker.model.PortfolioEntry;
import com.frkn.crypto.tracker.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService implements IService<PortfolioEntry, Long> {

    private PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public PortfolioEntry save(PortfolioEntry entity) {
        return this.portfolioRepository.save(entity);
    }

    @Override
    public Optional<PortfolioEntry> findById(Long id) {
        return this.portfolioRepository.findById(id);
    }

    @Override
    public Iterable<PortfolioEntry> findAll() {
        return this.portfolioRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        this.portfolioRepository.deleteById(id);
    }

    @Override
    public List<PortfolioEntry> findAllByRecordLimit(Integer limit){
        Pageable maxRecordCount = PageRequest.of(0, limit);
        return this.portfolioRepository.findAll(maxRecordCount).getContent();
    }

    @Override
    public Optional<PortfolioEntry> findAny(){
        Pageable maxRecordCount = PageRequest.of(0, 1);
        return this.portfolioRepository.findAll(maxRecordCount).stream().limit(1).findAny();
    }
}
