package com.zobicfilip.springjwtv2.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.util.List;


/**
 * required since default PageImpl is impossible to be constructed with json mapper
 */
@Validated
@Builder
public record PageResponseDTO<T>
    (@NotNull List<T> content,
    @NotNull @Valid PageResponseDTO.PageableResponseDTO pageable,
    @NotNull Boolean last,
    @NotNull Integer totalPages,
    @NotNull Integer totalElements,
    @NotNull Boolean first,
    @NotNull Integer size,
    @NotNull Integer number,
    @NotNull @Valid PageResponseDTO.SortResponseDTO sort,
    @NotNull Integer numberOfElements,
    @NotNull Boolean empty) {

    public record PageableResponseDTO
        (@NotNull PageResponseDTO.SortResponseDTO sort,
        @NotNull Integer offset,
        @NotNull Integer pageNumber,
        @NotNull Integer pageSize,
        @NotNull Boolean paged,
        @JsonProperty("unpaged")
        @NotNull Boolean unPaged) {}
    

    public record SortResponseDTO(@NotNull Boolean empty,
                                  @NotNull Boolean unsorted,
                                  @NotNull Boolean sorted) {}
}
