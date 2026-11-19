package com.pushkar.ordermatchingengine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pushkar.ordermatchingengine.entity.TradeEntity;

public interface TradeRepository extends JpaRepository<TradeEntity, String> {
    List<TradeEntity> findBySymbolOrderByExecutedAtDesc(String symbol);
}
