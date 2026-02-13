package com.kriliCar.services.interfaces;

import com.kriliCar.dtos.BrandDTO;
import java.util.List;

public interface BrandService {

    /**
     * Crée une nouvelle marque.
     * @param brandDTO Les données de la marque à créer.
     * @return La marque créée avec son code unique.
     */
    BrandDTO createBrand(BrandDTO brandDTO);

    /**
     * Met à jour une marque existante via son code unique.
     * @param code Le code métier de la marque.
     * @param brandDTO Les nouvelles données.
     * @return Le DTO mis à jour.
     */
    BrandDTO updateBrand(String code, BrandDTO brandDTO);

    /**
     * Supprime une marque via son code unique.
     * @param code Le code métier de la marque.
     */
    void deleteBrand(String code);

    /**
     * Récupère la liste de toutes les marques.
     * @return Liste de BrandDTO.
     */
    List<BrandDTO> getAllBrands();

    /**
     * Récupère une marque spécifique via son code unique.
     * @param code Le code métier de la marque.
     * @return Le DTO de la marque.
     */
    BrandDTO getBrandById(String code);
}