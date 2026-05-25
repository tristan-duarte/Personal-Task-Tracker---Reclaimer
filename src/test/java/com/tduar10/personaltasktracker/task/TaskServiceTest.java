package com.tduar10.personaltasktracker.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)   // wires Mockito into JUnit 5
class TaskServiceTest {

    @Mock                              // a fake repo
    private TaskRepository repository;

    @InjectMocks                       // creates a TaskService, injects the mock above into it
    private TaskService service;

    @Test
    void findAll_shouldReturnAllTasks() {
        // Arrange: tell the mock what to return when findAll() is called
        Task t1 = new Task();
        Task t2 = new Task();
        when(repository.findAll()).thenReturn(List.of(t1, t2));

        // Act
        List<Task> result = service.findAll(null, null);

        // Assert
        assertEquals(2, result.size());
        verify(repository).findAll();   // confirm the repo method was actually called
    }

    @Test
    void findAll_shouldFilterByStatus_whenStatusProvided() {
        Task done = new Task();
        when(repository.findByStatus(TaskStatus.DONE)).thenReturn(List.of(done));

        List<Task> result = service.findAll(TaskStatus.DONE, null);

        assertEquals(1, result.size());
        verify(repository).findByStatus(TaskStatus.DONE);
        verify(repository, never()).findAll();
    }

    @Test
    void findById_shouldReturnTask_whenIdExists() {
        Task task = new Task();
        task.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        Task result = service.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findById_shouldThrowException_whenIdDoesNotExist() {
        // Mock returns empty — simulating "not found"
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // assertThrows verifies the method throws the expected exception type
        assertThrows(TaskNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void create_shouldReturnSavedTask() {
        Task input = new Task();
        input.setTitle("New task");
        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("New task");
        when(repository.save(input)).thenReturn(saved);

        Task result = service.create(input);

        assertEquals(1L, result.getId());
        verify(repository).save(input);
    }

    @Test
    void update_shouldThrowException_whenTaskNotFound() {
        Task data = new Task();
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.update(999L, data));
    }

    @Test
    void delete_shouldThrowException_whenTaskNotFound() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> service.delete(999L));
        verify(repository, never()).deleteById(any());  // confirm delete was NOT called
    }
}