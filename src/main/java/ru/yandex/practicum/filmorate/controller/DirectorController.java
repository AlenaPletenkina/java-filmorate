package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import jakarta.validation.Valid;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.findAll();
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director user) {
        return directorService.create(user);
    }

    @PutMapping
    public Director put(@Valid @RequestBody Director user) {
        return directorService.update(user);
    }

    @GetMapping("{id}")
    public Director getById(@PathVariable Integer id) {
        return directorService.findDirectorById(id);
    }

    @DeleteMapping("{id}")
    public void deleteDirectorById(@PathVariable Integer id) {
        directorService.deleteDirectorByID(id);
    }
}