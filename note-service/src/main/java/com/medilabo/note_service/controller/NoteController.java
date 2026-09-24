package com.medilabo.note_service.controller;

import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.service.NoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/patient/{patId}")
    public List<Note> getAllNotesByPatient(@PathVariable Long patId) {
        return noteService.findNotesByPatientId(patId);
    }

    @PostMapping
    public Note addNote(@RequestBody Note note) {
        return noteService.addNote(note);
    }




}
