package com.hood.merch.models.repo;

import com.hood.merch.models.Offer;
import com.hood.merch.models.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OfferRepository extends CrudRepository <Offer, Long> {
    Optional<Offer> findById(int i);
}
