import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class RequestProcessor {

    public <A, B, C, D extends Result> D process(
             A req,
            Class<D> dClass,
            Function<A, B> preMap,
            Function<B, C> proccess,
            Function<C, D> postMap,
            BiFunction<Supplier<D>, Class<D>, D> context) {
        Supplier<D> pipeline = () -> preMap.andThen(proccess)
                .andThen(postMap).apply(req);
        return context.apply(pipeline, dClass);
    }


    public <A, D extends Result > D defaultProc(A req, Class<D> dClass, Function<A, D> method) {
        BiFunction<Supplier<D>, Class<D>, D> defaultContext = (pipeline, clazz) -> {
            try {
                return pipeline.get();
            } catch (Exception e) {
                D instance = null;
                try {
                    instance = clazz.newInstance();
                    instance.setMes(e.getMessage());
                } catch (InstantiationException | IllegalAccessException ee) {
                    e.printStackTrace();
                }
                return instance;
            }
        };
        return process(
                req,
                dClass,
                Function.identity(),
                method,
                Function.identity(),
                defaultContext
        );
    }
}


class Result{
    private String mes;

    public void setMes(String mes) {
        this.mes = mes;
    }
}