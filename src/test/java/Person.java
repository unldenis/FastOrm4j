import com.github.unldenis.fastorm4j.ann.*;

public record Person(
        @Column(required = true) String name,
        int age
) {

}