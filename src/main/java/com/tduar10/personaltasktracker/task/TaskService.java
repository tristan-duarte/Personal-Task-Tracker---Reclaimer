package com.tduar10.personaltasktracker.task;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final  TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

    public Task findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(Task task) {
        return repository.save(task);
    }
}
