package com.example.demo.service;

import com.example.demo.entity.Vente; // Votre entité JPA Vente
import com.example.demo.repository.VenteRepository; // Votre dépôt JPA
import com.exemple.grpc.stub.ServiceTraitementGrpc;
import com.exemple.grpc.stub.VenteRequest;
import com.exemple.grpc.stub.VenteResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@GrpcService
public class ServiceTraitementImpl extends ServiceTraitementGrpc.ServiceTraitementImplBase {

    @Autowired
    private VenteRepository venteRepository;

    @Override
    public void enregistrerVente(VenteRequest request, StreamObserver<VenteResponse> responseObserver) {
        try {
            // 1. Extraction des données envoyées par le client C++
            String produit = request.getProduit();
            int quantite = request.getQuantite();
            double prixUnitaire = request.getPrixUnitaire();

            // 2. Enregistrement en base de données MySQL via JPA
            Vente vente = new Vente();
            vente.setProduit(produit);
            vente.setQuantite(quantite);
            vente.setPrixUnitaire(prixUnitaire);
            vente.setDateVente(LocalDateTime.now());

            Vente venteSauvegardee = venteRepository.save(vente);

            // 3. Préparation de la réponse de succès
            VenteResponse response = VenteResponse.newBuilder()
                    .setSucces(true)
                    .setMessage("Vente enregistrée avec succès !")
                    .setIdVente(venteSauvegardee.getId())
                    .build();

            // 4. Envoi de la réponse au client gRPC
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            // En cas d'erreur
            VenteResponse response = VenteResponse.newBuilder()
                    .setSucces(false)
                    .setMessage("Erreur lors de l'enregistrement : " + e.getMessage())
                    .setIdVente(-1)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}