package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.dao.director.DirectorStorage;

import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public Director create(Director director) {
        log.info("Добавляем режиссера в коллекцию");
        if (director.getName().isBlank()) {
            throw new ValidationException("Имя директора не может быть пустым");
        }
        return directorStorage.save(director);
    }

    @Override
    public Director findDirectorById(Integer id) {
        Director director = directorStorage.findDirectorById(id);
        if (isNull(director)) {
            throw new NotFoundException("Режиссер с таким id не существует");
        }
        return director;
    }

    @Override
    public Director update(Director director) {
        log.info("Обновляем режиссера в коллекции");
        findDirectorById(director.getId());
        return directorStorage.update(director);
    }

    @Override
    public Collection<Director> findAll() {
        log.info("Выводим список всех режиссеров");
        return directorStorage.findAll();
    }

    @Override
    public void deleteDirectorByID(Integer id) {
        log.info(String.format("Удаляем режиссера с id: %s", id));
        if (!directorStorage.deleteDirector(id))
            throw new NotFoundException("Режиссер с таким id отсутствует в базе");
    }

    @Override
    public void setDirector(Integer idFilm, Integer idDirector) {
        directorStorage.setDirectors(idFilm, idDirector);
        log.info("Добавил режисера {} к фильму {}", idDirector, idFilm);
    }

    @Override
    public List<Director> getFilmDirectors(Integer filmId) {
        log.info("Приступаю к поиску жанров для фильма {}", filmId);
        List<Director> filmDirectors = directorStorage.getFilmDirectors(filmId);
        log.info("Нашел {} жанров к фильму", filmDirectors.size());
        return filmDirectors;
    }

    @Override
    public void clearFilmDirectors(Integer filmId) {
        directorStorage.clearFilmDirectors(filmId);
    }
}