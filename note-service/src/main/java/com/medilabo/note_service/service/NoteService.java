package com.medilabo.note_service.service;

import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> findNotesByPatientId(Long patId) {
        return noteRepository.findByPatId(patId);
    }

    public Note addNote(Note note) {
        return noteRepository.save(note);
    }
}
