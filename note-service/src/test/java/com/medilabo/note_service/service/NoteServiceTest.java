package com.medilabo.note_service.service;

import com.medilabo.note_service.exception.NoteNotFoundException;
import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    //private NoteNotFoundException noteNotFoundException;

    @Test
    void findNotesByPatientId_shouldReturnPatientNotes() {

        // ARRANGE
        Note note1 = new Note();
        note1.setPatId(1L);
        note1.setNote("Première Note");

        Note note2 = new Note();
        note2.setPatId(1L);
        note2.setNote("Deuxième note");

        when(noteRepository.findByPatId(1L)).thenReturn(List.of(note1, note2));

        // ACT
        List<Note> result = noteService.findNotesByPatientId(1L);

        // ASSERT
        assertEquals(2, result.size());
        verify(noteRepository).findByPatId(1L);
    }

    @Test
    void addNote_shouldAddNote() {

        // ARRANGE
        Note note = new Note();
        note.setId("1");

        when(noteRepository.save(note)).thenReturn(note);

        // ACT
        Note result = noteService.addNote(note);

        // ASSERT
        assertEquals("1", result.getId());
        verify(noteRepository).save(note);
    }

    @Test
    void updateNote_shouldReturnUpdatedNote() {

        // ARRANGE
        Note existingNote = new Note();
        existingNote.setId("1");
        existingNote.setNote("Existing");

        Note updated = new Note();
        updated.setNote("Updated");

        when(noteRepository.findById("1")).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);

        // ACT
        Note result = noteService.updateNote("1", updated);

        // ASSERT
        assertEquals("Updated", result.getNote());
        verify(noteRepository).findById("1");
        verify(noteRepository).save(existingNote);
    }

    @Test
    void updatedNote_shouldThrowExceptionWhenNoteDoesNotExist() {

        // ARRANGE
        Note updatedNote = new Note();
        updatedNote.setNote("Updated");

        when(noteRepository.findById("99")).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(NoteNotFoundException.class,
                () -> noteService.updateNote("99", updatedNote));

        verify(noteRepository).findById("99");
        verify(noteRepository, never()).save(any(Note.class));
    }

}
