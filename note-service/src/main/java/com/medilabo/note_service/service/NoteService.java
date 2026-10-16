package com.medilabo.note_service.service;

import com.medilabo.note_service.exception.NoteNotFoundException;
import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service métier chargé de la gestion des notes médicales.
 *
 * Il permet de consulter les notes associées à un patient,
 * d'enregistrer de nouvelles notes et de modifier les notes existantes.
 *
 * Les opérations de persistance sont déléguées à NoteRepository,
 * qui assure les interactions avec la base de données MongoDB.
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> findNotesByPatientId(Long patId) {
        return noteRepository.findByPatId(patId);
    }

    public Note addNote(Note note) {
        return noteRepository.save(note);
    }


    /**
     * Met à jour une note médicale existante.
     *
     * Recherche la note à partir de son identifiant, puis actualise
     * ses informations avant d'enregistrer les modifications.
     *
     * @param id identifiant de la note à modifier
     * @param note nouvelles informations de la note
     * @return la note mise à jour
     * @throws NoteNotFoundException si aucune note ne correspond à l'identifiant
     */
    public Note updateNote(String id, Note note) {

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(
                        "Aucune note ne correspond à cet id"));

        existingNote.setPatId(note.getPatId());
        existingNote.setPatient(note.getPatient());
        existingNote.setNote(note.getNote());

        return noteRepository.save(existingNote);
    }
}
