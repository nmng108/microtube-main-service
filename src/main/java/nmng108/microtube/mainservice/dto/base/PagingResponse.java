package nmng108.microtube.mainservice.dto.base;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagingResponse<T> {
    int current;
    int size;
    int totalPages;
    long totalRecords;
    Collection<T> dataset;

    public PagingResponse(Collection<T> dataset) {
        this(1, 1, dataset.size(), dataset);
    }

    public PagingResponse(PagingRequest pagingRequest, long totalRecords, Collection<T> dataset) {
        this(pagingRequest.getPage(), (int) Math.ceil((double) totalRecords / pagingRequest.getSize()), totalRecords, dataset);
    }

    public PagingResponse(int current, int totalPages, long totalRecords, Collection<T> dataset) {
        this.current = current;
        this.size = dataset.size();
        this.totalPages = totalPages;
        this.totalRecords = totalRecords;
        this.dataset = dataset;
    }

    /**
     * Note that this constructor does remove duplicate records from the result list
     */
    public <S> PagingResponse(Page<S> page, Function<S, T> mapper) {
        this.current = page.getNumber() + 1;
        this.size = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.totalRecords = page.getTotalElements();
        this.dataset = page.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T> PagingResponse<T> notPaginated(Collection<T> dataset) {
        return new PagingResponse<>(dataset);
    }

    public static <S, T> PagingResponse<T> from(Page<S> page, Function<S, T> mapper) {
        return new PagingResponse<>(page, mapper);
    }
}