package com.kriliCar.enums;

/**
 * Énumération définissant les rôles officiels de l'application KriliCar.
 * Le préfixe "ROLE_" est indispensable pour l'intégration native avec Spring Security.
 */
public enum Role {
    ADMIN,   // Super-utilisateur (gestion système, utilisateurs et flotte globale)
    COMPANY, // Agence de location (gestion de ses propres voitures et réservations)
    CLIENT   // Utilisateur final (consultation et réservation de véhicules)
}