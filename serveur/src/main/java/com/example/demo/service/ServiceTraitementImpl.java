package com.example.demo.service;

// Ces imports dépendent du nom de votre package défini dans le fichier .proto !
import com.exemple.grpc.stub.FichierRequest;
import com.exemple.grpc.stub.FichierResponse;
import com.exemple.grpc.stub.ServiceTraitementGrpc;

import com.example.demo.entity.LogFichier;
import com.example.demo.repository.LogFichierRepository;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@GrpcService
public class ServiceTraitementImpl extends ServiceTraitementGrpc.ServiceTraitementImplBase {

    @Autowired
    private LogFichierRepository logRepository;

    @Value("${app.upload.dir:./fichiers_recus/}")
    private String uploadDir;

    @Override
    public void traiterEtEnregistrer(FichierRequest request, StreamObserver<FichierResponse> responseObserver) {
        String nomFichier = request.getNomFichier();
        byte[] contenuOctets = request.getContenu().toByteArray(); // Récupère le contenu envoyé par C++

        LogFichier log = new LogFichier();
        log.setNomFichier(nomFichier);

        try {
            // 1. Création du dossier s'il n'existe pas
            File dossier = new File(uploadDir);
            if (!dossier.exists()) {
                dossier.mkdirs();
            }

            // 2. Écriture du fichier sur le disque local
            File fichierCible = new File(dossier, nomFichier);
            try (FileOutputStream fos = new FileOutputStream(fichierCible)) {
                fos.write(contenuOctets);
            }

            // 3. Enregistrement du succès dans MySQL
            log.setStatut("SUCCES");
            log.setMessage("Fichier enregistré dans : " + fichierCible.getAbsolutePath());
            LogFichier logSauvegarde = logRepository.save(log);

            // 4. Construction et envoi de la réponse gRPC vers C++
            FichierResponse response = FichierResponse.newBuilder()
                    .setSucces(true)
                    .setMessage("Fichier écrit avec succès sur le serveur Java.")
                    .setIdMysql(logSauvegarde.getId())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IOException e) {
            // Enregistrement de l'échec dans MySQL
            log.setStatut("ERREUR");
            log.setMessage("Erreur I/O : " + e.getMessage());
            logRepository.save(log);

            // Réponse d'erreur vers C++
            FichierResponse response = FichierResponse.newBuilder()
                    .setSucces(false)
                    .setMessage("Erreur lors de l'enregistrement du fichier : " + e.getMessage())
                    .setIdMysql(-1)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}