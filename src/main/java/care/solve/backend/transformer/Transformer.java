package care.solve.backend.transformer;

import java.util.List;
import java.util.stream.Collectors;

public interface Transformer<S, T> {

    T transform(S entity);

    default List<T> transformList(List<S> entities) {
        return entities.stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }
}
