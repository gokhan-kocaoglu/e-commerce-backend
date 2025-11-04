package com.commerce.e_commerce.repository.content;

import com.commerce.e_commerce.domain.marketing.EditorsPick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EditorsPickRepository extends JpaRepository<EditorsPick, UUID> {}
