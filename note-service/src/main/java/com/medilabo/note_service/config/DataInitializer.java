package com.medilabo.note_service.config;

import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(NoteRepository noteRepository) {

        return args -> {
            if (noteRepository.count() == 0) {

                Note note1 = new Note();
                note1.setPatId(1L);
                note1.setPatient("TestNone");
                note1.setNote("Le patient déclare qu'il 'se sent très bien'\n" +
                        "Poids égal ou inférieur au poids recommandé");
                noteRepository.save(note1);

                Note note2 = new Note();
                note2.setPatId(2L);
                note2.setPatient("TestBorderline");
                note2.setNote("Le patient déclare qu'il ressent beaucoup de stress au travail\n" +
                        "Il se plaint également que son audition est anormale dernièrement");
                noteRepository.save(note2);

                Note note3 = new Note();
                note3.setPatId(2L);
                note3.setPatient("TestBorderline");
                note3.setNote("Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois\n" +
                        "Il remarque également que son audition continue d'être anormale");
                noteRepository.save(note3);

                Note note4 = new Note();
                note4.setPatId(3L);
                note4.setPatient("TestInDanger");
                note4.setNote("Le patient déclare qu'il fume depuis peu");
                noteRepository.save(note4);

                Note note5 = new Note();
                note5.setPatId(3L);
                note5.setPatient("TestInDanger");
                note5.setNote("Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière\n" +
                        "Il se plaint également de crises d’apnée respiratoire anormales\n" +
                        "Tests de laboratoire indiquant un taux de cholestérol LDL élevé");
                noteRepository.save(note5);

                Note note6 = new Note();
                note6.setPatId(4L);
                note6.setPatient("TestEarlyOnset");
                note6.setNote("Le patient déclare qu'il lui est devenu difficile de monter les escaliers\n" +
                        "Il se plaint également d’être essoufflé\n" +
                        "Tests de laboratoire indiquant que les anticorps sont élevés\n" +
                        "Réaction aux médicaments");
                noteRepository.save(note6);

                Note note7 = new Note();
                note7.setPatId(4L);
                note7.setPatient("TestEarlyOnset");
                note7.setNote("Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps");
                noteRepository.save(note7);

                Note note8 = new Note();
                note8.setPatId(4L);
                note8.setPatient("TestEarlyOnset");
                note8.setNote("Le patient déclare avoir commencé à fumer depuis peu\n" +
                        "Hémoglobine A1C supérieure au niveau recommandé");
                noteRepository.save(note8);

                Note note9 = new Note();
                note9.setPatId(4L);
                note9.setPatient("TestEarlyOnset");
                note9.setNote("Taille, Poids, Cholestérol, Vertige et Réaction");
                noteRepository.save(note9);
            }
        };
    }
}
