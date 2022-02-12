package dataaccess;

import java.util.List;
import java.util.Optional;

/**
 * @param <T> steht für ein Entity
 * @param <I> steht für eine ID
 */
public interface BaseRepository<T,I> {

    //alle Repositories müssen inserten können mit einem beliebigen Typ T (repräsentiert ein Entity)
    Optional<T> insert(T entity);
    Optional<T> getById(I id);
    List<T> getAll();
    Optional<T> update(T entity);
    boolean deleteById(I id);
}
