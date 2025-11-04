package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.marketing.CollectionResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.MarketingMapper;
import com.commerce.e_commerce.repository.marketing.CollectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional(readOnly = true)
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepo;
    private final MarketingMapper mapper;

    @Override
    public CollectionResponse getBySlug(String slug) {
        var c = collectionRepo.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new ApiException("COLLECTION_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toCollectionResponse(c);
    }

    @Override
    public List<CollectionResponse> getAll() {
        var entities = collectionRepo.findAllByDeletedFalse(Sort.by("name").ascending());
        // mapper'da liste map'i yoksa stream ile dönüştür:
        return mapper.toCollectionResponseList(entities);
    }
}
