package com.tduar10.personaltasktracker.task;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll(TaskStatus status, TaskPriority priority) {
        if (status != null) {
            return repository.findByStatus(status);
        }
        if (priority != null) {
            return repository.findByPriority(priority);
        }
        return repository.findAll();
    }

    public Task findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(Task task) {
        return repository.save(task);
    }

    public Task update(Long id, Task taskData) {
        Task existing = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        existing.setTitle(taskData.getTitle());
        existing.setDescription(taskData.getDescription());
        existing.setPriority(taskData.getPriority());
        existing.setDueDate(taskData.getDueDate());
        existing.setStatus(taskData.getStatus());

        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
