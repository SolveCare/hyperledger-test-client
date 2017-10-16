package care.solve.backend.transformer;

public interface ProtoTransformer<S,T> {

    T transformToProto(S obj);

    S transformFromProto(T proto);

}
